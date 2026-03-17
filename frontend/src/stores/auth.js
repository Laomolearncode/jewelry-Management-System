import { defineStore } from 'pinia'
import { profileApi, loginApi, logoutApi } from '../api/auth'
import { clearToken, clearUser, getToken, getUser, setToken, setUser } from '../utils/storage'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: getToken(),
    user: getUser()
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token)
  },
  actions: {
    async login(form) {
      const data = await loginApi(form)
      this.token = data.token
      setToken(data.token)
      await this.fetchProfile()
    },
    async fetchProfile() {
      const profile = await profileApi()
      this.user = profile
      setUser(profile)
      return profile
    },
    async logout() {
      if (this.token) {
        try {
          await logoutApi()
        } catch (error) {
          // 忽略退出时的后端异常，优先清空本地状态。
        }
      }
      this.reset()
    },
    reset() {
      this.token = ''
      this.user = null
      clearToken()
      clearUser()
    }
  }
})
