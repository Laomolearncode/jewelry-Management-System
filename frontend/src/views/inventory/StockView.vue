<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <h3 class="page-title">库存台账</h3>
        <p class="page-desc">按商品、批次和证书号追踪库存，并识别低库存项目。</p>
      </div>
    </div>

    <div class="summary-grid">
      <div class="summary-item">
        <span>当前页记录</span>
        <strong>{{ (tableData.records || []).length }}</strong>
      </div>
      <div class="summary-item">
        <span>库存总量</span>
        <strong>{{ quantityTotal }}</strong>
      </div>
      <div class="summary-item">
        <span>低库存条目</span>
        <strong>{{ lowStockCount }}</strong>
      </div>
      <div class="summary-item">
        <span>成本金额</span>
        <strong>{{ amountTotal }}</strong>
      </div>
    </div>

    <div class="toolbar">
      <el-input v-model="query.productCode" placeholder="商品编码" clearable style="width: 180px" />
      <el-input v-model="query.productName" placeholder="商品名称" clearable style="width: 180px" />
      <el-input v-model="query.batchNo" placeholder="批次号" clearable style="width: 180px" />
      <el-input v-model="query.certificateNo" placeholder="证书号" clearable style="width: 220px" />
      <el-switch v-model="lowOnly" active-text="仅看低库存" />
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetQuery">重置</el-button>
    </div>

    <el-table :data="tableData.records || []" border>
      <el-table-column prop="warehouseName" label="仓库" min-width="120" />
      <el-table-column prop="productCode" label="商品编码" min-width="120" />
      <el-table-column prop="productName" label="商品名称" min-width="160" />
      <el-table-column prop="batchNo" label="批次号" min-width="140" />
      <el-table-column prop="certificateNo" label="证书号" min-width="180" />
      <el-table-column prop="quantity" label="库存数量" min-width="100" />
      <el-table-column prop="warningThreshold" label="预警值" min-width="100" />
      <el-table-column prop="avgCostPrice" label="平均成本价" min-width="120" />
      <el-table-column prop="updatedAt" label="最近变更时间" min-width="180" />
      <el-table-column label="预警状态" min-width="110">
        <template #default="{ row }">
          <el-tag :type="Number(row.quantity || 0) <= Number(row.warningThreshold || 0) ? 'danger' : 'success'">
            {{ Number(row.quantity || 0) <= Number(row.warningThreshold || 0) ? '低库存' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      style="margin-top: 16px; justify-content: flex-end"
      background
      layout="total, prev, pager, next"
      :current-page="query.pageNo"
      :page-size="query.pageSize"
      :total="tableData.total || 0"
      @current-change="handlePageChange"
    />
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { listLowStocksApi, listStocksApi } from '../../api/master'

const lowOnly = ref(false)
const tableData = ref({ total: 0, records: [] })
const query = reactive({
  pageNo: 1,
  pageSize: 10,
  productCode: '',
  productName: '',
  batchNo: '',
  certificateNo: ''
})

const quantityTotal = computed(() =>
  (tableData.value.records || []).reduce((sum, item) => sum + Number(item.quantity || 0), 0)
)

const lowStockCount = computed(
  () =>
    (tableData.value.records || []).filter(
      (item) => Number(item.quantity || 0) <= Number(item.warningThreshold || 0)
    ).length
)

const amountTotal = computed(() =>
  (tableData.value.records || [])
    .reduce((sum, item) => sum + Number(item.quantity || 0) * Number(item.avgCostPrice || 0), 0)
    .toFixed(2)
)

async function loadData() {
  tableData.value = lowOnly.value ? await listLowStocksApi(query) : await listStocksApi(query)
}

function handlePageChange(pageNo) {
  query.pageNo = pageNo
  loadData()
}

function resetQuery() {
  Object.assign(query, {
    pageNo: 1,
    pageSize: 10,
    productCode: '',
    productName: '',
    batchNo: '',
    certificateNo: ''
  })
  lowOnly.value = false
  loadData()
}

loadData()
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
</style>
