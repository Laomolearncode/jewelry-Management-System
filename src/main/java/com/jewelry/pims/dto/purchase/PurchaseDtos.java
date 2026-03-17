package com.jewelry.pims.dto.purchase;

import com.jewelry.pims.common.PageQuery;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class PurchaseDtos {

    private PurchaseDtos() {
    }

    @Data
    public static class PurchaseOrderCreateRequest {
        @NotNull(message = "供应商不能为空")
        private Long supplierId;
        @Valid
        @NotEmpty(message = "采购明细不能为空")
        private List<PurchaseItemRequest> items;
    }

    @Data
    public static class PurchaseItemRequest {
        @NotNull(message = "商品不能为空")
        private Long productId;
        @NotNull(message = "仓库不能为空")
        private Long warehouseId;
        private String batchNo;
        private String certificateNo;
        @NotNull(message = "数量不能为空")
        private Integer quantity;
        @NotNull(message = "克重不能为空")
        @DecimalMin(value = "0.0", inclusive = false, message = "克重大于0")
        private BigDecimal weight;
        @NotNull(message = "单价不能为空")
        @DecimalMin(value = "0.0", inclusive = false, message = "单价大于0")
        private BigDecimal unitPrice;
    }

    @Data
    public static class PurchaseOrderView {
        private Long id;
        private String orderNo;
        private Long supplierId;
        private String supplierName;
        private String status;
        private BigDecimal totalAmount;
        private LocalDateTime createdAt;
        private List<PurchaseItemView> items;
    }

    @Data
    public static class PurchaseItemView {
        private Long id;
        private Long productId;
        private String productName;
        private Long warehouseId;
        private String warehouseName;
        private String batchNo;
        private String certificateNo;
        private Integer quantity;
        private BigDecimal weight;
        private BigDecimal unitPrice;
        private BigDecimal subtotalAmount;
    }

    @Data
    public static class PurchaseOrderQuery extends PageQuery {
        private String orderNo;
        private String status;
        private String certificateNo;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
    }
}
