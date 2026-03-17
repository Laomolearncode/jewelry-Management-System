# 饰品进销存系统接口调试手册

本文档用于指导后端接口联调，重点说明每个接口的请求方式、请求路径、请求头、查询参数和请求体示例。除登录接口外，其余接口均需在请求头中携带 Bearer Token。

## 1. 通用说明

### 1.1 服务地址

```text
http://localhost:8080
```

### 1.2 通用请求头

除 `POST /api/auth/login` 外，其余接口统一携带：

```http
Authorization: Bearer <token>
Content-Type: application/json
```

### 1.3 分页参数

支持分页的列表接口统一使用以下参数：

| 参数名 | 含义 | 默认值 |
| --- | --- | --- |
| `pageNo` | 页码，从 1 开始 | 1 |
| `pageSize` | 每页条数，最大 100 | 10 |

分页接口返回结构示例：

```json
{
  "success": true,
  "message": "success",
  "data": {
    "total": 1,
    "records": []
  }
}
```

## 2. 认证接口

### 2.1 用户登录

- 方法：`POST`
- 路径：`/api/auth/login`
- 是否需要 token：否

请求体：

```json
{
  "username": "admin",
  "password": "admin123"
}
```

返回示例：

```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "xxxxxxxxxxxxxxxx",
    "expireHours": 12
  }
}
```

### 2.2 退出登录

- 方法：`POST`
- 路径：`/api/auth/logout`
- 是否需要 token：是
- 请求体：无

### 2.3 查询当前登录用户

- 方法：`GET`
- 路径：`/api/auth/profile`
- 是否需要 token：是
- 请求体：无

## 3. 系统管理接口

### 3.1 创建用户

- 方法：`POST`
- 路径：`/api/system/users`

请求体：

```json
{
  "username": "purchaser01",
  "password": "123456",
  "realName": "采购员张三",
  "phone": "13800138000",
  "roleIds": [2]
}
```

### 3.2 查询用户列表

- 方法：`GET`
- 路径：`/api/system/users`

### 3.3 修改用户状态

- 方法：`PUT`
- 路径：`/api/system/users/{id}/status`

请求体：

```json
{
  "status": 0
}
```

### 3.4 查询角色列表

- 方法：`GET`
- 路径：`/api/system/roles`

### 3.5 查询权限列表

- 方法：`GET`
- 路径：`/api/system/permissions`

### 3.6 查询操作日志

- 方法：`GET`
- 路径：`/api/system/logs`

## 4. 基础资料接口

### 4.1 新增材质

- 方法：`POST`
- 路径：`/api/master/materials`

请求体：

```json
{
  "materialCode": "PT",
  "materialName": "铂金",
  "description": "铂金材质",
  "status": 1
}
```

### 4.2 查询材质列表

- 方法：`GET`
- 路径：`/api/master/materials`

### 4.3 新增分类

- 方法：`POST`
- 路径：`/api/master/categories`

请求体：

```json
{
  "categoryCode": "BRACELET",
  "categoryName": "手链",
  "description": "手链类饰品",
  "status": 1
}
```

### 4.4 查询分类列表

- 方法：`GET`
- 路径：`/api/master/categories`

### 4.5 新增供应商

- 方法：`POST`
- 路径：`/api/master/suppliers`

请求体：

```json
{
  "supplierCode": "SUP002",
  "supplierName": "测试供应商",
  "contactName": "李经理",
  "phone": "13900000001",
  "address": "广州市番禺区",
  "status": 1
}
```

### 4.6 查询供应商列表

- 方法：`GET`
- 路径：`/api/master/suppliers`

### 4.7 新增客户

- 方法：`POST`
- 路径：`/api/master/customers`

请求体：

```json
{
  "customerCode": "CUS002",
  "customerName": "测试客户",
  "contactName": "王女士",
  "phone": "13700000001",
  "address": "广州市天河区",
  "status": 1
}
```

### 4.8 查询客户列表

- 方法：`GET`
- 路径：`/api/master/customers`

### 4.9 新增仓库

- 方法：`POST`
- 路径：`/api/master/warehouses`

请求体：

```json
{
  "warehouseCode": "WH002",
  "warehouseName": "门店仓",
  "location": "广州市海珠区",
  "status": 1
}
```

### 4.10 查询仓库列表

- 方法：`GET`
- 路径：`/api/master/warehouses`

### 4.11 新增商品

- 方法：`POST`
- 路径：`/api/master/products`

请求体：

```json
{
  "productCode": "P001",
  "productName": "足金戒指A01",
  "categoryId": 1,
  "materialId": 1,
  "brand": "测试品牌",
  "unit": "件",
  "weight": 5.20,
  "costPrice": 2800.00,
  "salePrice": 3280.00,
  "certificateNo": "CERT-P001",
  "warningThreshold": 2,
  "status": 1
}
```

### 4.12 查询商品列表

- 方法：`GET`
- 路径：`/api/master/products`

查询参数：

| 参数名 | 含义 |
| --- | --- |
| `pageNo` | 页码 |
| `pageSize` | 每页条数 |
| `productCode` | 商品编码 |
| `productName` | 商品名称 |
| `certificateNo` | 证书号 |
| `status` | 状态 |

示例：

```text
/api/master/products?pageNo=1&pageSize=10&productCode=P001
```

### 4.13 查询库存列表

- 方法：`GET`
- 路径：`/api/master/stocks`

查询参数：

| 参数名 | 含义 |
| --- | --- |
| `pageNo` | 页码 |
| `pageSize` | 每页条数 |
| `productCode` | 商品编码 |
| `productName` | 商品名称 |
| `batchNo` | 批次号 |
| `certificateNo` | 证书号 |

示例：

```text
/api/master/stocks?pageNo=1&pageSize=10&certificateNo=CERT-P001
```

### 4.14 查询低库存列表

- 方法：`GET`
- 路径：`/api/master/stocks/low`

查询参数与库存列表一致。

## 5. 采购接口

### 5.1 创建采购单

- 方法：`POST`
- 路径：`/api/purchases`

请求体：

```json
{
  "supplierId": 1,
  "items": [
    {
      "productId": 1,
      "warehouseId": 1,
      "batchNo": "BATCH-001",
      "certificateNo": "CERT-P001",
      "quantity": 10,
      "weight": 52.00,
      "unitPrice": 2800.00
    }
  ]
}
```

### 5.2 查询采购单列表

- 方法：`GET`
- 路径：`/api/purchases`

查询参数：

| 参数名 | 含义 |
| --- | --- |
| `pageNo` | 页码 |
| `pageSize` | 每页条数 |
| `orderNo` | 采购单号 |
| `status` | 单据状态 |
| `certificateNo` | 证书号 |
| `startDate` | 开始日期，格式 `yyyy-MM-dd` |
| `endDate` | 结束日期，格式 `yyyy-MM-dd` |

示例：

```text
/api/purchases?pageNo=1&pageSize=10&status=STOCKED
```

### 5.3 查询采购单详情

- 方法：`GET`
- 路径：`/api/purchases/{id}`

### 5.4 审核采购单

- 方法：`POST`
- 路径：`/api/purchases/{id}/approve`
- 请求体：无

### 5.5 采购入库

- 方法：`POST`
- 路径：`/api/purchases/{id}/stock-in`
- 请求体：无

## 6. 销售接口

### 6.1 创建销售单

- 方法：`POST`
- 路径：`/api/sales`

这是你当前最常用的接口之一，请求体可以直接按下面这样写：

```json
{
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "warehouseId": 1,
      "batchNo": "BATCH-001",
      "certificateNo": "CERT-P001",
      "quantity": 2,
      "weight": 10.40,
      "unitPrice": 3280.00
    }
  ]
}
```

字段说明：

| 字段名 | 含义 |
| --- | --- |
| `customerId` | 客户 ID |
| `items` | 销售明细数组 |
| `productId` | 商品 ID |
| `warehouseId` | 出库仓库 ID |
| `batchNo` | 批次号 |
| `certificateNo` | 证书号 |
| `quantity` | 销售数量 |
| `weight` | 销售克重 |
| `unitPrice` | 销售单价 |

### 6.2 查询销售单列表

- 方法：`GET`
- 路径：`/api/sales`

查询参数：

| 参数名 | 含义 |
| --- | --- |
| `pageNo` | 页码 |
| `pageSize` | 每页条数 |
| `orderNo` | 销售单号 |
| `status` | 单据状态 |
| `certificateNo` | 证书号 |
| `startDate` | 开始日期 |
| `endDate` | 结束日期 |

示例：

```text
/api/sales?pageNo=1&pageSize=10&status=APPROVED
```

### 6.3 查询销售单详情

- 方法：`GET`
- 路径：`/api/sales/{id}`

### 6.4 审核销售单并出库

- 方法：`POST`
- 路径：`/api/sales/{id}/approve`
- 请求体：无

## 7. 盘点与溯源接口

### 7.1 创建盘点单

- 方法：`POST`
- 路径：`/api/inventory/checks`

请求体：

```json
{
  "warehouseId": 1,
  "remark": "月末盘点",
  "items": [
    {
      "stockId": 1,
      "actualQuantity": 8
    }
  ]
}
```

### 7.2 查询盘点单列表

- 方法：`GET`
- 路径：`/api/inventory/checks`

### 7.3 审核盘点单

- 方法：`POST`
- 路径：`/api/inventory/checks/{id}/approve`
- 请求体：无

### 7.4 溯源查询

- 方法：`GET`
- 路径：`/api/inventory/trace`

查询参数：

| 参数名 | 含义 |
| --- | --- |
| `keyword` | 批次号、证书号或来源单号 |

示例：

```text
/api/inventory/trace?keyword=CERT-P001
```

## 8. 报表接口

### 8.1 仪表盘统计

- 方法：`GET`
- 路径：`/api/reports/dashboard`

查询参数：

| 参数名 | 含义 |
| --- | --- |
| `startDate` | 开始日期 |
| `endDate` | 结束日期 |

示例：

```text
/api/reports/dashboard?startDate=2026-03-01&endDate=2026-03-31
```

### 8.2 ABC 分类分析

- 方法：`GET`
- 路径：`/api/reports/abc`

### 8.3 库存周转率分析

- 方法：`GET`
- 路径：`/api/reports/turnover`

查询参数：

| 参数名 | 含义 |
| --- | --- |
| `startDate` | 开始日期 |
| `endDate` | 结束日期 |

示例：

```text
/api/reports/turnover?startDate=2026-03-01&endDate=2026-03-31
```

## 9. 常见联调错误说明

### 9.1 未登录或令牌缺失

原因：
- 没有在请求头里带 `Authorization`
- token 前面没有加 `Bearer `

正确写法：

```http
Authorization: Bearer xxxxxxxxxxxxxxxx
```

### 9.2 无权访问该接口

原因：
- 当前账号没有对应权限点

### 9.3 仅草稿单可审核

原因：
- 说明该单据已经审核过，重复审核被正确拦截

### 9.4 仅审核通过的采购单可入库

原因：
- 采购单还没审核
- 或已经完成入库后再次重复入库

### 9.5 库存不足

原因：
- 销售出库数量超过当前库存数量

## 10. 推荐联调顺序

建议按以下顺序调试：

1. 登录获取 token
2. 查询材质、分类、供应商、客户、仓库
3. 创建商品
4. 创建采购单
5. 审核采购单
6. 采购入库
7. 创建销售单
8. 审核销售单
9. 创建盘点单并审核
10. 查询溯源
11. 查询报表

如果你后面还想要，我可以继续再给你补一版：
- “Postman 调试版接口手册”
- 每个接口附带“成功响应示例 + 失败响应示例”
