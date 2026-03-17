<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <h3 class="page-title">盘点管理</h3>
        <p class="page-desc">发起盘点单并审核差异，形成库存调整闭环。</p>
      </div>
      <el-button v-if="canCreateCheck" type="primary" @click="openDialog">新建盘点单</el-button>
    </div>

    <div class="summary-grid">
      <div class="summary-item">
        <span>盘点单数</span>
        <strong>{{ checks.length }}</strong>
      </div>
      <div class="summary-item">
        <span>待审核</span>
        <strong>{{ pendingCount }}</strong>
      </div>
      <div class="summary-item">
        <span>库存样本</span>
        <strong>{{ stockOptions.length }}</strong>
      </div>
    </div>

    <el-table :data="checks" border>
      <el-table-column prop="checkNo" label="盘点单号" min-width="180" />
      <el-table-column prop="warehouseId" label="仓库ID" min-width="100" />
      <el-table-column prop="remark" label="备注" min-width="220" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" min-width="120">
        <template #default="{ row }">
          <el-tag :type="row.status === 'APPROVED' ? 'success' : 'warning'">
            {{ row.status === 'APPROVED' ? '已审核' : '待审核' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" min-width="180" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button
            v-if="row.status !== 'APPROVED' && canApproveCheck"
            size="small"
            type="primary"
            @click="approve(row.id)"
          >
            审核
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="新建盘点单" width="980px" top="4vh">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="盘点仓库">
              <el-select v-model="form.warehouseId" style="width: 100%" @change="syncCheckItems">
                <el-option v-for="item in warehouses" :key="item.id" :label="item.warehouseName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注">
              <el-input v-model="form.remark" placeholder="如：月末盘点、抽盘" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div class="toolbar">
        <el-input v-model="keyword" placeholder="筛选商品名称/证书号/批次号" clearable style="width: 260px" />
        <el-button @click="syncCheckItems">刷新盘点明细</el-button>
        <el-tag type="info">已载入 {{ filteredStocks.length }} 条库存记录</el-tag>
      </div>

      <el-table :data="filteredStocks" border max-height="420">
        <el-table-column prop="warehouseName" label="仓库" min-width="120" />
        <el-table-column prop="productCode" label="商品编码" min-width="120" />
        <el-table-column prop="productName" label="商品名称" min-width="160" />
        <el-table-column prop="batchNo" label="批次号" min-width="140" />
        <el-table-column prop="certificateNo" label="证书号" min-width="180" />
        <el-table-column prop="quantity" label="系统库存" min-width="100" />
        <el-table-column label="实盘数量" min-width="120">
          <template #default="{ row }">
            <el-input-number
              v-model="checkItemMap[row.id]"
              :min="0"
              :step="1"
              style="width: 100%"
            />
          </template>
        </el-table-column>
        <el-table-column label="差异" min-width="100">
          <template #default="{ row }">
            {{ Number(checkItemMap[row.id] ?? row.quantity) - Number(row.quantity || 0) }}
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">提交盘点单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../stores/auth'
import { hasAllPermissions, hasPermission } from '../../utils/permission'
import { approveCheckApi, createCheckApi, listChecksApi } from '../../api/inventory'
import { listStocksApi, listWarehousesApi } from '../../api/master'

const dialogVisible = ref(false)
const keyword = ref('')
const checks = ref([])
const warehouses = ref([])
const stockOptions = ref([])
const authStore = useAuthStore()
const checkItemMap = reactive({})
const form = reactive({
  warehouseId: undefined,
  remark: ''
})

const pendingCount = computed(() => checks.value.filter((item) => item.status !== 'APPROVED').length)
const canCreateCheck = computed(() =>
  hasAllPermissions(authStore.user, [
    'inventory:check:create',
    'inventory:stock:view',
    'master:warehouse:view'
  ])
)
const canApproveCheck = computed(() => hasPermission(authStore.user, 'inventory:check:approve'))

const filteredStocks = computed(() => {
  const keywordValue = keyword.value.trim().toLowerCase()
  return stockOptions.value.filter((item) => {
    const warehouseMatched = !form.warehouseId || item.warehouseId === form.warehouseId
    if (!warehouseMatched) {
      return false
    }
    if (!keywordValue) {
      return true
    }
    return [item.productName, item.productCode, item.batchNo, item.certificateNo]
      .filter(Boolean)
      .some((field) => String(field).toLowerCase().includes(keywordValue))
  })
})

function resetForm() {
  form.warehouseId = undefined
  form.remark = ''
  keyword.value = ''
  Object.keys(checkItemMap).forEach((key) => {
    delete checkItemMap[key]
  })
}

async function openDialog() {
  if (canCreateCheck.value && !warehouses.value.length) {
    const [warehouseList, stockPage] = await Promise.all([
      listWarehousesApi(),
      listStocksApi({ pageNo: 1, pageSize: 200 })
    ])
    warehouses.value = warehouseList
    stockOptions.value = stockPage.records || []
  }
  resetForm()
  dialogVisible.value = true
}

function syncCheckItems() {
  Object.keys(checkItemMap).forEach((key) => {
    delete checkItemMap[key]
  })
  filteredStocks.value.forEach((item) => {
    checkItemMap[item.id] = item.quantity
  })
}

async function loadAll() {
  const checkList = await listChecksApi()
  checks.value = checkList
}

async function submitCreate() {
  const items = filteredStocks.value.map((item) => ({
    stockId: item.id,
    actualQuantity: Number(checkItemMap[item.id] ?? item.quantity ?? 0)
  }))
  await createCheckApi({
    warehouseId: form.warehouseId,
    remark: form.remark,
    items
  })
  ElMessage.success('盘点单创建成功')
  dialogVisible.value = false
  loadAll()
}

async function approve(id) {
  await approveCheckApi(id)
  ElMessage.success('盘点单审核成功')
  loadAll()
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
  grid-template-columns: repeat(3, minmax(0, 1fr));
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
