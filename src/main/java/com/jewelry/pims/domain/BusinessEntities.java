package com.jewelry.pims.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class BusinessEntities {

    private BusinessEntities() {
    }

    @Data
    public static class PurchaseOrder {
        private Long id;
        private String orderNo;
        private Long supplierId;
        private String status;
        private BigDecimal totalAmount;
        private Long approvedBy;
        private LocalDateTime approvedAt;
        private Long createdBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    public static class PurchaseOrderItem {
        private Long id;
        private Long orderId;
        private Long productId;
        private Long warehouseId;
        private String batchNo;
        private String certificateNo;
        private Integer quantity;
        private BigDecimal weight;
        private BigDecimal unitPrice;
        private BigDecimal subtotalAmount;
    }

    @Data
    public static class SaleOrder {
        private Long id;
        private String orderNo;
        private Long customerId;
        private String status;
        private BigDecimal totalAmount;
        private BigDecimal totalCost;
        private Long approvedBy;
        private LocalDateTime approvedAt;
        private Long createdBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    public static class SaleOrderItem {
        private Long id;
        private Long orderId;
        private Long productId;
        private Long warehouseId;
        private String batchNo;
        private String certificateNo;
        private Integer quantity;
        private BigDecimal weight;
        private BigDecimal unitPrice;
        private BigDecimal unitCost;
        private BigDecimal subtotalAmount;
    }

    @Data
    public static class StockCheckOrder {
        private Long id;
        private String checkNo;
        private Long warehouseId;
        private String status;
        private String remark;
        private Long approvedBy;
        private LocalDateTime approvedAt;
        private Long createdBy;
        private LocalDateTime createdAt;
    }

    @Data
    public static class StockCheckItem {
        private Long id;
        private Long checkOrderId;
        private Long stockId;
        private Integer systemQuantity;
        private Integer actualQuantity;
        private Integer diffQuantity;
    }

    @Data
    public static class InventoryTxn {
        private Long id;
        private String txnNo;
        private String txnType;
        private Long warehouseId;
        private Long productId;
        private Long relatedOrderId;
        private String relatedOrderNo;
        private String relatedOrderType;
        private String batchNo;
        private String certificateNo;
        private Integer quantityDelta;
        private Integer quantityAfter;
        private BigDecimal weightDelta;
        private BigDecimal unitCost;
        private String remark;
        private Long createdBy;
        private LocalDateTime createdAt;
    }

    @Data
    public static class TraceLog {
        private Long id;
        private String traceType;
        private String sourceType;
        private String sourceNo;
        private Long productId;
        private String batchNo;
        private String certificateNo;
        private String content;
        private Long createdBy;
        private LocalDateTime createdAt;
    }
}
