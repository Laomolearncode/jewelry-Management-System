<template>
  <div class="page-card">
    <div class="page-header">
      <h3 class="page-title">经营仪表盘</h3>
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        @change="loadDashboard"
      />
    </div>

    <div class="stat-grid">
      <div class="stat-card">
        <h3>商品总数</h3>
        <p>{{ dashboard.productCount || 0 }}</p>
      </div>
      <div class="stat-card">
        <h3>库存总量</h3>
        <p>{{ dashboard.stockQuantity || 0 }}</p>
      </div>
      <div class="stat-card">
        <h3>采购金额</h3>
        <p>{{ formatAmount(dashboard.purchaseAmount) }}</p>
      </div>
      <div class="stat-card">
        <h3>销售金额</h3>
        <p>{{ formatAmount(dashboard.saleAmount) }}</p>
      </div>
    </div>

    <el-row :gutter="16" style="margin-top: 20px;">
      <el-col :span="12">
        <div class="page-card">
          <h3 class="page-title">库存金额与毛利</h3>
          <p>库存金额：{{ formatAmount(dashboard.stockAmount) }}</p>
          <p>毛利估算：{{ formatAmount(dashboard.grossProfit) }}</p>
          <p>低库存条目：{{ dashboard.lowStockCount || 0 }}</p>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="page-card">
          <h3 class="page-title">系统说明</h3>
          <p>前端已接入后端仪表盘接口和登录鉴权逻辑。</p>
          <p>建议后续补充图表组件以展示采购、销售和周转趋势。</p>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { dashboardApi } from '../../api/report'

const now = new Date()
const firstDay = new Date(now.getFullYear(), now.getMonth(), 1)
const dateRange = ref([formatDate(firstDay), formatDate(now)])
const dashboard = ref({})

function formatDate(date) {
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  return `${year}-${month}-${day}`
}

function formatAmount(value) {
  return Number(value || 0).toFixed(2)
}

async function loadDashboard() {
  const [startDate, endDate] = dateRange.value || []
  dashboard.value = await dashboardApi({ startDate, endDate })
}

loadDashboard()
</script>
