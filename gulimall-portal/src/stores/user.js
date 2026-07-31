import { defineStore } from 'pinia'

// 用户登录态：从 sessionStorage 恢复，刷新不丢失（仅前端示例，生产应配合后端 token）
export const useUserStore = defineStore('user', {
  state: () => {
    let info = null
    try {
      const raw = sessionStorage.getItem('loginUser')
      if (raw) info = JSON.parse(raw)
    } catch (e) {
      info = null
    }
    return { info }
  },
  getters: {
    isLogin: (state) => !!state.info,
    nickname: (state) => state.info?.nickname || ''
  },
  actions: {
    setUser(info) {
      this.info = info
      sessionStorage.setItem('loginUser', JSON.stringify(info))
    },
    logout() {
      this.info = null
      sessionStorage.removeItem('loginUser')
    }
  }
})
