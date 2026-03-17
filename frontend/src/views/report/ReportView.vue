<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <h3 class="page-title">经营报表</h3>
        <p class="page-desc">查看经营摘要、ABC 分类和库存周转率。</p>
      </div>
      <div class="toolbar" style="margin-bottom: 0">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
        <el-button type="primary" @click="loadAll">刷新报表</el-button>
      </div>
    </div>

    <div class="summary-grid">
      <div class="summary-item">
        <span>采购金额</span>
        <strong>{{ formatAmount(dashboard.purchaseAmount) }}</strong>
      </div>
      <div class="summary-item">
        <span>销售金额</span>
        <strong>{{ formatAmount(dashboard.saleAmount) }}</strong>
      </div>
      <div class="summary-item">
        <span>毛利估算</span>
        <strong>{{ formatAmount(dashboard.grossProfit) }}</strong>
      </div>
      <div class="summary-item">
        <span>低库存数</span>
        <strong>{{ dashboard.lowStockCount || 0 }}</strong>
      </div>
    </div>

    <el-row :gutter="16">
      <el-col :span="8">
        <div class="page-card">
          <h3 class="page-title">仪表盘摘要</h3>
          <p>采购金额：{{ formatAmount(dashboard.purchaseAmount) }}</p>
          <p>销售金额：{{ formatAmount(dashboard.saleAmount) }}</p>
          <p>毛利估算：{{ formatAmount(dashboard.grossProfit) }}</p>
          <p>低库存数：{{ dashboard.lowStockCount || 0 }}</p>
          <p>库存商品数：{{ dashboard.productCount || 0 }}</p>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="page-card">
          <h3 class="page-title">ABC 分类</h3>
          <el-table :data="abcList" size="small" max-height="320">
            <el-table-column prop="productCode" label="商品编码" min-width="110" />
            <el-table-column prop="abcClass" label="分类" min-width="80" />
            <el-table-column prop="stockAmount" label="库存金额" min-width="120" />
          </el-table>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="page-card">
          <h3 class="page-title">周转率</h3>
          <el-table :data="turnoverList" size="small" max-height="320">
            <el-table-column prop="period" label="期间" min-width="100" />
            <el-table-column prop="salesCost" label="销售成本" min-width="120" />
            <el-table-column prop="averageInventoryCost" label="平均库存成本" min-width="120" />
            <el-table-column prop="turnoverRate" label="周转率" min-width="100" />
          </el-table>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <div class="page-card">
          <h3 class="page-title">ABC 占比观察</h3>
          <div v-for="item in abcSummary" :key="item.label" class="progress-row">
            <div class="progress-label">
              <span>{{ item.label }}</span>
              <span>{{ item.count }} 项</span>
            </div>
            <el-progress :percentage="item.percent" :stroke-width="12" />
          </div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="page-card">
          <h3 class="page-title">经营建议</h3>
          <p>高价值 A 类商品建议优先补货并提升陈列关注度。</p>
          <p>若周转率持续偏低，可结合低库存数和销售单据复核商品结构。</p>
          <p>当前报表已接入后端日期范围筛选，可用于论文演示和业务复盘。</p>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { abcApi, dashboardApi, turnoverApi } from '../../api/report'

const dashboard = ref({})
const abcList = ref([])
const turnoverList = ref([])
const dateRange = ref([])

const abcSummary = computed(() => {
  const total = abcList.value.length || 1
  return ['A', 'B', 'C'].map((label) => {
    const count = abcList.value.filter((item) => item.abcClass === label).length
    return {
      label: `${label} 类商品`,
      count,
      percent: Math.round((count / total) * 100)
    }
  })
})

function formatAmount(value) {
  return Number(value || 0).toFixed(2)
}

async function loadAll() {
  const [startDate, endDate] = dateRange.value || []
  dashboard.value = await dashboardApi({ startDate, endDate })
  abcList.value = await abcApi()
  turnoverList.value = await turnoverApi({ startDate, endDate })
}

loadAll()
</script>

<style scoped>
.page-desc {
  margin: 8px 0 0;
  color: #64748b;
  font-size: 13px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 18px;
}

.summary-item {
  padding: 16px 18px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #f8fafc;
}

.summary-item span {
  display: block;
  color: #64748b;
  font-size: 13px;
}

.summary-item strong {
  display: block;
  margin-top: 8px;
  font-size: 24px;
}

.progress-row + .progress-row {
  margin-top: 14px;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
  color: #475569;
  font-size: 13px;
}
</style>
