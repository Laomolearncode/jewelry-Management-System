# 饰品进销存系统设计说明文档

## 1. 数据库设计说明

本系统数据库采用 `MySQL 8.x`，围绕“主数据、业务单据、库存结果、库存流水、权限控制、操作审计”六个层面展开设计。核心目标是保证饰品行业对材质、克重、批次号、证书号和库存溯源的精细化管理能力，同时兼顾采购、销售、盘点等业务在同一数据库中的一致性与可追踪性。

### 1.1 系统管理表

#### `sys_user`
作用：保存系统登录用户。

字段说明：
- `id`：主键
- `username`：登录账号
- `password`：登录密码
- `real_name`：真实姓名
- `phone`：手机号
- `status`：状态，`1` 表示启用，`0` 表示禁用
- `created_at`：创建时间
- `updated_at`：更新时间

#### `sys_role`
作用：保存角色信息。

字段说明：
- `id`：主键
- `role_code`：角色编码，如 `ADMIN`
- `role_name`：角色名称
- `description`：角色说明
- `status`：角色状态

#### `sys_permission`
作用：保存接口级权限点。

字段说明：
- `id`：主键
- `permission_code`：权限编码，格式为 `模块:资源:动作`
- `permission_name`：权限名称
- `description`：权限说明

#### `sys_user_role`
作用：用户与角色中间表。

字段说明：
- `id`：主键
- `user_id`：用户 ID
- `role_id`：角色 ID

#### `sys_role_permission`
作用：角色与权限中间表。

字段说明：
- `id`：主键
- `role_id`：角色 ID
- `permission_id`：权限 ID

#### `sys_user_token`
作用：保存登录令牌。

字段说明：
- `id`：主键
- `user_id`：用户 ID
- `token`：登录 token
- `expire_at`：过期时间
- `created_at`：创建时间

#### `sys_operate_log`
作用：保存操作审计日志。

字段说明：
- `id`：主键
- `user_id`：操作人 ID
- `username`：操作人账号
- `method`：请求方法
- `path`：请求路径
- `permission_code`：权限点编码
- `status_code`：响应状态码
- `duration_ms`：请求耗时
- `request_body`：请求内容
- `response_body`：响应内容摘要
- `ip`：客户端 IP
- `created_at`：记录时间

### 1.2 基础资料表

#### `material_dict`
作用：材质字典表。

字段说明：
- `id`：主键
- `material_code`：材质编码
- `material_name`：材质名称
- `description`：描述
- `status`：状态

#### `product_category`
作用：商品分类表。

字段说明：
- `id`：主键
- `category_code`：分类编码
- `category_name`：分类名称
- `description`：描述
- `status`：状态

#### `supplier`
作用：供应商档案表。

字段说明：
- `id`：主键
- `supplier_code`：供应商编码
- `supplier_name`：供应商名称
- `contact_name`：联系人
- `phone`：联系电话
- `address`：联系地址
- `status`：状态

#### `customer`
作用：客户档案表。

字段说明：
- `id`：主键
- `customer_code`：客户编码
- `customer_name`：客户名称
- `contact_name`：联系人
- `phone`：联系电话
- `address`：联系地址
- `status`：状态

#### `warehouse`
作用：仓库信息表。

字段说明：
- `id`：主键
- `warehouse_code`：仓库编码
- `warehouse_name`：仓库名称
- `location`：仓库位置
- `status`：状态

#### `product`
作用：饰品商品档案表，是整个业务的主数据核心。

字段说明：
- `id`：主键
- `product_code`：商品编码，唯一
- `product_name`：商品名称
- `category_id`：分类 ID
- `material_id`：材质 ID
- `brand`：品牌
- `unit`：单位
- `weight`：商品标准克重
- `cost_price`：成本价
- `sale_price`：销售价
- `certificate_no`：证书号
- `abc_level`：ABC 分类等级
- `warning_threshold`：库存预警阈值
- `status`：状态
- `created_at`：创建时间
- `updated_at`：更新时间

### 1.3 库存相关表

#### `stock`
作用：库存余额表，仅保存当前库存结果。

字段说明：
- `id`：主键
- `warehouse_id`：仓库 ID
- `product_id`：商品 ID
- `batch_no`：批次号
- `certificate_no`：证书号
- `quantity`：当前库存数量
- `total_weight`：当前库存总克重
- `avg_cost_price`：平均成本价
- `warning_threshold`：预警阈值
- `updated_at`：更新时间

设计特点：
- 库存维度为 `仓库 + 商品 + 批次号 + 证书号`
- 设置唯一约束，防止出现重复库存维度记录

#### `inventory_txn`
作用：库存流水表，记录每一次库存变化。

字段说明：
- `id`：主键
- `txn_no`：流水号
- `txn_type`：流水类型
- `warehouse_id`：仓库 ID
- `product_id`：商品 ID
- `related_order_id`：关联单据 ID
- `related_order_no`：关联单据编号
- `related_order_type`：关联单据类型
- `batch_no`：批次号
- `certificate_no`：证书号
- `quantity_delta`：数量变化值
- `quantity_after`：变化后库存
- `weight_delta`：克重变化值
- `unit_cost`：成本单价
- `remark`：备注
- `created_by`：创建人
- `created_at`：创建时间

### 1.4 采购相关表

#### `purchase_order`
作用：采购单主表。

字段说明：
- `id`：主键
- `order_no`：采购单号
- `supplier_id`：供应商 ID
- `status`：单据状态
- `total_amount`：总金额
- `approved_by`：审核人
- `approved_at`：审核时间
- `created_by`：创建人
- `created_at`：创建时间
- `updated_at`：更新时间

#### `purchase_order_item`
作用：采购单明细表。

字段说明：
- `id`：主键
- `order_id`：采购单 ID
- `product_id`：商品 ID
- `warehouse_id`：仓库 ID
- `batch_no`：批次号
- `certificate_no`：证书号
- `quantity`：采购数量
- `weight`：采购克重
- `unit_price`：采购单价
- `subtotal_amount`：明细金额小计

### 1.5 销售相关表

#### `sale_order`
作用：销售单主表。

字段说明：
- `id`：主键
- `order_no`：销售单号
- `customer_id`：客户 ID
- `status`：单据状态
- `total_amount`：销售总额
- `total_cost`：销售总成本
- `approved_by`：审核人
- `approved_at`：审核时间
- `created_by`：创建人
- `created_at`：创建时间
- `updated_at`：更新时间

#### `sale_order_item`
作用：销售单明细表。

字段说明：
- `id`：主键
- `order_id`：销售单 ID
- `product_id`：商品 ID
- `warehouse_id`：仓库 ID
- `batch_no`：批次号
- `certificate_no`：证书号
- `quantity`：销售数量
- `weight`：销售克重
- `unit_price`：销售单价
- `unit_cost`：成本单价
- `subtotal_amount`：金额小计

### 1.6 盘点与溯源表

#### `stock_check_order`
作用：盘点单主表。

字段说明：
- `id`：主键
- `check_no`：盘点单号
- `warehouse_id`：仓库 ID
- `status`：单据状态
- `remark`：备注
- `approved_by`：审核人
- `approved_at`：审核时间
- `created_by`：创建人
- `created_at`：创建时间

#### `stock_check_item`
作用：盘点单明细表。

字段说明：
- `id`：主键
- `check_order_id`：盘点单 ID
- `stock_id`：库存记录 ID
- `system_quantity`：系统账面数量
- `actual_quantity`：实际盘点数量
- `diff_quantity`：差异数量

#### `trace_log`
作用：溯源日志表，用于记录关键业务轨迹。

字段说明：
- `id`：主键
- `trace_type`：轨迹类型
- `source_type`：来源类型
- `source_no`：来源单号
- `product_id`：商品 ID
- `batch_no`：批次号
- `certificate_no`：证书号
- `content`：轨迹内容
- `created_by`：创建人
- `created_at`：创建时间

### 1.7 数据库设计特点

- 采用“主数据 + 业务单据 + 库存余额 + 库存流水 + 溯源日志”的分层设计
- `stock` 保存结果，`inventory_txn` 保存过程
- `product_code` 和 `certificate_no` 具备唯一约束，便于精确追踪
- 通过采购、销售、盘点、溯源形成完整业务闭环

## 2. 系统架构说明

本系统后端采用单体三层架构，技术栈为 `Spring Boot + MyBatis + MySQL`。在满足毕业设计功能需求的同时，兼顾系统可维护性、可扩展性和实现复杂度控制。

### 2.1 整体架构

- 表现层 `Controller`：负责接收请求、参数校验、返回统一响应
- 业务层 `Service`：负责业务规则、状态流转、事务处理
- 数据访问层 `Mapper`：负责数据库交互
- 数据存储层 `MySQL`：负责业务数据持久化

### 2.2 横切能力设计

- 统一响应：所有接口统一返回 `ApiResponse`
- 全局异常处理：统一处理参数错误和业务异常
- 登录认证：通过 Bearer Token 实现身份识别
- 权限控制：通过 `@Permission` 注解控制接口访问
- 操作日志：通过拦截器自动记录请求轨迹

### 2.3 请求处理流程

客户端请求进入系统后，依次经过操作日志拦截器、认证拦截器和权限拦截器。认证通过后进入对应控制器，再调用业务服务层和数据访问层，最后访问 MySQL 完成数据处理，并返回统一格式的结果。

### 2.4 架构特点

- 结构清晰，适合毕业设计展示
- 控制器、服务、数据访问职责分离
- 通过拦截器实现统一安全控制
- 通过事务保证采购入库、销售出库、盘点调整的数据一致性

## 3. 模块设计说明

系统共划分为七个核心模块。

### 3.1 系统管理模块

功能包括：
- 用户管理
- 角色管理
- 权限管理
- 登录认证
- 操作日志

该模块负责整个系统的用户身份识别和访问控制，是其他模块运行的基础。

### 3.2 基础信息管理模块

功能包括：
- 材质管理
- 分类管理
- 供应商管理
- 客户管理
- 仓库管理
- 商品管理

该模块提供采购、销售、库存业务运行所依赖的基础数据。

### 3.3 采购管理模块

功能包括：
- 采购单创建
- 采购单查询
- 采购单审核
- 采购入库

采购流程状态一般为：
- `DRAFT`：草稿
- `APPROVED`：审核通过
- `STOCKED`：已入库

### 3.4 库存管理模块

功能包括：
- 库存查询
- 库存余额维护
- 库存流水记录
- 低库存预警

库存模块通过采购入库、销售出库和盘点调整等业务操作自动维护，不允许随意手工修改库存结果。

### 3.5 销售管理模块

功能包括：
- 销售单创建
- 销售单查询
- 销售单审核
- 自动出库

销售单审核通过后立即触发库存扣减，并同步记录销售成本和库存流水。

### 3.6 盘点与溯源模块

功能包括：
- 盘点单创建
- 盘点单审核
- 盘点差异调整
- 批次号查询溯源
- 证书号查询溯源

该模块用于处理账实差异问题，并支撑饰品行业对高价值商品的全流程追踪。

### 3.7 统计分析模块

功能包括：
- 仪表盘统计
- ABC 分类分析
- 库存周转率分析

该模块主要面向经营分析与库存决策。

## 4. 接口说明

### 4.1 鉴权接口

#### `POST /api/auth/login`
用途：用户登录并获取 token

请求参数：
- `username`
- `password`

返回内容：
- token
- 过期时间

#### `POST /api/auth/logout`
用途：退出登录

#### `GET /api/auth/profile`
用途：查询当前登录用户信息、角色和权限

### 4.2 系统管理接口

- `POST /api/system/users`：创建用户
- `GET /api/system/users`：查询用户列表
- `PUT /api/system/users/{id}/status`：修改用户状态
- `GET /api/system/roles`：查询角色列表
- `GET /api/system/permissions`：查询权限列表
- `GET /api/system/logs`：查询操作日志

### 4.3 基础资料接口

- `POST /api/master/materials`、`GET /api/master/materials`
- `POST /api/master/categories`、`GET /api/master/categories`
- `POST /api/master/suppliers`、`GET /api/master/suppliers`
- `POST /api/master/customers`、`GET /api/master/customers`
- `POST /api/master/warehouses`、`GET /api/master/warehouses`
- `POST /api/master/products`、`GET /api/master/products`
- `GET /api/master/stocks`

### 4.4 采购接口

- `POST /api/purchases`：创建采购单
- `GET /api/purchases`：查询采购单列表
- `GET /api/purchases/{id}`：查询采购单详情
- `POST /api/purchases/{id}/approve`：审核采购单
- `POST /api/purchases/{id}/stock-in`：采购入库

### 4.5 销售接口

- `POST /api/sales`：创建销售单
- `GET /api/sales`：查询销售单列表
- `GET /api/sales/{id}`：查询销售单详情
- `POST /api/sales/{id}/approve`：审核销售单并完成出库

### 4.6 盘点与溯源接口

- `POST /api/inventory/checks`：创建盘点单
- `GET /api/inventory/checks`：查询盘点单列表
- `POST /api/inventory/checks/{id}/approve`：审核盘点单
- `GET /api/inventory/trace?keyword=`：按批次号或证书号进行溯源查询

### 4.7 报表接口

- `GET /api/reports/dashboard`：仪表盘统计
- `GET /api/reports/abc`：ABC 分类分析
- `GET /api/reports/turnover`：库存周转率分析

## 5. 测试用例说明

测试内容可分为功能测试、数据准确性测试、权限测试和异常测试四类。

### 5.1 功能测试用例

#### 用例 1：用户登录
- 前置条件：系统中存在有效账号
- 输入：正确用户名和密码
- 预期结果：返回 token，登录成功

#### 用例 2：商品创建
- 前置条件：分类和材质已存在
- 输入：商品编码、名称、克重、成本价、销售价、证书号
- 预期结果：商品创建成功，商品列表中可查询到

#### 用例 3：采购单创建
- 前置条件：供应商、商品、仓库存在
- 输入：采购主表与采购明细数据
- 预期结果：采购单状态为 `DRAFT`

#### 用例 4：采购单审核
- 前置条件：采购单存在且状态为 `DRAFT`
- 输入：采购单 ID
- 预期结果：采购单状态变为 `APPROVED`

#### 用例 5：采购入库
- 前置条件：采购单已审核
- 输入：采购单 ID
- 预期结果：库存增加，生成库存流水，采购单状态变为 `STOCKED`

#### 用例 6：销售审核出库
- 前置条件：库存充足
- 输入：销售单 ID
- 预期结果：库存减少，生成销售出库流水，销售单状态变为 `APPROVED`

#### 用例 7：盘点审核
- 前置条件：库存记录存在
- 输入：盘点单 ID
- 预期结果：库存按照差异调整，并生成盘点流水

#### 用例 8：溯源查询
- 前置条件：系统中已发生采购、入库、销售或盘点业务
- 输入：证书号或批次号
- 预期结果：返回对应业务轨迹

### 5.2 数据准确性测试用例

- 测试采购入库后库存数量是否正确增加
- 测试销售出库后库存数量是否正确减少
- 测试盘点审核后库存数量是否与实盘数量一致
- 测试销售成本和利润计算是否正确
- 测试仪表盘统计数据是否与数据库实际数据一致

### 5.3 权限测试用例

- 无 token 访问受保护接口，应返回未登录
- 错误 token 访问接口，应返回登录失效
- 无权限角色访问受限接口，应返回无权访问
- 管理员访问系统管理接口，应可成功执行

### 5.4 异常测试用例

- 创建商品时使用重复商品编码，应提示失败
- 创建商品时使用重复证书号，应提示失败
- 销售数量超过库存数量，应提示库存不足
- 已入库采购单再次入库，应禁止重复执行
- 已审核销售单再次审核，应禁止重复执行

## 6. 结论

本系统数据库设计围绕饰品行业进销存场景展开，充分考虑了材质、克重、批次号、证书号等行业特性，并通过采购、销售、库存、盘点和溯源模块形成完整的数据闭环。系统架构采用 `Spring Boot` 单体三层设计，在满足毕业设计实现要求的基础上，通过统一响应、权限控制、日志审计和事务机制保证了系统的可维护性、安全性和数据一致性。通过接口联调和测试验证，系统已具备较好的业务完整性和演示可行性，可作为毕业设计论文的系统设计与实现基础材料。
