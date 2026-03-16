package com.jewelry.pims.mapper;

import com.jewelry.pims.domain.BusinessEntities;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PurchaseMapper {

    @Insert("""
            insert into purchase_order(order_no, supplier_id, status, total_amount, approved_by, approved_at, created_by, created_at, updated_at)
            values(#{orderNo}, #{supplierId}, #{status}, #{totalAmount}, #{approvedBy}, #{approvedAt}, #{createdBy}, now(), now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrder(BusinessEntities.PurchaseOrder order);

    @Insert("""
            insert into purchase_order_item(order_id, product_id, warehouse_id, batch_no, certificate_no, quantity, weight, unit_price, subtotal_amount)
            values(#{orderId}, #{productId}, #{warehouseId}, #{batchNo}, #{certificateNo}, #{quantity}, #{weight}, #{unitPrice}, #{subtotalAmount})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertItem(BusinessEntities.PurchaseOrderItem item);

    @Select("""
            select id, order_no, supplier_id, status, total_amount, approved_by, approved_at, created_by, created_at, updated_at
            from purchase_order
            order by id desc
            """)
    List<BusinessEntities.PurchaseOrder> listOrders();

    @Select("""
            select id, order_no, supplier_id, status, total_amount, approved_by, approved_at, created_by, created_at, updated_at
            from purchase_order
            where id = #{id}
            """)
    BusinessEntities.PurchaseOrder findOrderById(Long id);

    @Select("""
            select id, order_id, product_id, warehouse_id, batch_no, certificate_no, quantity, weight, unit_price, subtotal_amount
            from purchase_order_item
            where order_id = #{orderId}
            order by id
            """)
    List<BusinessEntities.PurchaseOrderItem> listItemsByOrderId(Long orderId);

    @Update("update purchase_order set status = #{status}, approved_by = #{approvedBy}, approved_at = #{approvedAt}, updated_at = now() where id = #{id}")
    int updateOrderStatus(BusinessEntities.PurchaseOrder order);
}
