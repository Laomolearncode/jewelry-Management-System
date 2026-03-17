import { createRouter, createWebHashHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getFirstAccessiblePath, hasRouteAccess } from '../utils/route-access'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/LoginView.vue'),
    meta: { public: true, title: '登录' }
  },
  {
    path: '/',
    component: () => import('../layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/DashboardView.vue'),
        meta: { title: '仪表盘', permission: 'report:dashboard:view' }
      },
      {
        path: 'system/users',
        name: 'SystemUsers',
        component: () => import('../views/system/UserView.vue'),
        meta: {
          title: '系统管理',
          permissionsAll: ['system:user:view', 'system:role:view', 'system:permission:view', 'system:log:view']
        }
      },
      {
        path: 'master/basic',
        name: 'BasicData',
        component: () => import('../views/master/BasicDataView.vue'),
        meta: {
          title: '基础资料',
          permissionsAll: [
            'master:material:view',
            'master:category:view',
            'master:supplier:view',
            'master:customer:view',
            'master:warehouse:view'
          ]
        }
      },
      {
        path: 'master/products',
        name: 'Products',
        component: () => import('../views/master/ProductView.vue'),
        meta: { title: '商品管理', permission: 'master:product:view' }
      },
      {
        path: 'purchase/orders',
        name: 'PurchaseOrders',
        component: () => import('../views/purchase/PurchaseView.vue'),
        meta: { title: '采购管理', permission: 'purchase:order:view' }
      },
      {
        path: 'sales/orders',
        name: 'SalesOrders',
        component: () => import('../views/sales/SalesView.vue'),
        meta: { title: '销售管理', permission: 'sales:order:view' }
      },
      {
        path: 'inventory/stocks',
        name: 'Stocks',
        component: () => import('../views/inventory/StockView.vue'),
        meta: { title: '库存管理', permission: 'inventory:stock:view' }
      },
      {
        path: 'inventory/checks',
        name: 'StockChecks',
        component: () => import('../views/inventory/CheckView.vue'),
        meta: { title: '盘点管理', permission: 'inventory:check:view' }
      },
      {
        path: 'inventory/trace',
        name: 'Trace',
        component: () => import('../views/inventory/TraceView.vue'),
        meta: { title: '盘点溯源', permission: 'inventory:trace:view' }
      },
      {
        path: 'reports/analysis',
        name: 'Reports',
        component: () => import('../views/report/ReportView.vue'),
        meta: {
          title: '统计分析',
          permissionsAll: ['report:dashboard:view', 'report:abc:view', 'report:turnover:view']
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()
  if (to.meta.public) {
    if (to.path === '/login' && authStore.isLoggedIn && authStore.user) {
      const targetPath = getFirstAccessiblePath(authStore.user)
      if (targetPath !== '/login') {
        return targetPath
      }
    }
    return true
  }
  if (!authStore.isLoggedIn) {
    return '/login'
  }
  if (!authStore.user) {
    try {
      await authStore.fetchProfile()
    } catch (error) {
      authStore.reset()
      return '/login'
    }
  }
  if (!hasRouteAccess(authStore.user, to)) {
    const targetPath = getFirstAccessiblePath(authStore.user)
    return targetPath === '/login' ? '/login' : targetPath
  }
  return true
})

export default router
