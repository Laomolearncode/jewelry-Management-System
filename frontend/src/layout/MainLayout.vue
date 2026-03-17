<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="layout-aside">
      <div class="logo">饰品进销存系统</div>
      <el-menu
        :default-active="activeMenu"
        class="layout-menu"
        router
        :default-openeds="['system', 'master', 'business', 'inventory', 'report']"
        background-color="#001529"
        text-color="#cbd5e1"
        active-text-color="#409eff"
      >
        <el-menu-item v-if="can('report:dashboard:view')" index="/dashboard">
          <el-icon><DataBoard /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-sub-menu v-if="canAll(['system:user:view', 'system:role:view', 'system:permission:view', 'system:log:view'])" index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/users">用户权限</el-menu-item>
        </el-sub-menu>
        <el-sub-menu v-if="showMasterMenu" index="master">
          <template #title>
            <el-icon><Goods /></el-icon>
            <span>基础资料</span>
          </template>
          <el-menu-item v-if="canAll(['master:material:view', 'master:category:view', 'master:supplier:view', 'master:customer:view', 'master:warehouse:view'])" index="/master/basic">字典资料</el-menu-item>
          <el-menu-item v-if="can('master:product:view')" index="/master/products">商品档案</el-menu-item>
        </el-sub-menu>
        <el-sub-menu v-if="can('purchase:order:view') || can('sales:order:view')" index="business">
          <template #title>
            <el-icon><ShoppingBag /></el-icon>
            <span>业务单据</span>
          </template>
          <el-menu-item v-if="can('purchase:order:view')" index="/purchase/orders">采购管理</el-menu-item>
          <el-menu-item v-if="can('sales:order:view')" index="/sales/orders">销售管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu v-if="can('inventory:stock:view') || can('inventory:check:view') || can('inventory:trace:view')" index="inventory">
          <template #title>
            <el-icon><Box /></el-icon>
            <span>库存管理</span>
          </template>
          <el-menu-item v-if="can('inventory:stock:view')" index="/inventory/stocks">库存台账</el-menu-item>
          <el-menu-item v-if="can('inventory:check:view')" index="/inventory/checks">盘点管理</el-menu-item>
          <el-menu-item v-if="can('inventory:trace:view')" index="/inventory/trace">库存溯源</el-menu-item>
        </el-sub-menu>
        <el-sub-menu v-if="canAll(['report:dashboard:view', 'report:abc:view', 'report:turnover:view'])" index="report">
          <template #title>
            <el-icon><TrendCharts /></el-icon>
            <span>统计分析</span>
          </template>
          <el-menu-item index="/reports/analysis">经营报表</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div>
          <h2>{{ route.meta.title || '后台管理' }}</h2>
          <p>基于 Vue 3 + Element Plus 的饰品进销存管理后台</p>
        </div>
        <div class="header-actions">
          <span>{{ authStore.user?.realName || authStore.user?.username }}</span>
          <el-button type="danger" plain @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { hasAllPermissions, hasPermission } from '../utils/permission'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const activeMenu = computed(() => route.path)
const currentUser = computed(() => authStore.user)
const showMasterMenu = computed(
  () =>
    canAll(['master:material:view', 'master:category:view', 'master:supplier:view', 'master:customer:view', 'master:warehouse:view']) ||
    can('master:product:view')
)

function can(permission) {
  return hasPermission(currentUser.value, permission)
}

function canAll(permissions) {
  return hasAllPermissions(currentUser.value, permissions)
}

async function handleLogout() {
  await authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.layout-aside {
  background: #001529;
  color: #fff;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.layout-menu {
  border-right: none;
  height: calc(100vh - 64px);
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}

.layout-header h2 {
  margin: 0;
  font-size: 22px;
}

.layout-header p {
  margin: 6px 0 0;
  color: #64748b;
  font-size: 13px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.layout-main {
  padding: 20px;
}
</style>
