<template>
  <div class="member">
    <h2>个人中心</h2>
    <p v-if="loading" class="state-tip">加载中…</p>
    <template v-else-if="info">
      <section class="member__card">
        <p><span>昵称：</span>{{ info.nickname || info.username }}</p>
        <p><span>手机号：</span>{{ info.mobile || '—' }}</p>
        <p><span>会员等级：</span>{{ info.levelId || '—' }}</p>
        <p><span>积分：</span>{{ info.integration ?? '—' }}</p>
      </section>

      <!-- 我的订单 -->
      <section class="member__orders">
        <h3>我的订单</h3>
        <p v-if="ordersLoading" class="state-tip">订单加载中…</p>
        <p v-else-if="ordersError" class="state-tip">订单加载失败</p>
        <template v-else-if="orders.length">
          <table class="member__order-table">
            <thead>
              <tr>
                <th>订单号</th>
                <th>金额</th>
                <th>状态</th>
                <th>下单时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="o in orders" :key="o.id">
                <td>{{ o.orderSn }}</td>
                <td>￥{{ o.totalAmount }}</td>
                <td>{{ statusText(o.status) }}</td>
                <td>{{ o.modifyTime || o.createTime }}</td>
              </tr>
            </tbody>
          </table>
        </template>
        <p v-else class="state-tip">暂无订单</p>
      </section>

      <div class="member__actions">
        <router-link to="/order">去下单</router-link>
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
import { getMyOrders } from '@/api/order'

const router = useRouter()
const user = useUserStore()
const info = ref(null)
const loading = ref(true)

// 订单列表
const orders = ref([])
const ordersLoading = ref(false)
const ordersError = ref(false)

// 订单状态映射
const STATUS_MAP = {
  0: '待付款',
  1: '已付款',
  2: '已发货',
  3: '已完成',
  4: '已关闭'
}
function statusText(s) {
  return STATUS_MAP[s] || '未知'
}

onMounted(async () => {
  // 优先使用登录时本地保存的信息
  if (user.isLogin) {
    info.value = user.info
  } else {
    // 否则尝试从后端取当前会员
    try {
      const member = await getCurrentMember()
      info.value = member
      user.setUser(member)
    } catch (e) {
      info.value = null
    }
  }
  loading.value = false

  // 加载订单列表（仅登录用户）
  if (info.value) {
    ordersLoading.value = true
    try {
      const res = await getMyOrders({ page: 1, limit: 10 })
      // PageUtils: { totalCount, pageSize, totalPage, currPage, list }
      orders.value = res?.list || []
    } catch (e) {
      ordersError.value = true
    } finally {
      ordersLoading.value = false
    }
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
  margin-bottom: 16px;
}
.member__card p {
  margin: 8px 0;
}
.member__card span {
  color: #999;
  display: inline-block;
  width: 80px;
}
.member__orders {
  background: #fff;
  padding: 16px 24px;
  border-radius: 6px;
  margin-bottom: 16px;
}
.member__orders h3 {
  margin: 0 0 12px;
}
.member__order-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}
.member__order-table th,
.member__order-table td {
  padding: 8px 12px;
  border-bottom: 1px solid #eee;
  text-align: left;
}
.member__order-table th {
  color: #999;
  font-weight: normal;
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
