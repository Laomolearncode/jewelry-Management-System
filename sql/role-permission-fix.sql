USE jewelry_pims;

INSERT INTO sys_role_permission(role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.permission_code IN (
    'report:dashboard:view',
    'master:material:view',
    'master:category:view',
    'master:supplier:view',
    'master:warehouse:view',
    'master:product:view',
    'purchase:order:create',
    'purchase:order:view',
    'purchase:order:approve'
)
WHERE r.role_code = 'PURCHASER'
  AND NOT EXISTS (
    SELECT 1
    FROM sys_role_permission rp
    WHERE rp.role_id = r.id
      AND rp.permission_id = p.id
  );

INSERT INTO sys_role_permission(role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.permission_code IN (
    'report:dashboard:view',
    'master:warehouse:view',
    'master:product:view',
    'inventory:stock:view',
    'inventory:stockin:create',
    'inventory:check:create',
    'inventory:check:view',
    'inventory:check:approve',
    'inventory:trace:view'
)
WHERE r.role_code = 'WAREHOUSE_KEEPER'
  AND NOT EXISTS (
    SELECT 1
    FROM sys_role_permission rp
    WHERE rp.role_id = r.id
      AND rp.permission_id = p.id
  );

INSERT INTO sys_role_permission(role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p ON p.permission_code IN (
    'report:dashboard:view',
    'master:customer:view',
    'master:warehouse:view',
    'master:product:view',
    'inventory:stock:view',
    'sales:order:create',
    'sales:order:view',
    'sales:order:approve'
)
WHERE r.role_code = 'SALESMAN'
  AND NOT EXISTS (
    SELECT 1
    FROM sys_role_permission rp
    WHERE rp.role_id = r.id
      AND rp.permission_id = p.id
  );
