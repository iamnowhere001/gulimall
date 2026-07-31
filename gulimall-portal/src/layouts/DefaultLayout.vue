<template>
  <div class="layout">
    <AppHeader />
    <main class="layout-main">
      <slot />
    </main>
    <AppFooter />
  </div>
</template>

<script setup>
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
</script>

<style scoped>
.layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}
.layout-main {
  flex: 1;
  width: 1200px;
  max-width: 100%;
  margin: 0 auto;
  padding: 16px;
}
</style>
