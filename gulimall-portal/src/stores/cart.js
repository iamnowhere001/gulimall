import { defineStore } from 'pinia'
import { ref } from 'vue'

// 购物车数量（示例：本地维护，登录后应由后端购物车接口聚合）
export const useCartStore = defineStore('cart', () => {
  const count = ref(0)

  function setCount(n) {
    count.value = n
  }

  return { count, setCount }
})
