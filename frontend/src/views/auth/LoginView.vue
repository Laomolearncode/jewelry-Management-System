<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-intro">
        <h1>饰品进销存系统</h1>
        <p>登录后进入采购、销售、库存、盘点与报表管理后台。</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="handleLogin">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin">登录系统</el-button>
      </el-form>
      <div class="login-tips">
        默认账号：`admin` / `admin123`
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../stores/auth'
import { getFirstAccessiblePath } from '../../utils/route-access'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: 'admin',
  password: 'admin123'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    await authStore.login(form)
    ElMessage.success('登录成功')
    const targetPath = getFirstAccessiblePath(authStore.user)
    if (targetPath === '/login') {
      ElMessage.error('当前账号已登录，但未配置任何可访问的后台权限，请联系管理员分配权限')
      return
    }
    router.push(targetPath)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #dbeafe, #eff6ff, #f8fafc);
}

.login-card {
  width: 420px;
  padding: 36px 32px;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.12);
}

.login-intro h1 {
  margin: 0 0 12px;
  font-size: 28px;
}

.login-intro p {
  margin: 0 0 24px;
  color: #64748b;
  line-height: 1.6;
}

.login-btn {
  width: 100%;
  margin-top: 12px;
}

.login-tips {
  margin-top: 16px;
  color: #64748b;
  font-size: 13px;
}
</style>
