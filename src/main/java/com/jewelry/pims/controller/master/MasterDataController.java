package com.jewelry.pims.controller.master;

import com.jewelry.pims.common.ApiResponse;
import com.jewelry.pims.common.PageResult;
import com.jewelry.pims.domain.MasterEntities;
import com.jewelry.pims.dto.inventory.InventoryDtos;
import com.jewelry.pims.dto.master.MasterDtos;
import com.jewelry.pims.security.Permission;
import com.jewelry.pims.service.MasterDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/master")
@RequiredArgsConstructor
/**
 * 基础资料与库存查询接口。
 */
public class MasterDataController {

    private final MasterDataService masterDataService;

    /**
     * 新增材质字典。
     */
    @PostMapping("/materials")
    @Permission("master:material:create")
    public ApiResponse<MasterEntities.Material> createMaterial(@Valid @RequestBody MasterDtos.MaterialRequest request) {
        return ApiResponse.ok("创建成功", masterDataService.createMaterial(request));
    }

    /**
     * 查询材质列表。
     */
    @GetMapping("/materials")
    @Permission("master:material:view")
    public ApiResponse<List<MasterEntities.Material>> listMaterials() {
        return ApiResponse.ok(masterDataService.listMaterials());
    }

    /**
     * 新增商品分类。
     */
    @PostMapping("/categories")
    @Permission("master:category:create")
    public ApiResponse<MasterEntities.ProductCategory> createCategory(@Valid @RequestBody MasterDtos.CategoryRequest request) {
        return ApiResponse.ok("创建成功", masterDataService.createCategory(request));
    }

    /**
     * 查询商品分类列表。
     */
    @GetMapping("/categories")
    @Permission("master:category:view")
    public ApiResponse<List<MasterEntities.ProductCategory>> listCategories() {
        return ApiResponse.ok(masterDataService.listCategories());
    }

    /**
     * 新增供应商档案。
     */
    @PostMapping("/suppliers")
    @Permission("master:supplier:create")
    public ApiResponse<MasterEntities.Supplier> createSupplier(@Valid @RequestBody MasterDtos.SupplierRequest request) {
        return ApiResponse.ok("创建成功", masterDataService.createSupplier(request));
    }

    /**
     * 查询供应商列表。
     */
    @GetMapping("/suppliers")
    @Permission("master:supplier:view")
    public ApiResponse<List<MasterEntities.Supplier>> listSuppliers() {
        return ApiResponse.ok(masterDataService.listSuppliers());
    }

    /**
     * 新增客户档案。
     */
    @PostMapping("/customers")
    @Permission("master:customer:create")
    public ApiResponse<MasterEntities.Customer> createCustomer(@Valid @RequestBody MasterDtos.CustomerRequest request) {
        return ApiResponse.ok("创建成功", masterDataService.createCustomer(request));
    }

    /**
     * 查询客户列表。
     */
    @GetMapping("/customers")
    @Permission("master:customer:view")
    public ApiResponse<List<MasterEntities.Customer>> listCustomers() {
        return ApiResponse.ok(masterDataService.listCustomers());
    }

    /**
     * 新增仓库信息。
     */
    @PostMapping("/warehouses")
    @Permission("master:warehouse:create")
    public ApiResponse<MasterEntities.Warehouse> createWarehouse(@Valid @RequestBody MasterDtos.WarehouseRequest request) {
        return ApiResponse.ok("创建成功", masterDataService.createWarehouse(request));
    }

    /**
     * 查询仓库列表。
     */
    @GetMapping("/warehouses")
    @Permission("master:warehouse:view")
    public ApiResponse<List<MasterEntities.Warehouse>> listWarehouses() {
        return ApiResponse.ok(masterDataService.listWarehouses());
    }

    /**
     * 新增商品档案。
     */
    @PostMapping("/products")
    @Permission("master:product:create")
    public ApiResponse<MasterEntities.Product> createProduct(@Valid @RequestBody MasterDtos.ProductRequest request) {
        return ApiResponse.ok("创建成功", masterDataService.createProduct(request));
    }

    /**
     * 按条件分页查询商品列表。
     */
    @GetMapping("/products")
    @Permission("master:product:view")
    public ApiResponse<PageResult<MasterDtos.ProductView>> listProducts(MasterDtos.ProductQuery query) {
        return ApiResponse.ok(masterDataService.listProducts(query));
    }

    /**
     * 按条件分页查询库存列表。
     */
    @GetMapping("/stocks")
    @Permission("inventory:stock:view")
    public ApiResponse<PageResult<InventoryDtos.StockView>> listStocks(InventoryDtos.StockQuery query) {
        return ApiResponse.ok(masterDataService.listStocks(query));
    }

    /**
     * 分页查询低库存记录。
     */
    @GetMapping("/stocks/low")
    @Permission("inventory:stock:view")
    public ApiResponse<PageResult<InventoryDtos.StockView>> listLowStocks(InventoryDtos.StockQuery query) {
        return ApiResponse.ok(masterDataService.listLowStocks(query));
    }
}
