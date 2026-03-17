<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <h3 class="page-title">基础资料</h3>
        <p class="page-desc">统一维护材质、分类、供应商、客户和仓库档案。</p>
      </div>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="材质" name="materials">
        <div class="page-section">
          <div class="toolbar">
            <el-button type="primary" @click="openDialog('materials')">新增材质</el-button>
            <el-tag>共 {{ materials.length }} 条</el-tag>
          </div>
          <el-table :data="materials" border>
            <el-table-column prop="materialCode" label="材质编码" min-width="140" />
            <el-table-column prop="materialName" label="材质名称" min-width="160" />
            <el-table-column prop="description" label="说明" min-width="220" show-overflow-tooltip />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="分类" name="categories">
        <div class="page-section">
          <div class="toolbar">
            <el-button type="primary" @click="openDialog('categories')">新增分类</el-button>
            <el-tag>共 {{ categories.length }} 条</el-tag>
          </div>
          <el-table :data="categories" border>
            <el-table-column prop="categoryCode" label="分类编码" min-width="140" />
            <el-table-column prop="categoryName" label="分类名称" min-width="160" />
            <el-table-column prop="description" label="说明" min-width="220" show-overflow-tooltip />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="供应商" name="suppliers">
        <div class="page-section">
          <div class="toolbar">
            <el-button type="primary" @click="openDialog('suppliers')">新增供应商</el-button>
            <el-tag>共 {{ suppliers.length }} 条</el-tag>
          </div>
          <el-table :data="suppliers" border>
            <el-table-column prop="supplierCode" label="供应商编码" min-width="140" />
            <el-table-column prop="supplierName" label="供应商名称" min-width="180" />
            <el-table-column prop="contactName" label="联系人" min-width="120" />
            <el-table-column prop="phone" label="联系电话" min-width="150" />
            <el-table-column prop="address" label="地址" min-width="220" show-overflow-tooltip />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="客户" name="customers">
        <div class="page-section">
          <div class="toolbar">
            <el-button type="primary" @click="openDialog('customers')">新增客户</el-button>
            <el-tag>共 {{ customers.length }} 条</el-tag>
          </div>
          <el-table :data="customers" border>
            <el-table-column prop="customerCode" label="客户编码" min-width="140" />
            <el-table-column prop="customerName" label="客户名称" min-width="180" />
            <el-table-column prop="contactName" label="联系人" min-width="120" />
            <el-table-column prop="phone" label="联系电话" min-width="150" />
            <el-table-column prop="address" label="地址" min-width="220" show-overflow-tooltip />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="仓库" name="warehouses">
        <div class="page-section">
          <div class="toolbar">
            <el-button type="primary" @click="openDialog('warehouses')">新增仓库</el-button>
            <el-tag>共 {{ warehouses.length }} 条</el-tag>
          </div>
          <el-table :data="warehouses" border>
            <el-table-column prop="warehouseCode" label="仓库编码" min-width="140" />
            <el-table-column prop="warehouseName" label="仓库名称" min-width="180" />
            <el-table-column prop="location" label="仓库位置" min-width="220" show-overflow-tooltip />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px">
      <el-form :model="form" label-width="100px">
        <template v-if="activeTab === 'materials'">
          <el-form-item label="材质编码"><el-input v-model="form.materialCode" /></el-form-item>
          <el-form-item label="材质名称"><el-input v-model="form.materialName" /></el-form-item>
          <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        </template>

        <template v-else-if="activeTab === 'categories'">
          <el-form-item label="分类编码"><el-input v-model="form.categoryCode" /></el-form-item>
          <el-form-item label="分类名称"><el-input v-model="form.categoryName" /></el-form-item>
          <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        </template>

        <template v-else-if="activeTab === 'suppliers'">
          <el-form-item label="供应商编码"><el-input v-model="form.supplierCode" /></el-form-item>
          <el-form-item label="供应商名称"><el-input v-model="form.supplierName" /></el-form-item>
          <el-form-item label="联系人"><el-input v-model="form.contactName" /></el-form-item>
          <el-form-item label="联系电话"><el-input v-model="form.phone" /></el-form-item>
          <el-form-item label="地址"><el-input v-model="form.address" type="textarea" :rows="3" /></el-form-item>
        </template>

        <template v-else-if="activeTab === 'customers'">
          <el-form-item label="客户编码"><el-input v-model="form.customerCode" /></el-form-item>
          <el-form-item label="客户名称"><el-input v-model="form.customerName" /></el-form-item>
          <el-form-item label="联系人"><el-input v-model="form.contactName" /></el-form-item>
          <el-form-item label="联系电话"><el-input v-model="form.phone" /></el-form-item>
          <el-form-item label="地址"><el-input v-model="form.address" type="textarea" :rows="3" /></el-form-item>
        </template>

        <template v-else>
          <el-form-item label="仓库编码"><el-input v-model="form.warehouseCode" /></el-form-item>
          <el-form-item label="仓库名称"><el-input v-model="form.warehouseName" /></el-form-item>
          <el-form-item label="仓库位置"><el-input v-model="form.location" type="textarea" :rows="3" /></el-form-item>
        </template>

        <el-form-item label="状态">
          <el-switch
            v-model="statusEnabled"
            inline-prompt
            active-text="启用"
            inactive-text="停用"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  createCategoryApi,
  createCustomerApi,
  createMaterialApi,
  createSupplierApi,
  createWarehouseApi,
  listCategoriesApi,
  listCustomersApi,
  listMaterialsApi,
  listSuppliersApi,
  listWarehousesApi
} from '../../api/master'

const activeTab = ref('materials')
const dialogVisible = ref(false)
const materials = ref([])
const categories = ref([])
const suppliers = ref([])
const customers = ref([])
const warehouses = ref([])
const form = reactive(createEmptyForm())
const statusEnabled = ref(true)

const dialogTitle = computed(() => {
  const titles = {
    materials: '新增材质',
    categories: '新增分类',
    suppliers: '新增供应商',
    customers: '新增客户',
    warehouses: '新增仓库'
  }
  return titles[activeTab.value]
})

function createEmptyForm() {
  return {
    materialCode: '',
    materialName: '',
    categoryCode: '',
    categoryName: '',
    supplierCode: '',
    supplierName: '',
    customerCode: '',
    customerName: '',
    warehouseCode: '',
    warehouseName: '',
    contactName: '',
    phone: '',
    address: '',
    location: '',
    description: '',
    status: 1
  }
}

function resetForm() {
  Object.assign(form, createEmptyForm())
  statusEnabled.value = true
}

function openDialog(tabName) {
  activeTab.value = tabName
  resetForm()
  dialogVisible.value = true
}

async function loadAll() {
  const [materialList, categoryList, supplierList, customerList, warehouseList] = await Promise.all([
    listMaterialsApi(),
    listCategoriesApi(),
    listSuppliersApi(),
    listCustomersApi(),
    listWarehousesApi()
  ])
  materials.value = materialList
  categories.value = categoryList
  suppliers.value = supplierList
  customers.value = customerList
  warehouses.value = warehouseList
}

async function submitCreate() {
  form.status = statusEnabled.value ? 1 : 0
  const actionMap = {
    materials: () =>
      createMaterialApi({
        materialCode: form.materialCode,
        materialName: form.materialName,
        description: form.description,
        status: form.status
      }),
    categories: () =>
      createCategoryApi({
        categoryCode: form.categoryCode,
        categoryName: form.categoryName,
        description: form.description,
        status: form.status
      }),
    suppliers: () =>
      createSupplierApi({
        supplierCode: form.supplierCode,
        supplierName: form.supplierName,
        contactName: form.contactName,
        phone: form.phone,
        address: form.address,
        status: form.status
      }),
    customers: () =>
      createCustomerApi({
        customerCode: form.customerCode,
        customerName: form.customerName,
        contactName: form.contactName,
        phone: form.phone,
        address: form.address,
        status: form.status
      }),
    warehouses: () =>
      createWarehouseApi({
        warehouseCode: form.warehouseCode,
        warehouseName: form.warehouseName,
        location: form.location,
        status: form.status
      })
  }

  await actionMap[activeTab.value]()
  ElMessage.success('基础资料保存成功')
  dialogVisible.value = false
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

.page-section {
  padding-top: 4px;
}
</style>
