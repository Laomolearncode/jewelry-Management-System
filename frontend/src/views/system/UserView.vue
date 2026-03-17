<template>
  <div class="page-card">
    <div class="page-header">
      <div>
        <h3 class="page-title">系统管理</h3>
        <p class="page-desc">查看用户、角色、权限点和最近操作日志。</p>
      </div>
      <el-button type="primary" @click="openDialog">新增用户</el-button>
    </div>

    <div class="summary-grid">
      <div class="summary-item">
        <span>用户数</span>
        <strong>{{ users.length }}</strong>
      </div>
      <div class="summary-item">
        <span>角色数</span>
        <strong>{{ roles.length }}</strong>
      </div>
      <div class="summary-item">
        <span>权限点</span>
        <strong>{{ permissions.length }}</strong>
      </div>
      <div class="summary-item">
        <span>日志条数</span>
        <strong>{{ logs.length }}</strong>
      </div>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="用户列表" name="users">
        <el-table :data="users" border>
          <el-table-column prop="username" label="用户名" min-width="130" />
          <el-table-column prop="realName" label="姓名" min-width="120" />
          <el-table-column prop="phone" label="手机号" min-width="150" />
          <el-table-column label="角色" min-width="220">
            <template #default="{ row }">
              <el-space wrap>
                <el-tag v-for="role in row.roles || []" :key="role" type="info">{{ role }}</el-tag>
              </el-space>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" min-width="180" />
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button
                size="small"
                :type="row.status === 1 ? 'warning' : 'success'"
                @click="toggleStatus(row)"
              >
                {{ row.status === 1 ? '停用' : '启用' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="角色列表" name="roles">
        <el-table :data="roles" border>
          <el-table-column prop="roleCode" label="角色编码" min-width="160" />
          <el-table-column prop="roleName" label="角色名称" min-width="160" />
          <el-table-column prop="description" label="说明" min-width="260" show-overflow-tooltip />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="权限点" name="permissions">
        <el-table :data="permissions" border>
          <el-table-column prop="permissionCode" label="权限编码" min-width="220" />
          <el-table-column prop="permissionName" label="权限名称" min-width="180" />
          <el-table-column prop="description" label="说明" min-width="260" show-overflow-tooltip />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="操作日志" name="logs">
        <el-table :data="logs" border max-height="520">
          <el-table-column prop="username" label="操作人" min-width="120" />
          <el-table-column prop="method" label="方法" width="90" />
          <el-table-column prop="path" label="请求路径" min-width="220" show-overflow-tooltip />
          <el-table-column prop="permissionCode" label="权限编码" min-width="180" show-overflow-tooltip />
          <el-table-column prop="statusCode" label="状态码" width="90" />
          <el-table-column prop="durationMs" label="耗时(ms)" width="100" />
          <el-table-column prop="createdAt" label="时间" min-width="180" />
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="dialogVisible" title="新增系统用户" width="640px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="密码"><el-input v-model="form.password" show-password /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item></el-col>
          <el-col :span="24">
            <el-form-item label="角色">
              <el-select v-model="form.roleIds" multiple style="width: 100%">
                <el-option v-for="item in roles" :key="item.id" :label="item.roleName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  createUserApi,
  listLogsApi,
  listPermissionsApi,
  listRolesApi,
  listUsersApi,
  updateUserStatusApi
} from '../../api/system'

const activeTab = ref('users')
const dialogVisible = ref(false)
const users = ref([])
const roles = ref([])
const permissions = ref([])
const logs = ref([])
const form = reactive(createDefaultForm())

function createDefaultForm() {
  return {
    username: '',
    password: '',
    realName: '',
    phone: '',
    roleIds: []
  }
}

function resetForm() {
  Object.assign(form, createDefaultForm())
}

function openDialog() {
  resetForm()
  dialogVisible.value = true
}

async function loadAll() {
  const [userList, roleList, permissionList, logList] = await Promise.all([
    listUsersApi(),
    listRolesApi(),
    listPermissionsApi(),
    listLogsApi()
  ])
  users.value = userList
  roles.value = roleList
  permissions.value = permissionList
  logs.value = logList
}

async function submitCreate() {
  await createUserApi(form)
  ElMessage.success('用户创建成功')
  dialogVisible.value = false
  loadAll()
}

async function toggleStatus(row) {
  const nextStatus = row.status === 1 ? 0 : 1
  await updateUserStatusApi(row.id, { status: nextStatus })
  ElMessage.success('用户状态更新成功')
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
