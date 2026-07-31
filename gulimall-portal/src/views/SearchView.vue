<template>
  <div class="search">
    <div class="search__bar">
      <input v-model="keyword" @keyup.enter="onSearch" placeholder="搜索商品" />
      <button @click="onSearch">搜索</button>
    </div>

    <p v-if="loading" class="state-tip">搜索中…</p>
    <p v-else-if="error" class="state-tip">
      检索服务暂不可用。需后端 <code>/api/search/list</code> 正常返回 SearchResult。
    </p>
    <template v-else>
      <p class="search__count">共 {{ total }} 件商品，第 {{ pageNum }}/{{ totalPages }} 页</p>
      <div v-if="products.length" class="product-grid">
        <ProductCard v-for="p in products" :key="p.skuId" :product="p" />
      </div>
      <p v-else class="state-tip">没有找到相关商品</p>

      <div class="search__pager" v-if="totalPages > 1">
        <button :disabled="pageNum <= 1" @click="goPage(pageNum - 1)">上一页</button>
        <button :disabled="pageNum >= totalPages" @click="goPage(pageNum + 1)">下一页</button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchProducts } from '@/api/search'
import ProductCard from '@/components/ProductCard.vue'

const route = useRoute()
const router = useRouter()
const keyword = ref(route.query.keyword || '')
const catalog3Id = route.query.catalog3Id
const pageNum = ref(Number(route.query.pageNum) || 1)

const products = ref([])
const total = ref(0)
const totalPages = ref(0)
const loading = ref(false)
const error = ref(false)

async function onSearch() {
  loading.value = true
  error.value = false
  try {
    const res = await searchProducts({
      keyword: keyword.value.trim(),
      catalog3Id,
      pageNum: pageNum.value
    })
    products.value = res?.product || []
    total.value = res?.total || 0
    totalPages.value = res?.totalPages || 0
  } catch (e) {
    error.value = true
    products.value = []
  } finally {
    loading.value = false
  }
}

function goPage(p) {
  pageNum.value = p
  router.push({ path: '/search', query: { keyword: keyword.value, catalog3Id, pageNum: p } })
  onSearch()
}

onMounted(onSearch)
watch(
  () => route.query.keyword,
  (kw) => {
    keyword.value = kw || ''
    pageNum.value = 1
    onSearch()
  }
)
</script>

<style scoped>
.search__bar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}
.search__bar input {
  flex: 1;
  padding: 10px 12px;
  border: 2px solid #e1251b;
  outline: none;
  font-size: 14px;
}
.search__bar button {
  border: none;
  background: #e1251b;
  color: #fff;
  padding: 0 28px;
  cursor: pointer;
}
.search__count {
  color: #666;
  font-size: 13px;
}
.search__pager {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
}
.search__pager button {
  border: 1px solid #ddd;
  background: #fff;
  padding: 6px 18px;
  cursor: pointer;
}
.search__pager button:disabled {
  color: #ccc;
  cursor: not-allowed;
}
</style>
