package com.jewelry.pims.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class MasterEntities {

    private MasterEntities() {
    }

    @Data
    public static class Material {
        private Long id;
        private String materialCode;
        private String materialName;
        private String description;
        private Integer status;
    }

    @Data
    public static class ProductCategory {
        private Long id;
        private String categoryCode;
        private String categoryName;
        private String description;
        private Integer status;
    }

    @Data
    public static class Supplier {
        private Long id;
        private String supplierCode;
        private String supplierName;
        private String contactName;
        private String phone;
        private String address;
        private Integer status;
    }

    @Data
    public static class Customer {
        private Long id;
        private String customerCode;
        private String customerName;
        private String contactName;
        private String phone;
        private String address;
        private Integer status;
    }

    @Data
    public static class Warehouse {
        private Long id;
        private String warehouseCode;
        private String warehouseName;
        private String location;
        private Integer status;
    }

    @Data
    public static class Product {
        private Long id;
        private String productCode;
        private String productName;
        private Long categoryId;
        private Long materialId;
        private String brand;
        private String unit;
        private BigDecimal weight;
        private BigDecimal costPrice;
        private BigDecimal salePrice;
        private String certificateNo;
        private Integer abcLevel;
        private Integer warningThreshold;
        private Integer status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    public static class Stock {
        private Long id;
        private Long warehouseId;
        private Long productId;
        private String batchNo;
        private String certificateNo;
        private Integer quantity;
        private BigDecimal totalWeight;
        private BigDecimal avgCostPrice;
        private Integer warningThreshold;
        private LocalDateTime updatedAt;
    }
}
