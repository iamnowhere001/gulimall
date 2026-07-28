/**
 * 统一异常处理模块
 *
 * 职责：
 *  1. 接收 error 对象，自动分类（网络 / HTTP 状态 / 业务 / 未知）
 *  2. 根据分类和级别触发对应的 UI 反馈
 *  3. 提供可覆盖的钩子供外部自定义行为
 */

import Vue from 'vue'
import {
  ERROR_CATEGORY,
  ERROR_LEVEL,
  NETWORK_ERROR,
  matchHttpStatusError,
  matchBusinessError
} from './error-codes'

// ==================== 默认配置 ====================

const DEFAULT_CONFIG = {
  /** 是否启用全局错误提示 */
  enableGlobalTip: true,
  /** 重复错误去重时间窗口（毫秒），相同错误在窗口期内只提示一次 */
  dedupWindow: 3000,
  /** 是否将错误信息上报（可替换为实际的上报函数） */
  report: null
}

/** 最近错误记录，用于去重 */
const recentErrors = new Map()

// ==================== 错误分类器 ====================

/**
 * 分类错误并返回标准化错误对象
 * @param {Error} error axios 或业务抛出的错误
 * @returns {object} 标准化的错误对象
 */
export function classifyError (error) {
  const standardError = {
    original: error,
    category: ERROR_CATEGORY.UNKNOWN,
    level: ERROR_LEVEL.TIP,
    message: '未知错误',
    code: null,
    /** HTTP 状态码 */
    status: null,
    /** 是否应阻止后续操作 */
    isBlocking: false,
    /** 是否需要用户重新登录 */
    needLogin: false,
    /** 业务返回的原始数据（用于字段级校验） */
    businessData: null
  }

  // 1. 网络层错误判定
  if (error.__CANCEL__) {
    // 手动取消的请求，静默处理
    standardError.category = ERROR_CATEGORY.NETWORK
    standardError.level = ERROR_LEVEL.SILENT
    standardError.message = ''
    return standardError
  }

  if (error.code === 'ECONNABORTED' || error.message && error.message.includes('timeout')) {
    Object.assign(standardError, {
      ...NETWORK_ERROR.TIMEOUT,
      category: ERROR_CATEGORY.NETWORK,
      isBlocking: false
    })
    return standardError
  }

  if (error.code === 'ERR_NETWORK' || error.message === 'Network Error') {
    Object.assign(standardError, {
      ...NETWORK_ERROR.OFFLINE,
      category: ERROR_CATEGORY.NETWORK,
      isBlocking: false
    })
    return standardError
  }

  if (error.code === 'ECONNREFUSED') {
    Object.assign(standardError, {
      ...NETWORK_ERROR.CONNECTION_REFUSED,
      category: ERROR_CATEGORY.NETWORK,
      isBlocking: false
    })
    return standardError
  }

  // 2. HTTP 状态码错误判定
  if (error.response) {
    const status = error.response.status
    const httpError = matchHttpStatusError(status)
    standardError.category = ERROR_CATEGORY.HTTP_STATUS
    standardError.status = status
    standardError.code = status
    standardError.message = httpError.message || `服务异常（${status}）`
    standardError.level = httpError.level || ERROR_LEVEL.WARN
    standardError.isBlocking = httpError.level === ERROR_LEVEL.FATAL
    standardError.needLogin = status === 401

    // 如果后端响应中有更具体的业务消息，优先使用
    if (error.response.data && error.response.data.msg) {
      const data = error.response.data
      const bizMatch = matchBusinessError(data.code)
      if (bizMatch) {
        standardError.message = data.msg || bizMatch.message
        standardError.level = bizMatch.level
        standardError.businessData = data
        standardError.category = ERROR_CATEGORY.BUSINESS
        standardError.isBlocking = bizMatch.level === ERROR_LEVEL.FATAL
        standardError.needLogin = bizMatch.action === 'logout'
      } else {
        standardError.message = data.msg
        standardError.businessData = data
      }
    }
    return standardError
  }

  // 3. 业务错误判定（响应成功但 code !== 0）
  if (error.isBusinessError) {
    const bizMatch = matchBusinessError(error.code)
    standardError.category = ERROR_CATEGORY.BUSINESS
    standardError.code = error.code
    standardError.businessData = error.data || null
    standardError.isBlocking = false

    if (bizMatch) {
      standardError.message = error.message || bizMatch.message
      standardError.level = bizMatch.level
      standardError.isBlocking = bizMatch.level === ERROR_LEVEL.FATAL
      standardError.needLogin = bizMatch.action === 'logout'
    } else {
      standardError.message = error.message || '操作执行失败'
      standardError.level = ERROR_LEVEL.TIP
    }
    return standardError
  }

  // 4. 带消息的普通 Error 对象
  if (error.message) {
    standardError.message = error.message
  }

  return standardError
}

// ==================== UI 反馈触发 ====================

let _networkStatusVm = null

/**
 * 注册 NetworkStatus 组件实例引用，
 * 用于触发断网重连 UI
 * @param {object} vm 组件实例
 */
export function registerNetworkStatus (vm) {
  _networkStatusVm = vm
}

/**
 * 根据标准化错误触发对应的 UI 反馈
 * @param {object} stdError classifyError 返回的标准化错误对象
 * @param {object} options 可选覆盖选项
 */
export function triggerFeedback (stdError, options = {}) {
  // 静默级别不触发任何反馈
  if (!stdError.message || stdError.level === ERROR_LEVEL.SILENT) {
    return
  }

  // 去重：相同消息在时间窗口内只提示一次
  if (isDuplicate(stdError)) {
    return
  }

  const config = { ...DEFAULT_CONFIG, ...options }

  // 触发全局提示
  if (config.enableGlobalTip && stdError.category !== ERROR_CATEGORY.UNKNOWN) {
    showGlobalTip(stdError)
  }

  // 网络离线 -> 触发断网页面
  if (stdError.code === NETWORK_ERROR.OFFLINE.code || stdError.code === 'ERR_NETWORK') {
    if (_networkStatusVm && typeof _networkStatusVm.onOffline === 'function') {
      _networkStatusVm.onOffline()
    }
  }

  // 注意：不在此处调用 router.push() 或 clearLoginInfo()
  // 路由跳转和登录信息清理由调用方（拦截器 / 路由守卫）处理，
  // 避免在导航守卫进行中启动新导航导致 "Navigation cancelled" 错误

  // 上报
  if (typeof config.report === 'function') {
    config.report(stdError)
  }
}

/**
 * 显示轻量级全局提示
 */
function showGlobalTip (stdError) {
  const { category, level, message } = stdError

  // 根据级别选用不同的 Element UI 组件
  if (level === ERROR_LEVEL.WARN) {
    Vue.prototype.$notify({
      title: '操作提示',
      message,
      type: 'warning',
      duration: 4000,
      customClass: 'error-handler-notify'
    })
  } else if (level === ERROR_LEVEL.FATAL) {
    Vue.prototype.$notify({
      title: '系统提示',
      message,
      type: 'error',
      duration: 6000,
      customClass: 'error-handler-notify'
    })
  } else {
    // TIP 级别使用轻量 Message
    const msgType = category === ERROR_CATEGORY.HTTP_STATUS ? 'warning' : 'error'
    Vue.prototype.$message({
      message,
      type: msgType,
      duration: 3000
    })
  }
}

/**
 * 检查当前错误是否为重复错误（去重）
 */
function isDuplicate (stdError) {
  if (!stdError.message) return false
  const key = `${stdError.category}:${stdError.code || ''}:${stdError.message}`
  const now = Date.now()
  if (recentErrors.has(key) && now - recentErrors.get(key) < DEFAULT_CONFIG.dedupWindow) {
    return true
  }
  recentErrors.set(key, now)
  // 定时清理过期记录
  if (recentErrors.size > 50) {
    const expired = now - DEFAULT_CONFIG.dedupWindow
    for (const [k, t] of recentErrors) {
      if (t < expired) recentErrors.delete(k)
    }
  }
  return false
}

// ==================== 创建业务错误 ====================

/**
 * 创建一个业务错误对象
 * 在 axios 响应拦截器中，当 code !== 0 时调用此方法
 * @param {object} responseData 后端响应数据 { code, msg, data }
 * @returns {Error} 包装后的错误
 */
export function createBusinessError (responseData) {
  const err = new Error(responseData.msg || '业务处理异常')
  err.isBusinessError = true
  err.code = responseData.code
  err.data = responseData.data
  err.msg = responseData.msg
  return err
}

// ==================== 插件注册 ====================

const ErrorHandlerPlugin = {
  install (VueInstance, options) {
    const mergedConfig = { ...DEFAULT_CONFIG, ...options }

    // 挂载 error分类方法
    VueInstance.prototype.$classifyError = classifyError
    VueInstance.prototype.$createBusinessError = createBusinessError

    // 挂载便捷方法：直接触发提示
    VueInstance.prototype.$errorTip = function (message, type) {
      const stdError = {
        category: ERROR_CATEGORY.UNKNOWN,
        level: type === 'warn' ? ERROR_LEVEL.WARN : ERROR_LEVEL.TIP,
        message,
        code: null,
        status: null,
        isBlocking: false,
        needLogin: false,
        businessData: null
      }
      triggerFeedback(stdError, mergedConfig)
    }
  }
}

export { ErrorHandlerPlugin, DEFAULT_CONFIG }
export default { classifyError, triggerFeedback, createBusinessError, ErrorHandlerPlugin, registerNetworkStatus }
