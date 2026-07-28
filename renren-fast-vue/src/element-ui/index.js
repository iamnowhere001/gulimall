/**
 * Element UI 全量引入
 */
import Vue from 'vue'
import ElementUI from 'element-ui'

Vue.use(ElementUI, { size: 'medium' })

// 服务类方法挂载到原型
Vue.prototype.$ELEMENT = { size: 'medium' }
