<template>
  <div class="order">
    <h2>订单确认</h2>
    <p v-if="loading" class="state-tip">加载中…</p>
    <p v-else-if="error" class="state-tip">
      订单服务暂不可用。请确认已登录，且后端 <code>/api/order/confirm</code> 正常返回 OrderConfirmVo。
    </p>
    <template v-else-if="confirm">
      <!-- 收货地址 -->
      <section class="order__block">
        <h3>收货地址</h3>
        <label
          v-for="addr in confirm.memberAddressVos || []"
          :key="addr.id"
          class="order__addr"
          :class="{ active: selectedAddrId === addr.id }"
        >
          <input type="radio" :value="addr.id" v-model="selectedAddrId" />
          <span>{{ addr.name }} {{ addr.phone }}（{{ addr.region }} {{ addr.detailAddress }}）</span>
          <em v-if="addr.isDefault === 1">默认</em>
        </label>
        <p v-if="!(confirm.memberAddressVos || []).length" class="order__hint">请先添加收货地址</p>
      </section>

      <!-- 商品清单 -->
      <section class="order__block">
        <h3>商品清单</h3>
        <ul class="order__items">
          <li v-for="it in confirm.items || []" :key="it.skuId">
            <img :src="it.skuPic || it.image" :alt="it.skuName" />
            <div class="order__item-info">
              <p>{{ it.skuName || it.title }}</p>
              <p class="order__item-attrs" v-if="it.skuAttrs">{{ it.skuAttrs.join(' ') }}</p>
            </div>
            <span>￥{{ it.skuPrice || it.price }} × {{ it.skuQuantity || it.count }}</span>
          </li>
        </ul>
      </section>

      <!-- 金额 -->
      <section class="order__block order__amount">
        <div><span>商品总额</span><b>￥{{ confirm.payPrice }}</b></div>
        <div><span>运费</span><b>￥0.00</b></div>
        <div class="order__pay"><span>应付</span><b>￥{{ confirm.payPrice }}</b></div>
      </section>

      <div class="order__footer">
        <button class="btn-primary" :disabled="submitting" @click="submit">
          {{ submitting ? '提交中…' : '提交订单' }}
        </button>
      </div>
      <p v-if="submitMsg" class="order__msg">{{ submitMsg }}</p>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderConfirm, submitOrder } from '@/api/order'

const router = useRouter()
const confirm = ref(null)
const loading = ref(true)
const error = ref(false)
const selectedAddrId = ref(null)
const submitting = ref(false)
const submitMsg = ref('')

onMounted(async () => {
  try {
    const res = await getOrderConfirm()
    confirm.value = res
    const addrs = res?.memberAddressVos || []
    const def = addrs.find((a) => a.isDefault === 1) || addrs[0]
    selectedAddrId.value = def?.id ?? null
  } catch (e) {
    error.value = true
  } finally {
    loading.value = false
  }
})

async function submit() {
  if (!selectedAddrId.value) {
    submitMsg.value = '请选择收货地址'
    return
  }
  submitting.value = true
  submitMsg.value = ''
  try {
    const res = await submitOrder({
      addrId: selectedAddrId.value,
      payType: 1,
      orderToken: confirm.value.orderToken
    })
    submitMsg.value = '下单成功！订单号：' + (res.orderSn || '')
    setTimeout(() => router.push('/member'), 1200)
  } catch (e) {
    submitMsg.value = '下单失败：' + (e.message || e)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.order__block {
  background: #fff;
  padding: 16px;
  border-radius: 6px;
  margin-bottom: 16px;
}
.order__addr {
  display: block;
  padding: 8px;
  border: 1px solid #eee;
  border-radius: 4px;
  margin-bottom: 8px;
  cursor: pointer;
}
.order__addr.active {
  border-color: #e1251b;
}
.order__addr em {
  color: #e1251b;
  font-style: normal;
  margin-left: 8px;
  font-size: 12px;
}
.order__hint {
  color: #999;
  font-size: 13px;
}
.order__items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.order__items li {
  display: flex;
  align-items: center;
  gap: 12px;
}
.order__items img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border: 1px solid #eee;
}
.order__item-info {
  flex: 1;
}
.order__item-info p {
  margin: 0;
  font-size: 14px;
}
.order__item-attrs {
  color: #999;
  font-size: 12px;
}
.order__amount div {
  display: flex;
  justify-content: space-between;
  padding: 4px 0;
}
.order__pay {
  border-top: 1px solid #eee;
  margin-top: 8px;
  padding-top: 8px;
}
.order__pay b {
  color: #e1251b;
  font-size: 18px;
}
.order__footer {
  display: flex;
  justify-content: flex-end;
}
.btn-primary {
  background: #e1251b;
  color: #fff;
  border: none;
  padding: 10px 28px;
  cursor: pointer;
  border-radius: 2px;
}
.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.order__msg {
  text-align: right;
  color: #e1251b;
  margin-top: 8px;
}
</style>
