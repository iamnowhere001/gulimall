import { defineStore } from 'pinia'
import { TOKEN_KEY } from '@/api/request'

const USER_KEY = 'gulimall_user'

// 用户登录态：从 localStorage 恢复，刷新不丢失
// token（X-Auth-Token）由 axios 拦截器自动管理，此处仅同步清除
export const useUserStore = defineStore('user', {
  state: () => {
    let info = null
    try {
      const raw = localStorage.getItem(USER_KEY)
      if (raw) info = JSON.parse(raw)
    } catch (e) {
      info = null
    }
    return { info }
  },
  getters: {
    isLogin: (state) => !!state.info,
    nickname: (state) => state.info?.nickname || state.info?.username || ''
  },
  actions: {
    setUser(info) {
      this.info = info
      localStorage.setItem(USER_KEY, JSON.stringify(info))
    },
    logout() {
      this.info = null
      localStorage.removeItem(USER_KEY)
      localStorage.removeItem(TOKEN_KEY)
    }
  }
})
