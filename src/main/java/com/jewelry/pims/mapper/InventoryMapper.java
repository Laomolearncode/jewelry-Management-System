package com.jewelry.pims.mapper;

import com.jewelry.pims.domain.BusinessEntities;
import com.jewelry.pims.dto.inventory.InventoryDtos;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface InventoryMapper {

    @Insert("""
            insert into inventory_txn(txn_no, txn_type, warehouse_id, product_id, related_order_id, related_order_no, related_order_type, batch_no, certificate_no, quantity_delta, quantity_after, weight_delta, unit_cost, remark, created_by, created_at)
            values(#{txnNo}, #{txnType}, #{warehouseId}, #{productId}, #{relatedOrderId}, #{relatedOrderNo}, #{relatedOrderType}, #{batchNo}, #{certificateNo}, #{quantityDelta}, #{quantityAfter}, #{weightDelta}, #{unitCost}, #{remark}, #{createdBy}, now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertTxn(BusinessEntities.InventoryTxn txn);

    @Insert("""
            insert into trace_log(trace_type, source_type, source_no, product_id, batch_no, certificate_no, content, created_by, created_at)
            values(#{traceType}, #{sourceType}, #{sourceNo}, #{productId}, #{batchNo}, #{certificateNo}, #{content}, #{createdBy}, now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertTrace(BusinessEntities.TraceLog traceLog);

    @Insert("""
            insert into stock_check_order(check_no, warehouse_id, status, remark, approved_by, approved_at, created_by, created_at)
            values(#{checkNo}, #{warehouseId}, #{status}, #{remark}, #{approvedBy}, #{approvedAt}, #{createdBy}, now())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertCheckOrder(BusinessEntities.StockCheckOrder order);

    @Insert("""
            insert into stock_check_item(check_order_id, stock_id, system_quantity, actual_quantity, diff_quantity)
            values(#{checkOrderId}, #{stockId}, #{systemQuantity}, #{actualQuantity}, #{diffQuantity})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertCheckItem(BusinessEntities.StockCheckItem item);

    @Select("""
            select id, check_no, warehouse_id, status, remark, approved_by, approved_at, created_by, created_at
            from stock_check_order order by id desc
            """)
    List<BusinessEntities.StockCheckOrder> listCheckOrders();

    @Select("""
            select id, check_no, warehouse_id, status, remark, approved_by, approved_at, created_by, created_at
            from stock_check_order where id = #{id}
            """)
    BusinessEntities.StockCheckOrder findCheckOrderById(Long id);

    @Update("""
            update stock_check_order
            set status = #{status}, approved_by = #{approvedBy}, approved_at = #{approvedAt}
            where id = #{id}
            """)
    int updateCheckOrder(BusinessEntities.StockCheckOrder order);

    @Select("""
            select id, check_order_id, stock_id, system_quantity, actual_quantity, diff_quantity
            from stock_check_item where check_order_id = #{checkOrderId}
            """)
    List<BusinessEntities.StockCheckItem> listCheckItems(Long checkOrderId);

    @Select("""
            select trace_type, source_type, source_no, product_id, batch_no, certificate_no, content, created_at
            from trace_log
            where certificate_no = #{keyword}
               or batch_no = #{keyword}
               or source_no = #{keyword}
            order by id desc
            """)
    List<InventoryDtos.TraceView> queryTrace(String keyword);

    @Select("""
            select ifnull(sum(quantity * avg_cost_price), 0) from stock
            """)
    BigDecimal sumStockAmount();

    @Select("""
            <script>
            select ifnull(sum(total_amount), 0) from purchase_order
            where status = 'STOCKED'
            <if test="startTime != null">
              and created_at <![CDATA[>=]]> #{startTime}
            </if>
            <if test="endTime != null">
              and created_at <![CDATA[<=]]> #{endTime}
            </if>
            </script>
            """)
    BigDecimal sumPurchaseAmount(@Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("""
            <script>
            select ifnull(sum(total_amount), 0) from sale_order
            where status = 'APPROVED'
            <if test="startTime != null">
              and created_at <![CDATA[>=]]> #{startTime}
            </if>
            <if test="endTime != null">
              and created_at <![CDATA[<=]]> #{endTime}
            </if>
            </script>
            """)
    BigDecimal sumSaleAmount(@Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("""
            <script>
            select ifnull(sum(total_amount - total_cost), 0) from sale_order
            where status = 'APPROVED'
            <if test="startTime != null">
              and created_at <![CDATA[>=]]> #{startTime}
            </if>
            <if test="endTime != null">
              and created_at <![CDATA[<=]]> #{endTime}
            </if>
            </script>
            """)
    BigDecimal sumGrossProfit(@Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("""
            select ifnull(sum(quantity), 0) from stock
            """)
    Integer sumStockQuantity();

    @Select("""
            select p.id as productId, p.product_code as productCode, p.product_name as productName, sum(s.quantity * s.avg_cost_price) as stockAmount
            from stock s
            inner join product p on p.id = s.product_id
            group by p.id, p.product_code, p.product_name
            order by stockAmount desc
            """)
    List<Map<String, Object>> abcSource();

    @Select("""
            <script>
            select date_format(created_at, '%Y-%m') as period, ifnull(sum(total_cost), 0) as salesCost
            from sale_order
            where status = 'APPROVED'
            <if test="startTime != null">
              and created_at <![CDATA[>=]]> #{startTime}
            </if>
            <if test="endTime != null">
              and created_at <![CDATA[<=]]> #{endTime}
            </if>
            group by date_format(created_at, '%Y-%m')
            order by period desc
            limit 12
            </script>
            """)
    List<Map<String, Object>> monthlySalesCost(@Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("""
            select ifnull(avg(quantity * avg_cost_price), 0) from stock
            """)
    BigDecimal averageInventoryCost();
}
