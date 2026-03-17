package com.jewelry.pims.dto.inventory;

import com.jewelry.pims.common.PageQuery;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class InventoryDtos {

    private InventoryDtos() {
    }

    @Data
    public static class StockView {
        private Long id;
        private Long warehouseId;
        private String warehouseName;
        private Long productId;
        private String productCode;
        private String productName;
        private String batchNo;
        private String certificateNo;
        private Integer quantity;
        private BigDecimal totalWeight;
        private BigDecimal avgCostPrice;
        private Integer warningThreshold;
        private LocalDateTime updatedAt;
    }

    @Data
    public static class StockCheckCreateRequest {
        @NotNull(message = "仓库不能为空")
        private Long warehouseId;
        private String remark;
        @Valid
        @NotEmpty(message = "盘点明细不能为空")
        private List<StockCheckItemRequest> items;
    }

    @Data
    public static class StockCheckItemRequest {
        @NotNull(message = "库存记录不能为空")
        private Long stockId;
        @NotNull(message = "实盘数量不能为空")
        private Integer actualQuantity;
    }

    @Data
    public static class TraceView {
        private String traceType;
        private String sourceType;
        private String sourceNo;
        private Long productId;
        private String batchNo;
        private String certificateNo;
        private String content;
        private LocalDateTime createdAt;
    }

    @Data
    public static class DashboardView {
        private Integer productCount;
        private Integer stockQuantity;
        private BigDecimal stockAmount;
        private BigDecimal purchaseAmount;
        private BigDecimal saleAmount;
        private BigDecimal grossProfit;
        private Integer lowStockCount;
    }

    @Data
    public static class AbcItemView {
        private Long productId;
        private String productCode;
        private String productName;
        private BigDecimal stockAmount;
        private String abcClass;
    }

    @Data
    public static class TurnoverView {
        private String period;
        private BigDecimal salesCost;
        private BigDecimal averageInventoryCost;
        private BigDecimal turnoverRate;
    }

    @Data
    public static class TraceQuery {
        @NotBlank(message = "查询关键字不能为空")
        private String keyword;
    }

    @Data
    public static class StockQuery extends PageQuery {
        private String productCode;
        private String productName;
        private String batchNo;
        private String certificateNo;
        private Boolean lowStockOnly = false;
    }

    @Data
    public static class ReportQuery {
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
    }
}
