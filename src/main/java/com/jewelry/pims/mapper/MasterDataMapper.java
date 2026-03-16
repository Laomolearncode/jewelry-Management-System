package com.jewelry.pims.mapper;

import com.jewelry.pims.domain.MasterEntities;
import com.jewelry.pims.dto.inventory.InventoryDtos;
import com.jewelry.pims.dto.master.MasterDtos;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MasterDataMapper {

    @Select("select id, material_code, material_name, description, status from material_dict order by id desc")
    List<MasterEntities.Material> listMaterials();

    @Insert("insert into material_dict(material_code, material_name, description, status) values(#{materialCode}, #{materialName}, #{description}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertMaterial(MasterEntities.Material material);

    @Select("select id, category_code, category_name, description, status from product_category order by id desc")
    List<MasterEntities.ProductCategory> listCategories();

    @Insert("insert into product_category(category_code, category_name, description, status) values(#{categoryCode}, #{categoryName}, #{description}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertCategory(MasterEntities.ProductCategory category);

    @Select("select id, supplier_code, supplier_name, contact_name, phone, address, status from supplier order by id desc")
    List<MasterEntities.Supplier> listSuppliers();

    @Insert("insert into supplier(supplier_code, supplier_name, contact_name, phone, address, status) values(#{supplierCode}, #{supplierName}, #{contactName}, #{phone}, #{address}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertSupplier(MasterEntities.Supplier supplier);

    @Select("select id, customer_code, customer_name, contact_name, phone, address, status from customer order by id desc")
    List<MasterEntities.Customer> listCustomers();

    @Insert("insert into customer(customer_code, customer_name, contact_name, phone, address, status) values(#{customerCode}, #{customerName}, #{contactName}, #{phone}, #{address}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertCustomer(MasterEntities.Customer customer);

    @Select("select id, warehouse_code, warehouse_name, location, status from warehouse order by id desc")
    List<MasterEntities.Warehouse> listWarehouses();

    @Insert("insert into warehouse(warehouse_code, warehouse_name, location, status) values(#{warehouseCode}, #{warehouseName}, #{location}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertWarehouse(MasterEntities.Warehouse warehouse);

    @Select("select count(1) from product where product_code = #{productCode}")
    int countProductByCode(String productCode);

    @Select("select count(1) from product where certificate_no = #{certificateNo} and certificate_no is not null and certificate_no != ''")
    int countByCertificateNo(String certificateNo);

    @Insert("""
            insert into product(product_code, product_name, category_id, material_id, brand, unit, weight, cost_price, sale_price, certificate_no, abc_level, warning_threshold, status, created_at, updated_at)
            values(#{productCode}, #{productName}, #{categoryId}, #{materialId}, #{brand}, #{unit}, #{weight}, #{costPrice}, #{salePrice}, #{certificateNo}, #{abcLevel}, #{warningThreshold}, #{status}, now(), now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertProduct(MasterEntities.Product product);

    @Select("""
            select p.id, p.product_code, p.product_name, c.category_name, m.material_name, p.brand, p.unit, p.weight, p.cost_price, p.sale_price, p.certificate_no, p.abc_level, p.status, p.created_at
            from product p
            left join product_category c on c.id = p.category_id
            left join material_dict m on m.id = p.material_id
            order by p.id desc
            """)
    List<MasterDtos.ProductView> listProducts();

    @Select("""
            select id, product_code, product_name, category_id, material_id, brand, unit, weight, cost_price, sale_price, certificate_no, abc_level, status, created_at, updated_at
            from product where id = #{id}
            """)
    MasterEntities.Product findProductById(Long id);

    @Select("select warehouse_name from warehouse where id = #{id}")
    String getWarehouseName(Long id);

    @Select("select supplier_name from supplier where id = #{id}")
    String getSupplierName(Long id);

    @Select("select customer_name from customer where id = #{id}")
    String getCustomerName(Long id);

    @Select("select product_name from product where id = #{id}")
    String getProductName(Long id);

    @Select("""
            select id, warehouse_id, product_id, batch_no, certificate_no, quantity, total_weight, avg_cost_price, warning_threshold, updated_at
            from stock
            where warehouse_id = #{warehouseId}
              and product_id = #{productId}
              and ifnull(batch_no, '') = ifnull(#{batchNo}, '')
              and ifnull(certificate_no, '') = ifnull(#{certificateNo}, '')
            limit 1
            """)
    MasterEntities.Stock findStockByKey(@Param("warehouseId") Long warehouseId,
                                        @Param("productId") Long productId,
                                        @Param("batchNo") String batchNo,
                                        @Param("certificateNo") String certificateNo);

    @Select("""
            select id, warehouse_id, product_id, batch_no, certificate_no, quantity, total_weight, avg_cost_price, warning_threshold, updated_at
            from stock where id = #{id}
            """)
    MasterEntities.Stock findStockById(Long id);

    @Insert("""
            insert into stock(warehouse_id, product_id, batch_no, certificate_no, quantity, total_weight, avg_cost_price, warning_threshold, updated_at)
            values(#{warehouseId}, #{productId}, #{batchNo}, #{certificateNo}, #{quantity}, #{totalWeight}, #{avgCostPrice}, #{warningThreshold}, now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertStock(MasterEntities.Stock stock);

    @Update("""
            update stock set quantity = #{quantity}, total_weight = #{totalWeight}, avg_cost_price = #{avgCostPrice}, warning_threshold = #{warningThreshold}, updated_at = now()
            where id = #{id}
            """)
    int updateStock(MasterEntities.Stock stock);

    @Select("""
            select s.id, s.warehouse_id, w.warehouse_name, s.product_id, p.product_code, p.product_name, s.batch_no, s.certificate_no, s.quantity, s.total_weight, s.avg_cost_price, s.warning_threshold, s.updated_at
            from stock s
            left join warehouse w on w.id = s.warehouse_id
            left join product p on p.id = s.product_id
            order by s.updated_at desc
            """)
    List<InventoryDtos.StockView> listStocks();

    @Select("select count(1) from stock where quantity <= warning_threshold")
    int countLowStock();

    @Select("select count(1) from product")
    int countProducts();
}
