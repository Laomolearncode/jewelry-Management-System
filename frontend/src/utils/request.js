import axios from 'axios'
import { ElMessage } from 'element-plus'
import { clearToken, clearUser, getToken } from './storage'

const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

service.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

service.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (!payload.success) {
      ElMessage.error(payload.message || '请求失败')
      return Promise.reject(new Error(payload.message || '请求失败'))
    }
    return payload.data
  },
  (error) => {
    const message = error?.response?.data?.message || error.message || '服务异常'
    if (message.includes('未登录') || message.includes('登录已失效')) {
      clearToken()
      clearUser()
      if (window.location.hash !== '#/login') {
        window.location.href = '/#/login'
      }
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default service
