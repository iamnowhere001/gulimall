<template>
  <div class="member">
    <h2>个人中心</h2>
    <p v-if="loading" class="state-tip">加载中…</p>
    <template v-else-if="info">
      <section class="member__card">
        <p><span>昵称：</span>{{ info.nickname }}</p>
        <p><span>手机号：</span>{{ info.mobile }}</p>
        <p><span>会员等级：</span>{{ info.levelName || info.levelId || '—' }}</p>
        <p><span>积分：</span>{{ info.integration ?? '—' }}</p>
      </section>
      <div class="member__actions">
        <router-link to="/order">我的订单</router-link>
        <a href="javascript:;" @click="handleLogout">退出登录</a>
      </div>
    </template>
    <p v-else class="state-tip">
      未登录，<router-link to="/login" class="member__login">请先登录</router-link>
    </p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getCurrentMember } from '@/api/member'

const router = useRouter()
const user = useUserStore()
const info = ref(null)
const loading = ref(true)

onMounted(async () => {
  // 优先使用登录时本地保存的信息
  if (user.isLogin) {
    info.value = user.info
    loading.value = false
    return
  }
  // 否则尝试从后端取当前会员
  try {
    const member = await getCurrentMember()
    info.value = member
    user.setUser(member)
  } catch (e) {
    info.value = null
  } finally {
    loading.value = false
  }
})

function handleLogout() {
  user.logout()
  router.push('/')
}
</script>

<style scoped>
.member__card {
  background: #fff;
  padding: 24px;
  border-radius: 6px;
}
.member__card p {
  margin: 8px 0;
}
.member__card span {
  color: #999;
  display: inline-block;
  width: 80px;
}
.member__actions {
  margin-top: 16px;
  display: flex;
  gap: 16px;
}
.member__actions a {
  color: #e1251b;
}
.member__login {
  color: #e1251b;
}
</style>
