package com.jewelry.pims.service;

import com.jewelry.pims.domain.MasterEntities;
import com.jewelry.pims.dto.inventory.InventoryDtos;
import com.jewelry.pims.dto.master.MasterDtos;

import java.util.List;

public interface MasterDataService {

    MasterEntities.Material createMaterial(MasterDtos.MaterialRequest request);

    List<MasterEntities.Material> listMaterials();

    MasterEntities.ProductCategory createCategory(MasterDtos.CategoryRequest request);

    List<MasterEntities.ProductCategory> listCategories();

    MasterEntities.Supplier createSupplier(MasterDtos.SupplierRequest request);

    List<MasterEntities.Supplier> listSuppliers();

    MasterEntities.Customer createCustomer(MasterDtos.CustomerRequest request);

    List<MasterEntities.Customer> listCustomers();

    MasterEntities.Warehouse createWarehouse(MasterDtos.WarehouseRequest request);

    List<MasterEntities.Warehouse> listWarehouses();

    MasterEntities.Product createProduct(MasterDtos.ProductRequest request);

    List<MasterDtos.ProductView> listProducts();

    List<InventoryDtos.StockView> listStocks();
}
