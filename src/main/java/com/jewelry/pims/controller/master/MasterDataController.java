package com.jewelry.pims.controller.master;

import com.jewelry.pims.common.ApiResponse;
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
public class MasterDataController {

    private final MasterDataService masterDataService;

    @PostMapping("/materials")
    @Permission("master:material:create")
    public ApiResponse<MasterEntities.Material> createMaterial(@Valid @RequestBody MasterDtos.MaterialRequest request) {
        return ApiResponse.ok("创建成功", masterDataService.createMaterial(request));
    }

    @GetMapping("/materials")
    @Permission("master:material:view")
    public ApiResponse<List<MasterEntities.Material>> listMaterials() {
        return ApiResponse.ok(masterDataService.listMaterials());
    }

    @PostMapping("/categories")
    @Permission("master:category:create")
    public ApiResponse<MasterEntities.ProductCategory> createCategory(@Valid @RequestBody MasterDtos.CategoryRequest request) {
        return ApiResponse.ok("创建成功", masterDataService.createCategory(request));
    }

    @GetMapping("/categories")
    @Permission("master:category:view")
    public ApiResponse<List<MasterEntities.ProductCategory>> listCategories() {
        return ApiResponse.ok(masterDataService.listCategories());
    }

    @PostMapping("/suppliers")
    @Permission("master:supplier:create")
    public ApiResponse<MasterEntities.Supplier> createSupplier(@Valid @RequestBody MasterDtos.SupplierRequest request) {
        return ApiResponse.ok("创建成功", masterDataService.createSupplier(request));
    }

    @GetMapping("/suppliers")
    @Permission("master:supplier:view")
    public ApiResponse<List<MasterEntities.Supplier>> listSuppliers() {
        return ApiResponse.ok(masterDataService.listSuppliers());
    }

    @PostMapping("/customers")
    @Permission("master:customer:create")
    public ApiResponse<MasterEntities.Customer> createCustomer(@Valid @RequestBody MasterDtos.CustomerRequest request) {
        return ApiResponse.ok("创建成功", masterDataService.createCustomer(request));
    }

    @GetMapping("/customers")
    @Permission("master:customer:view")
    public ApiResponse<List<MasterEntities.Customer>> listCustomers() {
        return ApiResponse.ok(masterDataService.listCustomers());
    }

    @PostMapping("/warehouses")
    @Permission("master:warehouse:create")
    public ApiResponse<MasterEntities.Warehouse> createWarehouse(@Valid @RequestBody MasterDtos.WarehouseRequest request) {
        return ApiResponse.ok("创建成功", masterDataService.createWarehouse(request));
    }

    @GetMapping("/warehouses")
    @Permission("master:warehouse:view")
    public ApiResponse<List<MasterEntities.Warehouse>> listWarehouses() {
        return ApiResponse.ok(masterDataService.listWarehouses());
    }

    @PostMapping("/products")
    @Permission("master:product:create")
    public ApiResponse<MasterEntities.Product> createProduct(@Valid @RequestBody MasterDtos.ProductRequest request) {
        return ApiResponse.ok("创建成功", masterDataService.createProduct(request));
    }

    @GetMapping("/products")
    @Permission("master:product:view")
    public ApiResponse<List<MasterDtos.ProductView>> listProducts() {
        return ApiResponse.ok(masterDataService.listProducts());
    }

    @GetMapping("/stocks")
    @Permission("inventory:stock:view")
    public ApiResponse<List<InventoryDtos.StockView>> listStocks() {
        return ApiResponse.ok(masterDataService.listStocks());
    }
}
