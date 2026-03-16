package com.jewelry.pims.dto.master;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class MasterDtos {

    private MasterDtos() {
    }

    @Data
    public static class MaterialRequest {
        @NotBlank(message = "材质编码不能为空")
        private String materialCode;
        @NotBlank(message = "材质名称不能为空")
        private String materialName;
        private String description;
        private Integer status = 1;
    }

    @Data
    public static class CategoryRequest {
        @NotBlank(message = "分类编码不能为空")
        private String categoryCode;
        @NotBlank(message = "分类名称不能为空")
        private String categoryName;
        private String description;
        private Integer status = 1;
    }

    @Data
    public static class SupplierRequest {
        @NotBlank(message = "供应商编码不能为空")
        private String supplierCode;
        @NotBlank(message = "供应商名称不能为空")
        private String supplierName;
        private String contactName;
        private String phone;
        private String address;
        private Integer status = 1;
    }

    @Data
    public static class CustomerRequest {
        @NotBlank(message = "客户编码不能为空")
        private String customerCode;
        @NotBlank(message = "客户名称不能为空")
        private String customerName;
        private String contactName;
        private String phone;
        private String address;
        private Integer status = 1;
    }

    @Data
    public static class WarehouseRequest {
        @NotBlank(message = "仓库编码不能为空")
        private String warehouseCode;
        @NotBlank(message = "仓库名称不能为空")
        private String warehouseName;
        private String location;
        private Integer status = 1;
    }

    @Data
    public static class ProductRequest {
        @NotBlank(message = "商品编码不能为空")
        private String productCode;
        @NotBlank(message = "商品名称不能为空")
        private String productName;
        @NotNull(message = "分类不能为空")
        private Long categoryId;
        @NotNull(message = "材质不能为空")
        private Long materialId;
        private String brand;
        private String unit;
        @NotNull(message = "克重不能为空")
        @DecimalMin(value = "0.0", inclusive = false, message = "克重大于0")
        private BigDecimal weight;
        @NotNull(message = "成本价不能为空")
        @DecimalMin(value = "0.0", message = "成本价不能为负")
        private BigDecimal costPrice;
        @NotNull(message = "销售价不能为空")
        @DecimalMin(value = "0.0", message = "销售价不能为负")
        private BigDecimal salePrice;
        private String certificateNo;
        private Integer warningThreshold = 0;
        private Integer status = 1;
    }

    @Data
    public static class ProductView {
        private Long id;
        private String productCode;
        private String productName;
        private String categoryName;
        private String materialName;
        private String brand;
        private String unit;
        private BigDecimal weight;
        private BigDecimal costPrice;
        private BigDecimal salePrice;
        private String certificateNo;
        private Integer abcLevel;
        private Integer status;
        private LocalDateTime createdAt;
    }
}
