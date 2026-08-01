import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

// 路由按"前台页面"组织，所有请求最终由 nginx/网关统一收口到对应微服务 JSON 接口。
const routes = [
  { path: '/', name: 'home', component: () => import('@/views/HomeView.vue'), meta: { title: '首页' } },
  { path: '/search', name: 'search', component: () => import('@/views/SearchView.vue'), meta: { title: '搜索' } },
  { path: '/product/:skuId', name: 'product-detail', component: () => import('@/views/ProductDetailView.vue'), meta: { title: '商品详情' } },
  { path: '/cart', name: 'cart', component: () => import('@/views/CartView.vue'), meta: { title: '购物车' } },
  { path: '/order', name: 'order', component: () => import('@/views/OrderView.vue'), meta: { title: '订单', requireAuth: true } },
  { path: '/member', name: 'member', component: () => import('@/views/MemberView.vue'), meta: { title: '个人中心', requireAuth: true } },
  { path: '/login', name: 'login', component: () => import('@/views/LoginView.vue'), meta: { title: '登录' } }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

// 登录拦截：requireAuth 页面未登录时跳转登录页，并携带 return_url
router.beforeEach((to, from, next) => {
  if (to.meta.requireAuth) {
    const user = useUserStore()
    if (!user.isLogin) {
      next({ path: '/login', query: { return_url: to.fullPath } })
      return
    }
  }
  next()
})

router.afterEach((to) => {
  document.title = (to.meta.title ? to.meta.title + ' - ' : '') + '谷粒商城'
})

export default router
