<template>
  <div class="login">
    <div class="login__box">
      <h2>账号登录</h2>
      <input v-model="form.loginacct" placeholder="手机号/用户名" />
      <input v-model="form.password" type="password" placeholder="密码" @keyup.enter="onLogin" />
      <p v-if="msg" class="login__msg">{{ msg }}</p>
      <button class="btn-primary" @click="onLogin">登录</button>
      <p class="login__tip">
        登录后 token 自动通过 X-Auth-Token 头管理，无需手动处理。
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { login as loginApi } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const route = useRoute()
const user = useUserStore()
const cart = useCartStore()
const form = ref({ loginacct: '', password: '' })
const msg = ref('')

async function onLogin() {
  msg.value = ''
  try {
    const info = await loginApi(form.value)
    user.setUser(info)
    // 登录后刷新购物车数量（后端会合并临时购物车到用户购物车）
    cart.fetchCount()
    // 如果有 return_url 参数，跳回原页面
    const redirect = route.query.return_url
    if (redirect && typeof redirect === 'string') {
      router.push(redirect)
    } else {
      router.push('/')
    }
  } catch (e) {
    msg.value = e.message || '登录失败'
  }
}
</script>

<style scoped>
.login {
  display: flex;
  justify-content: center;
  padding: 48px 0;
}
.login__box {
  width: 360px;
  background: #fff;
  padding: 32px;
  border-radius: 6px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}
.login__box input {
  width: 100%;
  padding: 10px 12px;
  margin-bottom: 12px;
  border: 1px solid #ddd;
  border-radius: 2px;
  outline: none;
}
.login__msg {
  color: #e1251b;
  font-size: 13px;
}
.btn-primary {
  width: 100%;
  background: #e1251b;
  color: #fff;
  border: none;
  padding: 10px;
  cursor: pointer;
  border-radius: 2px;
}
.login__tip {
  margin-top: 16px;
  font-size: 12px;
  color: #999;
}
</style>
