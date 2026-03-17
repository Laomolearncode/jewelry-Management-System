import { hasAllPermissions, hasPermission } from './permission'

const routeAccessRules = {
  '/dashboard': { permission: 'report:dashboard:view' },
  '/purchase/orders': { permission: 'purchase:order:view' },
  '/sales/orders': { permission: 'sales:order:view' },
  '/inventory/stocks': { permission: 'inventory:stock:view' },
  '/inventory/checks': { permission: 'inventory:check:view' },
  '/inventory/trace': { permission: 'inventory:trace:view' },
  '/master/products': { permission: 'master:product:view' },
  '/master/basic': {
    permissionsAll: [
      'master:material:view',
      'master:category:view',
      'master:supplier:view',
      'master:customer:view',
      'master:warehouse:view'
    ]
  },
  '/reports/analysis': {
    permissionsAll: ['report:dashboard:view', 'report:abc:view', 'report:turnover:view']
  },
  '/system/users': {
    permissionsAll: ['system:user:view', 'system:role:view', 'system:permission:view', 'system:log:view']
  }
}

const preferredPaths = Object.keys(routeAccessRules)

export function hasRouteAccess(user, route) {
  if (!route) {
    return false
  }
  if (route.meta?.permission && !hasPermission(user, route.meta.permission)) {
    return false
  }
  if (route.meta?.permissionsAll && !hasAllPermissions(user, route.meta.permissionsAll)) {
    return false
  }
  return true
}

export function getFirstAccessiblePath(user) {
  return (
    preferredPaths.find((path) => {
      const rule = routeAccessRules[path]
      if (rule.permission) {
        return hasPermission(user, rule.permission)
      }
      if (rule.permissionsAll) {
        return hasAllPermissions(user, rule.permissionsAll)
      }
      return false
    }) || '/login'
  )
}
