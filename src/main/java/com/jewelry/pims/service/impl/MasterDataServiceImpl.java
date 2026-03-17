package com.jewelry.pims.service.impl;

import com.jewelry.pims.common.BusinessException;
import com.jewelry.pims.common.PageResult;
import com.jewelry.pims.common.PageUtils;
import com.jewelry.pims.domain.MasterEntities;
import com.jewelry.pims.dto.inventory.InventoryDtos;
import com.jewelry.pims.dto.master.MasterDtos;
import com.jewelry.pims.mapper.MasterDataMapper;
import com.jewelry.pims.service.MasterDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
/**
 * 基础资料与库存查询业务实现。
 */
public class MasterDataServiceImpl implements MasterDataService {

    private final MasterDataMapper masterDataMapper;

    /**
     * 新增材质字典。
     */
    @Override
    public MasterEntities.Material createMaterial(MasterDtos.MaterialRequest request) {
        MasterEntities.Material material = new MasterEntities.Material();
        material.setMaterialCode(request.getMaterialCode());
        material.setMaterialName(request.getMaterialName());
        material.setDescription(request.getDescription());
        material.setStatus(request.getStatus());
        masterDataMapper.insertMaterial(material);
        return material;
    }

    /**
     * 查询材质列表。
     */
    @Override
    public List<MasterEntities.Material> listMaterials() {
        return masterDataMapper.listMaterials();
    }

    /**
     * 新增商品分类。
     */
    @Override
    public MasterEntities.ProductCategory createCategory(MasterDtos.CategoryRequest request) {
        MasterEntities.ProductCategory category = new MasterEntities.ProductCategory();
        category.setCategoryCode(request.getCategoryCode());
        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        category.setStatus(request.getStatus());
        masterDataMapper.insertCategory(category);
        return category;
    }

    /**
     * 查询商品分类列表。
     */
    @Override
    public List<MasterEntities.ProductCategory> listCategories() {
        return masterDataMapper.listCategories();
    }

    /**
     * 新增供应商。
     */
    @Override
    public MasterEntities.Supplier createSupplier(MasterDtos.SupplierRequest request) {
        MasterEntities.Supplier supplier = new MasterEntities.Supplier();
        supplier.setSupplierCode(request.getSupplierCode());
        supplier.setSupplierName(request.getSupplierName());
        supplier.setContactName(request.getContactName());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        supplier.setStatus(request.getStatus());
        masterDataMapper.insertSupplier(supplier);
        return supplier;
    }

    /**
     * 查询供应商列表。
     */
    @Override
    public List<MasterEntities.Supplier> listSuppliers() {
        return masterDataMapper.listSuppliers();
    }

    /**
     * 新增客户。
     */
    @Override
    public MasterEntities.Customer createCustomer(MasterDtos.CustomerRequest request) {
        MasterEntities.Customer customer = new MasterEntities.Customer();
        customer.setCustomerCode(request.getCustomerCode());
        customer.setCustomerName(request.getCustomerName());
        customer.setContactName(request.getContactName());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setStatus(request.getStatus());
        masterDataMapper.insertCustomer(customer);
        return customer;
    }

    /**
     * 查询客户列表。
     */
    @Override
    public List<MasterEntities.Customer> listCustomers() {
        return masterDataMapper.listCustomers();
    }

    /**
     * 新增仓库。
     */
    @Override
    public MasterEntities.Warehouse createWarehouse(MasterDtos.WarehouseRequest request) {
        MasterEntities.Warehouse warehouse = new MasterEntities.Warehouse();
        warehouse.setWarehouseCode(request.getWarehouseCode());
        warehouse.setWarehouseName(request.getWarehouseName());
        warehouse.setLocation(request.getLocation());
        warehouse.setStatus(request.getStatus());
        masterDataMapper.insertWarehouse(warehouse);
        return warehouse;
    }

    /**
     * 查询仓库列表。
     */
    @Override
    public List<MasterEntities.Warehouse> listWarehouses() {
        return masterDataMapper.listWarehouses();
    }

    /**
     * 新增商品档案并校验唯一约束。
     */
    @Override
    public MasterEntities.Product createProduct(MasterDtos.ProductRequest request) {
        if (masterDataMapper.countProductByCode(request.getProductCode()) > 0) {
            throw new BusinessException("商品编码已存在");
        }
        if (request.getCertificateNo() != null && !request.getCertificateNo().isBlank()
                && masterDataMapper.countByCertificateNo(request.getCertificateNo()) > 0) {
            throw new BusinessException("证书号已存在");
        }
        MasterEntities.Product product = new MasterEntities.Product();
        product.setProductCode(request.getProductCode());
        product.setProductName(request.getProductName());
        product.setCategoryId(request.getCategoryId());
        product.setMaterialId(request.getMaterialId());
        product.setBrand(request.getBrand());
        product.setUnit(request.getUnit());
        product.setWeight(request.getWeight());
        product.setCostPrice(request.getCostPrice());
        product.setSalePrice(request.getSalePrice());
        product.setCertificateNo(request.getCertificateNo());
        product.setAbcLevel(3);
        product.setWarningThreshold(request.getWarningThreshold());
        product.setStatus(request.getStatus());
        masterDataMapper.insertProduct(product);
        return product;
    }

    /**
     * 按条件分页查询商品。
     */
    @Override
    public PageResult<MasterDtos.ProductView> listProducts(MasterDtos.ProductQuery query) {
        MasterDtos.ProductQuery safeQuery = query == null ? new MasterDtos.ProductQuery() : query;
        List<MasterDtos.ProductView> filtered = masterDataMapper.listProducts().stream()
                .filter(item -> !StringUtils.hasText(safeQuery.getProductCode()) || item.getProductCode().contains(safeQuery.getProductCode()))
                .filter(item -> !StringUtils.hasText(safeQuery.getProductName()) || item.getProductName().contains(safeQuery.getProductName()))
                .filter(item -> !StringUtils.hasText(safeQuery.getCertificateNo()) || safeQuery.getCertificateNo().equals(item.getCertificateNo()))
                .filter(item -> safeQuery.getStatus() == null || safeQuery.getStatus().equals(item.getStatus()))
                .collect(Collectors.toList());
        return PageUtils.paginate(filtered, safeQuery);
    }

    /**
     * 按条件分页查询库存。
     */
    @Override
    public PageResult<InventoryDtos.StockView> listStocks(InventoryDtos.StockQuery query) {
        InventoryDtos.StockQuery safeQuery = query == null ? new InventoryDtos.StockQuery() : query;
        return PageUtils.paginate(filterStocks(safeQuery, false), safeQuery);
    }

    /**
     * 分页查询低库存记录。
     */
    @Override
    public PageResult<InventoryDtos.StockView> listLowStocks(InventoryDtos.StockQuery query) {
        InventoryDtos.StockQuery safeQuery = query == null ? new InventoryDtos.StockQuery() : query;
        return PageUtils.paginate(filterStocks(safeQuery, true), safeQuery);
    }

    private List<InventoryDtos.StockView> filterStocks(InventoryDtos.StockQuery query, boolean forceLowStockOnly) {
        boolean lowStockOnly = forceLowStockOnly || Boolean.TRUE.equals(query.getLowStockOnly());
        return masterDataMapper.listStocks().stream()
                .filter(item -> !StringUtils.hasText(query.getProductCode()) || item.getProductCode().contains(query.getProductCode()))
                .filter(item -> !StringUtils.hasText(query.getProductName()) || item.getProductName().contains(query.getProductName()))
                .filter(item -> !StringUtils.hasText(query.getBatchNo()) || query.getBatchNo().equals(item.getBatchNo()))
                .filter(item -> !StringUtils.hasText(query.getCertificateNo()) || query.getCertificateNo().equals(item.getCertificateNo()))
                .filter(item -> !lowStockOnly || item.getQuantity() <= item.getWarningThreshold())
                .collect(Collectors.toList());
    }
}
