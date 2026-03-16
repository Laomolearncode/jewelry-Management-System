CREATE DATABASE IF NOT EXISTS jewelry_pims DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE jewelry_pims;

DROP TABLE IF EXISTS trace_log;
DROP TABLE IF EXISTS inventory_txn;
DROP TABLE IF EXISTS stock_check_item;
DROP TABLE IF EXISTS stock_check_order;
DROP TABLE IF EXISTS sale_order_item;
DROP TABLE IF EXISTS sale_order;
DROP TABLE IF EXISTS purchase_order_item;
DROP TABLE IF EXISTS purchase_order;
DROP TABLE IF EXISTS stock;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS warehouse;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS supplier;
DROP TABLE IF EXISTS product_category;
DROP TABLE IF EXISTS material_dict;
DROP TABLE IF EXISTS sys_operate_log;
DROP TABLE IF EXISTS sys_user_token;
DROP TABLE IF EXISTS sys_role_permission;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_permission;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    role_name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    status TINYINT NOT NULL DEFAULT 1
);

CREATE TABLE sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    permission_code VARCHAR(100) NOT NULL UNIQUE,
    permission_name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_role (user_id, role_id)
);

CREATE TABLE sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    UNIQUE KEY uk_role_permission (role_id, permission_id)
);

CREATE TABLE sys_user_token (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token VARCHAR(128) NOT NULL UNIQUE,
    expire_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL
);

CREATE TABLE sys_operate_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    username VARCHAR(50),
    method VARCHAR(10),
    path VARCHAR(255),
    permission_code VARCHAR(100),
    status_code INT,
    duration_ms BIGINT,
    request_body TEXT,
    response_body TEXT,
    ip VARCHAR(64),
    created_at DATETIME NOT NULL
);

CREATE TABLE material_dict (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    material_code VARCHAR(50) NOT NULL UNIQUE,
    material_name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    status TINYINT NOT NULL DEFAULT 1
);

CREATE TABLE product_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_code VARCHAR(50) NOT NULL UNIQUE,
    category_name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    status TINYINT NOT NULL DEFAULT 1
);

CREATE TABLE supplier (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    supplier_code VARCHAR(50) NOT NULL UNIQUE,
    supplier_name VARCHAR(100) NOT NULL,
    contact_name VARCHAR(50),
    phone VARCHAR(20),
    address VARCHAR(255),
    status TINYINT NOT NULL DEFAULT 1
);

CREATE TABLE customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_code VARCHAR(50) NOT NULL UNIQUE,
    customer_name VARCHAR(100) NOT NULL,
    contact_name VARCHAR(50),
    phone VARCHAR(20),
    address VARCHAR(255),
    status TINYINT NOT NULL DEFAULT 1
);

CREATE TABLE warehouse (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    warehouse_code VARCHAR(50) NOT NULL UNIQUE,
    warehouse_name VARCHAR(100) NOT NULL,
    location VARCHAR(255),
    status TINYINT NOT NULL DEFAULT 1
);

CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_code VARCHAR(50) NOT NULL UNIQUE,
    product_name VARCHAR(100) NOT NULL,
    category_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    brand VARCHAR(100),
    unit VARCHAR(20),
    weight DECIMAL(12, 4) NOT NULL,
    cost_price DECIMAL(12, 2) NOT NULL,
    sale_price DECIMAL(12, 2) NOT NULL,
    certificate_no VARCHAR(100) UNIQUE,
    abc_level TINYINT NOT NULL DEFAULT 3,
    warning_threshold INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE stock (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    warehouse_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    batch_no VARCHAR(100),
    certificate_no VARCHAR(100),
    quantity INT NOT NULL,
    total_weight DECIMAL(12, 4) NOT NULL DEFAULT 0,
    avg_cost_price DECIMAL(12, 4) NOT NULL DEFAULT 0,
    warning_threshold INT NOT NULL DEFAULT 0,
    updated_at DATETIME NOT NULL,
    UNIQUE KEY uk_stock_dim (warehouse_id, product_id, batch_no, certificate_no)
);

CREATE TABLE purchase_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    supplier_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    approved_by BIGINT,
    approved_at DATETIME,
    created_by BIGINT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE purchase_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    batch_no VARCHAR(100),
    certificate_no VARCHAR(100),
    quantity INT NOT NULL,
    weight DECIMAL(12, 4) NOT NULL,
    unit_price DECIMAL(12, 2) NOT NULL,
    subtotal_amount DECIMAL(12, 2) NOT NULL
);

CREATE TABLE sale_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    total_cost DECIMAL(12, 2) NOT NULL DEFAULT 0,
    approved_by BIGINT,
    approved_at DATETIME,
    created_by BIGINT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE sale_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    batch_no VARCHAR(100),
    certificate_no VARCHAR(100),
    quantity INT NOT NULL,
    weight DECIMAL(12, 4) NOT NULL,
    unit_price DECIMAL(12, 2) NOT NULL,
    unit_cost DECIMAL(12, 4) NOT NULL DEFAULT 0,
    subtotal_amount DECIMAL(12, 2) NOT NULL
);

CREATE TABLE stock_check_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    check_no VARCHAR(50) NOT NULL UNIQUE,
    warehouse_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    remark VARCHAR(255),
    approved_by BIGINT,
    approved_at DATETIME,
    created_by BIGINT,
    created_at DATETIME NOT NULL
);

CREATE TABLE stock_check_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    check_order_id BIGINT NOT NULL,
    stock_id BIGINT NOT NULL,
    system_quantity INT NOT NULL,
    actual_quantity INT NOT NULL,
    diff_quantity INT NOT NULL
);

CREATE TABLE inventory_txn (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    txn_no VARCHAR(50) NOT NULL UNIQUE,
    txn_type VARCHAR(30) NOT NULL,
    warehouse_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    related_order_id BIGINT,
    related_order_no VARCHAR(50),
    related_order_type VARCHAR(30),
    batch_no VARCHAR(100),
    certificate_no VARCHAR(100),
    quantity_delta INT NOT NULL,
    quantity_after INT NOT NULL,
    weight_delta DECIMAL(12, 4) NOT NULL DEFAULT 0,
    unit_cost DECIMAL(12, 4) NOT NULL DEFAULT 0,
    remark VARCHAR(255),
    created_by BIGINT,
    created_at DATETIME NOT NULL
);

CREATE TABLE trace_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    trace_type VARCHAR(30) NOT NULL,
    source_type VARCHAR(30) NOT NULL,
    source_no VARCHAR(50) NOT NULL,
    product_id BIGINT,
    batch_no VARCHAR(100),
    certificate_no VARCHAR(100),
    content VARCHAR(255),
    created_by BIGINT,
    created_at DATETIME NOT NULL
);

CREATE INDEX idx_purchase_supplier ON purchase_order(supplier_id);
CREATE INDEX idx_sale_customer ON sale_order(customer_id);
CREATE INDEX idx_stock_product ON stock(product_id);
CREATE INDEX idx_trace_certificate ON trace_log(certificate_no);
CREATE INDEX idx_trace_batch ON trace_log(batch_no);
