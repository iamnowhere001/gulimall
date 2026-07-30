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
  // 约定：标准响应体含 code 字段，code === 0 表示成功；
  // 若后端直接返回业务数据（裸数组 / 裸对象，无 code 字段，如 /sys/menu/list 返回数组），
  // 视为成功原样放行，避免被误判为“业务处理异常”。
  const isStandardResponse = data && typeof data === 'object' && !Array.isArray(data) && ('code' in data)
  if (isStandardResponse && data.code !== 0) {
    const businessError = createBusinessError(data)
    const stdError = classifyError(businessError)
    // 统一提示错误信息；但【不】在此强制清除登录信息或跳转登录页，
    // 避免页面请求偶发异常（含 401）时把用户踢出系统。是否重新登录由用户自行决定。
    triggerFeedback(stdError)

    // 阻断后续 then 链，进入 catch
    return Promise.reject(businessError)
  }
  return response
},   error => {
    // HTTP / 网络层错误：仅做统一提示，【不】强制清除登录信息或跳转登录页，
    // 避免页面请求偶发 401 / 网络异常时把用户踢出系统
    const stdError = classifyError(error)
    triggerFeedback(stdError)
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
