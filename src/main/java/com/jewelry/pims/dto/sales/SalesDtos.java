package com.jewelry.pims.dto.sales;

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

public final class SalesDtos {

    private SalesDtos() {
    }

    @Data
    public static class SaleOrderCreateRequest {
        @NotNull(message = "客户不能为空")
        private Long customerId;
        @Valid
        @NotEmpty(message = "销售明细不能为空")
        private List<SaleItemRequest> items;
    }

    @Data
    public static class SaleItemRequest {
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
        @NotNull(message = "销售单价不能为空")
        @DecimalMin(value = "0.0", inclusive = false, message = "销售单价大于0")
        private BigDecimal unitPrice;
    }

    @Data
    public static class SaleOrderView {
        private Long id;
        private String orderNo;
        private Long customerId;
        private String customerName;
        private String status;
        private BigDecimal totalAmount;
        private BigDecimal totalCost;
        private LocalDateTime createdAt;
        private List<SaleItemView> items;
    }

    @Data
    public static class SaleItemView {
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
        private BigDecimal unitCost;
        private BigDecimal subtotalAmount;
    }

    @Data
    public static class SaleOrderQuery extends PageQuery {
        private String orderNo;
        private String status;
        private String certificateNo;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
    }
}
