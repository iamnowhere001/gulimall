import Vue from 'vue'
import App from '@/App'
import router from '@/router' // api: https://github.com/vuejs/vue-router
import store from '@/store' // api: https://github.com/vuejs/vuex
import VueCookie from 'vue-cookie' // api: https://github.com/alfhen/vue-cookie
import '@/element-ui' // api: https://github.com/ElemeFE/element
import '@/icons' // api: http://www.iconfont.cn/
import '@/element-ui-theme'
import '@/assets/scss/index.scss'
import httpRequest from '@/utils/httpRequest' // api: https://github.com/axios/axios
import { isAuth } from '@/utils'
import cloneDeep from 'lodash/cloneDeep'
import PubSub from 'pubsub-js'

// 统一异常处理
import { ErrorHandlerPlugin } from '@/utils/error-handler'
import NetworkStatus from '@/components/NetworkStatus'

Vue.use(VueCookie)
Vue.use(ErrorHandlerPlugin)
Vue.config.productionTip = false

// 非生产环境, 适配mockjs模拟数据                 // api: https://github.com/nuysoft/Mock
// 注释掉 mock 拦截，直接连接后端服务器
// if (process.env.NODE_ENV !== 'production') {
//     require('@/mock')
// }

// 挂载全局
Vue.prototype.$http = httpRequest // ajax请求方法
Vue.prototype.isAuth = isAuth // 权限方法
Vue.prototype.PubSub = PubSub

// 保存整站vuex本地储存初始状态
window.SITE_CONFIG['storeState'] = cloneDeep(store.state)

/* eslint-disable no-new */
new Vue({
    el: '#app',
    router,
    store,
    template: '<App/>',
    components: { App }
})

// 动态挂载 NetworkStatus 组件到 body
const NetworkStatusConstructor = Vue.extend(NetworkStatus)
const networkStatusInstance = new NetworkStatusConstructor({
  router,
  store
})
networkStatusInstance.$mount()
document.body.appendChild(networkStatusInstance.$el)