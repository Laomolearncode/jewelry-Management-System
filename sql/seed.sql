USE jewelry_pims;

INSERT INTO sys_user(username, password, real_name, phone, status, created_at, updated_at)
VALUES ('admin', 'admin123', '系统管理员', '13800000000', 1, NOW(), NOW());

INSERT INTO sys_role(role_code, role_name, description, status)
VALUES
('ADMIN', '系统管理员', '拥有全部权限', 1),
('PURCHASER', '采购员', '负责采购业务', 1),
('WAREHOUSE_KEEPER', '仓管员', '负责仓储盘点', 1),
('SALESMAN', '销售员', '负责销售业务', 1);

INSERT INTO sys_permission(permission_code, permission_name, description)
VALUES
('system:user:create', '创建用户', '系统用户创建'),
('system:user:view', '查看用户', '系统用户查看'),
('system:user:update', '更新用户', '系统用户更新'),
('system:role:view', '查看角色', '系统角色查看'),
('system:permission:view', '查看权限', '系统权限查看'),
('system:log:view', '查看日志', '系统操作日志查看'),
('master:material:create', '创建材质', '基础材质创建'),
('master:material:view', '查看材质', '基础材质查看'),
('master:category:create', '创建分类', '商品分类创建'),
('master:category:view', '查看分类', '商品分类查看'),
('master:supplier:create', '创建供应商', '供应商创建'),
('master:supplier:view', '查看供应商', '供应商查看'),
('master:customer:create', '创建客户', '客户创建'),
('master:customer:view', '查看客户', '客户查看'),
('master:warehouse:create', '创建仓库', '仓库创建'),
('master:warehouse:view', '查看仓库', '仓库查看'),
('master:product:create', '创建商品', '商品创建'),
('master:product:view', '查看商品', '商品查看'),
('purchase:order:create', '创建采购单', '采购单创建'),
('purchase:order:view', '查看采购单', '采购单查看'),
('purchase:order:approve', '审核采购单', '采购单审核'),
('inventory:stock:view', '查看库存', '库存查看'),
('inventory:stockin:create', '采购入库', '采购入库'),
('inventory:check:create', '创建盘点', '盘点创建'),
('inventory:check:view', '查看盘点', '盘点查看'),
('inventory:check:approve', '审核盘点', '盘点审核'),
('inventory:trace:view', '查看溯源', '溯源查看'),
('sales:order:create', '创建销售单', '销售单创建'),
('sales:order:view', '查看销售单', '销售单查看'),
('sales:order:approve', '审核销售单', '销售单审核'),
('report:dashboard:view', '查看仪表盘', '仪表盘查看'),
('report:abc:view', '查看ABC分析', 'ABC分析查看'),
('report:turnover:view', '查看周转率', '周转率查看');

INSERT INTO sys_user_role(user_id, role_id)
VALUES (1, 1);

INSERT INTO sys_role_permission(role_id, permission_id)
SELECT 1, id FROM sys_permission;

INSERT INTO material_dict(material_code, material_name, description, status)
VALUES
('AU', '黄金', '贵金属黄金材质', 1),
('AG', '白银', '贵金属白银材质', 1),
('PEARL', '珍珠', '天然珍珠材质', 1);

INSERT INTO product_category(category_code, category_name, description, status)
VALUES
('RING', '戒指', '戒指类饰品', 1),
('NECKLACE', '项链', '项链类饰品', 1),
('EARRING', '耳饰', '耳饰类饰品', 1);

INSERT INTO supplier(supplier_code, supplier_name, contact_name, phone, address, status)
VALUES ('SUP001', '示例供应商', '李经理', '13900000000', '广州番禺', 1);

INSERT INTO customer(customer_code, customer_name, contact_name, phone, address, status)
VALUES ('CUS001', '示例客户', '张小姐', '13700000000', '广州天河', 1);

INSERT INTO warehouse(warehouse_code, warehouse_name, location, status)
VALUES ('WH001', '总仓', '广州市仓储中心', 1);
