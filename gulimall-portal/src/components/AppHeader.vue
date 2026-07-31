<template>
  <header class="app-header">
    <div class="app-header__top">
      <div class="app-header__top-inner">
        <template v-if="user.isLogin">
          <span>欢迎, {{ user.nickname }}</span>
          <a href="javascript:;" @click="handleLogout">退出</a>
        </template>
        <template v-else>
          <router-link to="/login">你好，请登录</router-link>
          <router-link to="/login">免费注册</router-link>
        </template>
        <span class="split">|</span>
        <router-link to="/order">我的订单</router-link>
        <router-link to="/member">个人中心</router-link>
      </div>
    </div>

    <div class="app-header__main">
      <router-link to="/" class="app-header__logo">谷粒商城</router-link>
      <div class="app-header__search">
        <input
          v-model="keyword"
          type="text"
          placeholder="搜索你想要的商品"
          @keyup.enter="doSearch"
        />
        <button @click="doSearch">搜索</button>
      </div>
      <router-link to="/cart" class="app-header__cart">
        我的购物车 <b v-if="cart.count">{{ cart.count }}</b>
      </router-link>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const user = useUserStore()
const cart = useCartStore()
const keyword = ref('')

function doSearch() {
  const kw = keyword.value.trim()
  if (!kw) return
  router.push({ path: '/search', query: { keyword: kw } })
}

function handleLogout() {
  user.logout()
  router.push('/')
}
</script>

<style scoped>
.app-header__top {
  background: #f5f5f5;
  font-size: 12px;
  color: #666;
}
.app-header__top-inner {
  width: 1200px;
  max-width: 100%;
  margin: 0 auto;
  padding: 6px 16px;
  display: flex;
  gap: 12px;
  align-items: center;
}
.app-header__top-inner a {
  color: #666;
  text-decoration: none;
}
.app-header__top-inner a:hover {
  color: #e1251b;
}
.split {
  color: #ddd;
}
.app-header__main {
  width: 1200px;
  max-width: 100%;
  margin: 0 auto;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 24px;
}
.app-header__logo {
  font-size: 28px;
  font-weight: 700;
  color: #e1251b;
  text-decoration: none;
}
.app-header__search {
  flex: 1;
  display: flex;
  border: 2px solid #e1251b;
  border-radius: 2px;
  overflow: hidden;
}
.app-header__search input {
  flex: 1;
  border: none;
  outline: none;
  padding: 8px 12px;
  font-size: 14px;
}
.app-header__search button {
  border: none;
  background: #e1251b;
  color: #fff;
  padding: 0 24px;
  cursor: pointer;
  font-size: 14px;
}
.app-header__cart {
  border: 1px solid #e1251b;
  color: #e1251b;
  padding: 8px 16px;
  text-decoration: none;
  border-radius: 2px;
}
</style>
