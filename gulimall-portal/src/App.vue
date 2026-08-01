<template>
  <DefaultLayout>
    <router-view />
  </DefaultLayout>
</template>

<script setup>
import { onMounted } from 'vue'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import { useCartStore } from '@/stores/cart'

const cart = useCartStore()

onMounted(() => {
  // 应用启动时拉取购物车数量（后端会通过 X-Auth-Token 识别用户/临时用户）
  cart.fetchCount()
})
</script>
