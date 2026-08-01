<template>
  <div class="detail" v-if="sku">
    <div class="detail__main">
      <div class="detail__gallery">
        <img :src="mainImg" class="detail__img" :alt="sku.skuTitle" />
        <div class="detail__thumbs" v-if="images.length">
          <img
            v-for="img in images"
            :key="img.id"
            :src="img.imgUrl"
            :class="{ active: mainImg === img.imgUrl }"
            @click="mainImg = img.imgUrl"
            class="detail__thumb"
          />
        </div>
      </div>

      <div class="detail__info">
        <h1>{{ sku.skuTitle }}</h1>
        <p class="detail__sub">{{ sku.skuSubtitle }}</p>
        <p class="detail__price">￥{{ sku.price }}</p>
        <p class="detail__sales">销量：{{ sku.saleCount }}</p>

        <!-- 销售属性 -->
        <div class="detail__attrs" v-if="saleAttr.length">
          <div class="detail__attr" v-for="a in saleAttr" :key="a.attrId">
            <span class="detail__attr-name">{{ a.attrName }}：</span>
            <span
              v-for="v in a.attrValues"
              :key="v.attrValue"
              class="detail__attr-val"
            >{{ v.attrValue }}</span>
          </div>
        </div>

        <div class="detail__actions">
          <button class="btn-primary" @click="addCart">加入购物车</button>
          <button class="btn-ghost" @click="buyNow">立即购买</button>
        </div>
      </div>
    </div>

    <!-- 规格参数 -->
    <section class="detail__specs" v-if="groupAttrs.length">
      <h3>规格参数</h3>
      <div v-for="g in groupAttrs" :key="g.groupName" class="detail__spec-group">
        <h4>{{ g.groupName }}</h4>
        <ul>
          <li v-for="(at, i) in g.attrs" :key="i">
            <span class="detail__spec-name">{{ at.attrName }}：</span>
            <span>{{ at.attrValue }}</span>
          </li>
        </ul>
      </div>
    </section>
  </div>
  <p v-else-if="loading" class="state-tip">加载中…</p>
  <p v-else class="state-tip">商品不存在或后端未启动。</p>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSkuDetail } from '@/api/product'
import { addToCart } from '@/api/cart'
import { useCartStore } from '@/stores/cart'

const route = useRoute()
const router = useRouter()
const cart = useCartStore()
const skuId = route.params.skuId

const detail = ref(null)
const loading = ref(true)
const mainImg = ref('')

// SkuItemVo 字段
const sku = computed(() => detail.value?.info || null)
const images = computed(() => detail.value?.images || [])
const saleAttr = computed(() => detail.value?.saleAttr || [])
const groupAttrs = computed(() => detail.value?.groupAttrs || [])

onMounted(async () => {
  try {
    detail.value = await getSkuDetail(skuId)
    const def = images.value.find((i) => i.defaultImg === 1) || images.value[0]
    mainImg.value = def?.imgUrl || sku.value?.skuDefaultImg || ''
  } catch (e) {
    console.error('加载商品失败：', e)
  } finally {
    loading.value = false
  }
})

async function addCart() {
  try {
    await addToCart(skuId, 1)
    // 刷新购物车角标
    await cart.fetchCount()
    router.push('/cart')
  } catch (e) {
    alert('加入购物车失败：' + (e.message || e))
  }
}
function buyNow() {
  router.push('/order')
}
</script>

<style scoped>
.detail__main {
  display: flex;
  gap: 32px;
  background: #fff;
  padding: 24px;
  border-radius: 6px;
}
.detail__gallery {
  width: 360px;
}
.detail__img {
  width: 360px;
  height: 360px;
  object-fit: cover;
  border: 1px solid #eee;
}
.detail__thumbs {
  display: flex;
  gap: 8px;
  margin-top: 8px;
  flex-wrap: wrap;
}
.detail__thumb {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border: 1px solid #eee;
  cursor: pointer;
}
.detail__thumb.active {
  border-color: #e1251b;
}
.detail__info {
  flex: 1;
}
.detail__info h1 {
  font-size: 22px;
  margin: 0 0 12px;
}
.detail__sub {
  color: #888;
}
.detail__price {
  color: #e1251b;
  font-size: 28px;
  font-weight: 700;
}
.detail__sales {
  color: #999;
  font-size: 13px;
}
.detail__attrs {
  margin: 16px 0;
}
.detail__attr {
  margin-bottom: 8px;
}
.detail__attr-name {
  color: #666;
}
.detail__attr-val {
  display: inline-block;
  border: 1px solid #ddd;
  padding: 2px 10px;
  margin: 0 6px 6px 0;
  border-radius: 2px;
}
.detail__actions {
  margin-top: 24px;
  display: flex;
  gap: 16px;
}
.btn-primary,
.btn-ghost {
  padding: 12px 32px;
  border-radius: 2px;
  font-size: 16px;
  cursor: pointer;
  border: 1px solid #e1251b;
}
.btn-primary {
  background: #e1251b;
  color: #fff;
}
.btn-ghost {
  background: #fff;
  color: #e1251b;
}
.detail__specs {
  background: #fff;
  padding: 16px 24px;
  margin-top: 16px;
  border-radius: 6px;
}
.detail__spec-group h4 {
  margin: 12px 0 6px;
}
.detail__spec-group li {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}
.detail__spec-name {
  color: #999;
}
</style>
