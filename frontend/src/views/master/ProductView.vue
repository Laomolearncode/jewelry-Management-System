<template>
  <div class="page-card">
    <div class="page-header">
      <h3 class="page-title">商品管理</h3>
      <el-button v-if="canCreateProduct" type="primary" @click="openCreateDialog">新增商品</el-button>
    </div>

    <div class="toolbar">
      <el-input v-model="query.productCode" placeholder="商品编码" clearable style="width: 180px" />
      <el-input v-model="query.productName" placeholder="商品名称" clearable style="width: 220px" />
      <el-input v-model="query.certificateNo" placeholder="证书号" clearable style="width: 220px" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 140px">
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button type="primary" @click="loadProducts">查询</el-button>
      <el-button @click="resetQuery">重置</el-button>
    </div>

    <el-table :data="tableData.records || []" border>
      <el-table-column prop="productCode" label="商品编码" min-width="120" />
      <el-table-column prop="productName" label="商品名称" min-width="160" />
      <el-table-column prop="categoryName" label="分类" min-width="120" />
      <el-table-column prop="materialName" label="材质" min-width="120" />
      <el-table-column prop="weight" label="克重" min-width="100" />
      <el-table-column prop="costPrice" label="成本价" min-width="100" />
      <el-table-column prop="salePrice" label="销售价" min-width="100" />
      <el-table-column prop="certificateNo" label="证书号" min-width="180" />
      <el-table-column prop="status" label="状态" min-width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
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

    <el-dialog v-model="dialogVisible" title="新增商品" width="720px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="商品编码"><el-input v-model="form.productCode" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="商品名称"><el-input v-model="form.productName" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="分类">
              <el-select v-model="form.categoryId" style="width: 100%">
                <el-option v-for="item in categories" :key="item.id" :label="item.categoryName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="材质">
              <el-select v-model="form.materialId" style="width: 100%">
                <el-option v-for="item in materials" :key="item.id" :label="item.materialName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12"><el-form-item label="品牌"><el-input v-model="form.brand" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="单位"><el-input v-model="form.unit" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="克重"><el-input-number v-model="form.weight" :min="0.01" :precision="2" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="成本价"><el-input-number v-model="form.costPrice" :min="0" :precision="2" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="销售价"><el-input-number v-model="form.salePrice" :min="0" :precision="2" style="width: 100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="证书号"><el-input v-model="form.certificateNo" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="预警值"><el-input-number v-model="form.warningThreshold" :min="0" style="width: 100%" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../stores/auth'
import { hasAllPermissions, hasPermission } from '../../utils/permission'
import { createProductApi, listCategoriesApi, listMaterialsApi, listProductsApi } from '../../api/master'

const dialogVisible = ref(false)
const tableData = ref({ total: 0, records: [] })
const materials = ref([])
const categories = ref([])
const authStore = useAuthStore()
const canCreateProduct = computed(() => hasPermission(authStore.user, 'master:product:create'))
const canLoadBaseOptions = computed(() =>
  hasAllPermissions(authStore.user, ['master:material:view', 'master:category:view'])
)

const query = reactive({
  pageNo: 1,
  pageSize: 10,
  productCode: '',
  productName: '',
  certificateNo: '',
  status: undefined
})

const form = reactive(createDefaultForm())

function createDefaultForm() {
  return {
    productCode: '',
    productName: '',
    categoryId: undefined,
    materialId: undefined,
    brand: '',
    unit: '件',
    weight: 1,
    costPrice: 0,
    salePrice: 0,
    certificateNo: '',
    warningThreshold: 0,
    status: 1
  }
}

function resetForm() {
  Object.assign(form, createDefaultForm())
}

async function loadBaseOptions() {
  if (!canLoadBaseOptions.value) {
    return
  }
  materials.value = await listMaterialsApi()
  categories.value = await listCategoriesApi()
}

async function loadProducts() {
  tableData.value = await listProductsApi(query)
}

function resetQuery() {
  Object.assign(query, {
    pageNo: 1,
    pageSize: 10,
    productCode: '',
    productName: '',
    certificateNo: '',
    status: undefined
  })
  loadProducts()
}

function handlePageChange(pageNo) {
  query.pageNo = pageNo
  loadProducts()
}

async function openCreateDialog() {
  await loadBaseOptions()
  resetForm()
  dialogVisible.value = true
}

async function submitCreate() {
  await createProductApi(form)
  ElMessage.success('商品创建成功')
  dialogVisible.value = false
  loadProducts()
}

loadProducts()
</script>
