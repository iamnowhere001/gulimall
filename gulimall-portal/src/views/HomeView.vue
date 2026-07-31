<template>
  <div class="home">
    <!-- 分类导航 + 轮播 -->
    <section class="home-hero">
      <nav class="home-cats" @mouseleave="activeCat = null">
        <ul>
          <li
            v-for="cat in categories"
            :key="cat.catId"
            :class="{ active: activeCat === cat.catId }"
            @mouseenter="activeCat = cat.catId"
          >
            <span>{{ cat.name }}</span>
            <!-- 二级/三级分类浮层（来自 catalog.json） -->
            <div v-if="activeCat === cat.catId" class="home-cats__panel">
              <template v-for="c2 in catalogMap[cat.catId] || []" :key="c2.catalog2Id">
                <div class="home-cats__group">
                  <h4>{{ c2.catalog2Name }}</h4>
                  <div class="home-cats__links">
                    <router-link
                      v-for="c3 in c2.catalog3List"
                      :key="c3.catalog3Id"
                      :to="`/search?catalog3Id=${c3.catalog3Id}`"
                    >
                      {{ c3.catalog3Name }}
                    </router-link>
                  </div>
                </div>
              </template>
            </div>
          </li>
        </ul>
      </nav>

      <div class="home-banner">
        <div class="home-banner__slide">🛒 限时秒杀 低至 1 折</div>
        <div class="home-banner__slide">🚚 全场包邮 极速送达</div>
      </div>
    </section>

    <p v-if="loading" class="state-tip">加载中…</p>
    <p v-else-if="!categories.length" class="state-tip">
      未能连接到后端（gulimall-gateway:88）。请先启动微服务与网关。
    </p>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getCategoryTree, getCatalogJson } from '@/api/product'

const categories = ref([])
const catalog = ref({})
const loading = ref(true)
const activeCat = ref(null)

// 把 catalog.json 的 Map<cat1Id, Catelog2Vo[]> 转成按一级分类查找的结构
const catalogMap = computed(() => catalog.value || {})

onMounted(async () => {
  try {
    const [tree, cat] = await Promise.all([getCategoryTree(), getCatalogJson()])
    categories.value = tree || []
    catalog.value = cat || {}
  } catch (e) {
    console.error('加载首页数据失败：', e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.home-hero {
  display: flex;
  gap: 16px;
}
.home-cats {
  width: 200px;
  background: #fff;
  border: 1px solid #eee;
}
.home-cats li {
  position: relative;
  padding: 12px 16px;
  cursor: pointer;
  border-bottom: 1px solid #f5f5f5;
}
.home-cats li.active {
  background: #fdecea;
  color: #e1251b;
}
.home-cats__panel {
  position: absolute;
  left: 200px;
  top: 0;
  width: 600px;
  min-height: 100%;
  background: #fff;
  border: 1px solid #eee;
  box-shadow: 2px 2px 8px rgba(0, 0, 0, 0.1);
  padding: 16px;
  z-index: 10;
}
.home-cats__group {
  margin-bottom: 12px;
}
.home-cats__group h4 {
  margin: 0 0 6px;
  font-size: 14px;
}
.home-cats__links {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.home-cats__links a {
  font-size: 12px;
  color: #666;
  text-decoration: none;
}
.home-cats__links a:hover {
  color: #e1251b;
}
.home-banner {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.home-banner__slide {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #ff6a00, #ee0979);
  border-radius: 6px;
  min-height: 140px;
}
</style>
