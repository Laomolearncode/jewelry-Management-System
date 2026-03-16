package com.jewelry.pims.service.impl;

import com.jewelry.pims.common.BusinessException;
import com.jewelry.pims.domain.MasterEntities;
import com.jewelry.pims.dto.inventory.InventoryDtos;
import com.jewelry.pims.dto.master.MasterDtos;
import com.jewelry.pims.mapper.MasterDataMapper;
import com.jewelry.pims.service.MasterDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterDataServiceImpl implements MasterDataService {

    private final MasterDataMapper masterDataMapper;

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

    @Override
    public List<MasterEntities.Material> listMaterials() {
        return masterDataMapper.listMaterials();
    }

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

    @Override
    public List<MasterEntities.ProductCategory> listCategories() {
        return masterDataMapper.listCategories();
    }

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

    @Override
    public List<MasterEntities.Supplier> listSuppliers() {
        return masterDataMapper.listSuppliers();
    }

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

    @Override
    public List<MasterEntities.Customer> listCustomers() {
        return masterDataMapper.listCustomers();
    }

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

    @Override
    public List<MasterEntities.Warehouse> listWarehouses() {
        return masterDataMapper.listWarehouses();
    }

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

    @Override
    public List<MasterDtos.ProductView> listProducts() {
        return masterDataMapper.listProducts();
    }

    @Override
    public List<InventoryDtos.StockView> listStocks() {
        return masterDataMapper.listStocks();
    }
}
