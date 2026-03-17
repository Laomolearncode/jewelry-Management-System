package com.jewelry.pims.mapper;

import com.jewelry.pims.domain.BusinessEntities;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SalesMapper {

    @Insert("""
            insert into sale_order(order_no, customer_id, status, total_amount, total_cost, approved_by, approved_at, created_by, created_at, updated_at)
            values(#{orderNo}, #{customerId}, #{status}, #{totalAmount}, #{totalCost}, #{approvedBy}, #{approvedAt}, #{createdBy}, now(), now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrder(BusinessEntities.SaleOrder order);

    @Insert("""
            insert into sale_order_item(order_id, product_id, warehouse_id, batch_no, certificate_no, quantity, weight, unit_price, unit_cost, subtotal_amount)
            values(#{orderId}, #{productId}, #{warehouseId}, #{batchNo}, #{certificateNo}, #{quantity}, #{weight}, #{unitPrice}, #{unitCost}, #{subtotalAmount})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertItem(BusinessEntities.SaleOrderItem item);

    @Select("""
            select id, order_no, customer_id, status, total_amount, total_cost, approved_by, approved_at, created_by, created_at, updated_at
            from sale_order order by id desc
            """)
    List<BusinessEntities.SaleOrder> listOrders();

    @Select("""
            select id, order_no, customer_id, status, total_amount, total_cost, approved_by, approved_at, created_by, created_at, updated_at
            from sale_order where id = #{id}
            """)
    BusinessEntities.SaleOrder findOrderById(Long id);

    @Select("""
            select id, order_id, product_id, warehouse_id, batch_no, certificate_no, quantity, weight, unit_price, unit_cost, subtotal_amount
            from sale_order_item where order_id = #{orderId} order by id
            """)
    List<BusinessEntities.SaleOrderItem> listItemsByOrderId(Long orderId);

    @Update("update sale_order_item set unit_cost = #{unitCost} where id = #{id}")
    int updateItemUnitCost(BusinessEntities.SaleOrderItem item);

    @Update("update sale_order set status = #{status}, total_cost = #{totalCost}, approved_by = #{approvedBy}, approved_at = #{approvedAt}, updated_at = now() where id = #{id}")
    int updateOrderStatus(BusinessEntities.SaleOrder order);
}
