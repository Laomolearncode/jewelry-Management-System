<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <h3 class="page-title">销售管理</h3>
        <p class="page-desc">支持多商品销售开单、审核出库和毛利查看。</p>
      </div>
      <el-button v-if="canCreateSales" type="primary" @click="openDialog">新建销售单</el-button>
    </div>

    <div class="summary-grid">
      <div class="summary-item">
        <span>销售单总数</span>
        <strong>{{ tableData.total || 0 }}</strong>
      </div>
      <div class="summary-item">
        <span>草稿数</span>
        <strong>{{ statusCount.DRAFT }}</strong>
      </div>
      <div class="summary-item">
        <span>已审核</span>
        <strong>{{ statusCount.APPROVED }}</strong>
      </div>
      <div class="summary-item">
        <span>本页毛利</span>
        <strong>{{ currentGrossProfit }}</strong>
      </div>
    </div>

    <div class="toolbar">
      <el-input v-model="query.orderNo" placeholder="销售单号" clearable style="width: 180px" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 160px">
        <el-option label="草稿" value="DRAFT" />
        <el-option label="已审核" value="APPROVED" />
      </el-select>
      <el-input v-model="query.certificateNo" placeholder="证书号" clearable style="width: 220px" />
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetQuery">重置</el-button>
    </div>

    <el-table :data="tableData.records || []" border>
      <el-table-column prop="orderNo" label="销售单号" min-width="180" />
      <el-table-column prop="customerName" label="客户" min-width="140" />
      <el-table-column prop="status" label="状态" min-width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'APPROVED' ? 'success' : 'info'">
            {{ row.status === 'APPROVED' ? '已审核' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="totalAmount" label="销售金额" min-width="120" />
      <el-table-column prop="totalCost" label="销售成本" min-width="120" />
      <el-table-column label="毛利" min-width="110">
        <template #default="{ row }">
          {{ formatAmount(Number(row.totalAmount || 0) - Number(row.totalCost || 0)) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="180">
        <template #default="{ row }">
          <div class="action-row">
            <el-button size="small" @click="showDetail(row.id)">详情</el-button>
            <el-button v-if="row.status === 'DRAFT' && canApproveSales" size="small" type="primary" @click="approve(row.id)">审核出库</el-button>
          </div>
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

    <el-dialog v-model="dialogVisible" title="新建销售单" width="1100px" top="4vh">
      <el-form :model="form" label-width="100px">
        <el-form-item label="客户">
          <el-select v-model="form.customerId" style="width: 100%">
            <el-option v-for="item in customers" :key="item.id" :label="item.customerName" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button @click="addItem">新增明细行</el-button>
        <el-tag type="info">明细 {{ form.items.length }} 行</el-tag>
        <el-tag type="success">预计销售额 {{ estimatedAmount }}</el-tag>
      </div>

      <el-table :data="form.items" border>
        <el-table-column label="商品" min-width="220">
          <template #default="{ row }">
            <el-select v-model="row.productId" filterable style="width: 100%">
              <el-option
                v-for="item in products"
                :key="item.id"
                :label="`${item.productCode} - ${item.productName}`"
                :value="item.id"
              />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="仓库" min-width="160">
          <template #default="{ row }">
            <el-select v-model="row.warehouseId" style="width: 100%">
              <el-option v-for="item in warehouses" :key="item.id" :label="item.warehouseName" :value="item.id" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="批次号" min-width="140">
          <template #default="{ row }">
            <el-input v-model="row.batchNo" />
          </template>
        </el-table-column>
        <el-table-column label="证书号" min-width="160">
          <template #default="{ row }">
            <el-input v-model="row.certificateNo" />
          </template>
        </el-table-column>
        <el-table-column label="数量" min-width="100">
          <template #default="{ row }">
            <el-input-number v-model="row.quantity" :min="1" style="width: 100%" />
          </template>
        </el-table-column>
        <el-table-column label="克重" min-width="120">
          <template #default="{ row }">
            <el-input-number v-model="row.weight" :min="0.01" :precision="2" style="width: 100%" />
          </template>
        </el-table-column>
        <el-table-column label="销售单价" min-width="120">
          <template #default="{ row }">
            <el-input-number v-model="row.unitPrice" :min="0.01" :precision="2" style="width: 100%" />
          </template>
        </el-table-column>
        <el-table-column label="小计" min-width="120">
          <template #default="{ row }">
            {{ formatAmount(row.quantity * row.unitPrice) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="90">
          <template #default="{ $index }">
            <el-button link type="danger" @click="removeItem($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">提交</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" title="销售单详情" size="55%">
      <template v-if="detailData.orderNo">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="销售单号">{{ detailData.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="客户">{{ detailData.customerName }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ detailData.status }}</el-descriptions-item>
          <el-descriptions-item label="销售金额">{{ detailData.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="销售成本">{{ detailData.totalCost }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ detailData.createdAt }}</el-descriptions-item>
        </el-descriptions>
        <el-divider>销售明细</el-divider>
        <el-table :data="detailData.items || []" border>
          <el-table-column prop="productId" label="商品ID" min-width="100" />
          <el-table-column prop="warehouseId" label="仓库ID" min-width="100" />
          <el-table-column prop="batchNo" label="批次号" min-width="140" />
          <el-table-column prop="certificateNo" label="证书号" min-width="180" />
          <el-table-column prop="quantity" label="数量" min-width="90" />
          <el-table-column prop="unitPrice" label="销售单价" min-width="110" />
          <el-table-column prop="unitCost" label="单位成本" min-width="110" />
          <el-table-column prop="subtotalAmount" label="小计" min-width="100" />
        </el-table>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../stores/auth'
import { hasAllPermissions, hasPermission } from '../../utils/permission'
import { listCustomersApi, listProductsApi, listWarehousesApi } from '../../api/master'
import { approveSalesApi, createSalesApi, getSalesApi, listSalesApi } from '../../api/sales'

const query = reactive({
  pageNo: 1,
  pageSize: 10,
  orderNo: '',
  status: '',
  certificateNo: ''
})

const tableData = ref({ total: 0, records: [] })
const customers = ref([])
const products = ref([])
const warehouses = ref([])
const authStore = useAuthStore()
const dialogVisible = ref(false)
const detailVisible = ref(false)
const detailData = ref({})
const form = reactive(createDefaultForm())

const statusCount = computed(() =>
  (tableData.value.records || []).reduce(
    (acc, item) => {
      acc[item.status] = (acc[item.status] || 0) + 1
      return acc
    },
    { DRAFT: 0, APPROVED: 0 }
  )
)

const estimatedAmount = computed(() =>
  formatAmount(form.items.reduce((sum, item) => sum + Number(item.quantity || 0) * Number(item.unitPrice || 0), 0))
)

const currentGrossProfit = computed(() => {
  const amount = (tableData.value.records || []).reduce((sum, item) => sum + Number(item.totalAmount || 0), 0)
  const cost = (tableData.value.records || []).reduce((sum, item) => sum + Number(item.totalCost || 0), 0)
  return formatAmount(amount - cost)
})

const canCreateSales = computed(() =>
  hasAllPermissions(authStore.user, [
    'sales:order:create',
    'master:customer:view',
    'master:product:view',
    'master:warehouse:view'
  ])
)
const canApproveSales = computed(() => hasPermission(authStore.user, 'sales:order:approve'))

function createItemForm() {
  return {
    productId: undefined,
    warehouseId: undefined,
    batchNo: '',
    certificateNo: '',
    quantity: 1,
    weight: 1,
    unitPrice: 1
  }
}

function createDefaultForm() {
  return {
    customerId: undefined,
    items: [createItemForm()]
  }
}

function formatAmount(value) {
  return Number(value || 0).toFixed(2)
}

function resetForm() {
  Object.assign(form, createDefaultForm())
}

function resetQuery() {
  Object.assign(query, {
    pageNo: 1,
    pageSize: 10,
    orderNo: '',
    status: '',
    certificateNo: ''
  })
  loadData()
}

async function loadOptions() {
  if (!canCreateSales.value) {
    return
  }
  customers.value = await listCustomersApi()
  products.value = (await listProductsApi({ pageNo: 1, pageSize: 100 })).records || []
  warehouses.value = await listWarehousesApi()
}

async function loadData() {
  tableData.value = await listSalesApi(query)
}

function handlePageChange(pageNo) {
  query.pageNo = pageNo
  loadData()
}

function addItem() {
  form.items.push(createItemForm())
}

function removeItem(index) {
  if (form.items.length === 1) {
    return
  }
  form.items.splice(index, 1)
}

async function openDialog() {
  await loadOptions()
  resetForm()
  dialogVisible.value = true
}

async function submitCreate() {
  await createSalesApi({
    customerId: form.customerId,
    items: form.items.map((item) => ({ ...item }))
  })
  ElMessage.success('销售单创建成功')
  dialogVisible.value = false
  loadData()
}

async function approve(id) {
  await approveSalesApi(id)
  ElMessage.success('销售单审核成功')
  loadData()
}

async function showDetail(id) {
  detailData.value = await getSalesApi(id)
  detailVisible.value = true
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
