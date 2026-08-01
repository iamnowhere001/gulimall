import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCart } from '@/api/cart'

// 购物车数量：从后端 /cart/items 获取 CartVo.countType（商品种类数）
export const useCartStore = defineStore('cart', () => {
  const count = ref(0)

  function setCount(n) {
    count.value = n
  }

  // 从后端拉取购物车，更新角标数量
  async function fetchCount() {
    try {
      const cart = await getCart()
      // CartVo.countType = 购物项种类数
      count.value = cart?.countType || 0
    } catch (e) {
      // 购物车接口异常时不影响主流程
      count.value = 0
    }
  }

  return { count, setCount, fetchCount }
})
