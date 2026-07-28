import Vue from 'vue'
import axios from 'axios'
import qs from 'qs'
import merge from 'lodash/merge'
import { classifyError, triggerFeedback, createBusinessError } from './error-handler'

const http = axios.create({
  timeout: 1000 * 30,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json; charset=utf-8'
  }
})

/**
 * 安全跳转登录页：仅在非导航守卫场景下执行跳转
 * 通过检查 router.history.pending 判断是否有导航正在进行
 */
function redirectToLoginIfSafe () {
  const router = Vue.prototype.$router
  if (!router) return
  // 如果当前已有导航在进行（即处于 beforeEach 守卫中），不跳转
  // 守卫的 catch 块会通过 next({ name: 'login' }) 处理跳转
  if (router.history && router.history.pending) return
  // 已在登录页则不重复跳转
  if (router.currentRoute && router.currentRoute.name === 'login') return
  router.push({ name: 'login' }).catch(() => {})
}

/**
 * 请求拦截
 */
http.interceptors.request.use(config => {
  config.headers['token'] = Vue.cookie.get('token') // 请求头带上token
  return config
}, error => {
  // 请求发送阶段的错误（极少发生），统一处理
  const stdError = classifyError(error)
  triggerFeedback(stdError)
  return Promise.reject(error)
})

/**
 * 响应拦截
 */
http.interceptors.response.use(response => {
  const data = response.data
  // 业务逻辑错误：后端返回了数据但 code !== 0
  if (data && data.code !== 0) {
    const businessError = createBusinessError(data)
    const stdError = classifyError(businessError)
    triggerFeedback(stdError)

    // 401 token 失效 -> 清除登录信息
    if (stdError.needLogin) {
      const { clearLoginInfo } = require('@/utils')
      clearLoginInfo()
      // 仅在非导航守卫场景下跳转登录页；
      // 守卫场景由 next({ name: 'login' }) 处理，避免 Navigation cancelled
      redirectToLoginIfSafe()
    }

    // 阻断后续 then 链，进入 catch
    return Promise.reject(businessError)
  }
  return response
}, error => {
  // HTTP / 网络层错误
  const stdError = classifyError(error)
  triggerFeedback(stdError)

  // 401 -> 清除登录信息
  if (stdError.needLogin) {
    const { clearLoginInfo } = require('@/utils')
    clearLoginInfo()
    redirectToLoginIfSafe()
  }

  return Promise.reject(error)
})

/**
 * 请求地址处理
 * @param {*} actionName action方法名称
 */
http.adornUrl = (actionName) => {
  // 非生产环境 && 开启代理, 接口前缀统一使用[/proxyApi/]前缀做代理拦截!
  return (process.env.NODE_ENV !== 'production' && process.env.OPEN_PROXY ? '/api/' : window.SITE_CONFIG.baseUrl) + actionName
}

/**
 * get请求参数处理
 * @param {*} params 参数对象
 * @param {*} openDefultParams 是否开启默认参数?
 */
http.adornParams = (params = {}, openDefultParams = true) => {
  var defaults = {
    't': new Date().getTime()
  }
  return openDefultParams ? merge(defaults, params) : params
}

/**
 * post请求数据处理
 * @param {*} data 数据对象
 * @param {*} openDefultdata 是否开启默认数据?
 * @param {*} contentType 数据格式
 *  json: 'application/json; charset=utf-8'
 *  form: 'application/x-www-form-urlencoded; charset=utf-8'
 */
http.adornData = (data = {}, openDefultdata = true, contentType = 'json') => {
  var defaults = {
    't': new Date().getTime()
  }
  data = openDefultdata ? merge(defaults, data) : data
  return contentType === 'json' ? JSON.stringify(data) : qs.stringify(data)
}

export default http
