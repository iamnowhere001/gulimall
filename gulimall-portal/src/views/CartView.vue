<template>
  <div class="cart">
    <h2>我的购物车</h2>
    <p v-if="loading" class="state-tip">加载中…</p>
    <p v-else-if="error" class="state-tip">
      购物车服务暂不可用。需后端 <code>/api/cart/items</code> 返回 CartVo。
    </p>
    <template v-else-if="items.length">
      <table class="cart__table">
        <thead>
          <tr>
            <th style="width: 50px"></th>
            <th>商品</th>
            <th>单价</th>
            <th>数量</th>
            <th>小计</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="it in items" :key="it.skuId">
            <td>
              <input type="checkbox" v-model="it.checked" @change="recalc" />
            </td>
            <td>
              <div class="cart__item">
                <img :src="it.image" :alt="it.title" />
                <div>
                  <p class="cart__title">{{ it.title }}</p>
                  <p class="cart__attr" v-if="it.skuAttrValues">
                    {{ it.skuAttrValues.join(' ') }}
                  </p>
                </div>
              </div>
            </td>
            <td>￥{{ it.price }}</td>
            <td>
              <input
                type="number"
                min="1"
                v-model.number="it.count"
                @change="changeCount(it)"
                class="cart__num"
              />
            </td>
            <td>￥{{ (it.price * it.count).toFixed(2) }}</td>
            <td>
              <a href="javascript:;" class="cart__del" @click="remove(it.skuId)">删除</a>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="cart__footer">
        <span>已选 {{ checkedCount }} 件，合计：￥{{ checkedTotal }}</span>
        <button class="btn-primary" @click="checkout">去结算</button>
      </div>
    </template>
    <p v-else class="state-tip">购物车是空的</p>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getCart, updateCartItem, removeCartItem } from '@/api/cart'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const cart = useCartStore()
const items = ref([])
const loading = ref(true)
const error = ref(false)

const checkedCount = computed(() => items.value.filter((i) => i.checked).reduce((s, i) => s + i.count, 0))
const checkedTotal = computed(() =>
  items.value.filter((i) => i.checked).reduce((s, i) => s + i.price * i.count, 0).toFixed(2)
)

onMounted(async () => {
  try {
    const res = await getCart()
    // CartVo.items 即购物项列表，给每项加一个本地选中态，默认选中
    items.value = (res?.items || []).map((i) => ({ ...i, checked: i.check !== false }))
  } catch (e) {
    error.value = true
  } finally {
    loading.value = false
  }
})

function recalc() {}

async function changeCount(it) {
  try {
    await updateCartItem(it.skuId, it.count)
  } catch (e) {
    alert('修改数量失败：' + (e.message || e))
  }
}

async function remove(skuId) {
  try {
    await removeCartItem(skuId)
    items.value = items.value.filter((i) => i.skuId !== skuId)
    // 同步刷新顶部购物车角标
    cart.fetchCount()
  } catch (e) {
    alert('删除失败：' + (e.message || e))
  }
}

function checkout() {
  router.push('/order')
}
</script>

<style scoped>
.cart__table {
  width: 100%;
  background: #fff;
  border-collapse: collapse;
}
.cart__table th,
.cart__table td {
  padding: 12px;
  border-bottom: 1px solid #eee;
  text-align: left;
  vertical-align: middle;
}
.cart__item {
  display: flex;
  gap: 12px;
  align-items: center;
}
.cart__item img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border: 1px solid #eee;
}
.cart__title {
  margin: 0;
  font-size: 14px;
}
.cart__attr {
  margin: 4px 0 0;
  font-size: 12px;
  color: #999;
}
.cart__num {
  width: 60px;
  padding: 4px;
}
.cart__del {
  color: #e1251b;
  font-size: 13px;
}
.cart__footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 16px;
  margin-top: 16px;
}
.btn-primary {
  background: #e1251b;
  color: #fff;
  border: none;
  padding: 10px 28px;
  cursor: pointer;
  border-radius: 2px;
}
</style>
