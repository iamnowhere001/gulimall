/**
 * 错误码常量与分类模块
 *
 * 分类体系：
 *  1. HTTP 网络层错误（超时、断网、DNS 故障等）
 *  2. HTTP 状态码错误（401 / 403 / 404 / 500 等）
 *  3. 业务逻辑错误（后端返回 code !== 0）
 */

// ==================== 错误类型枚举 ====================

/** 错误大类 */
export const ERROR_CATEGORY = {
  /** 网络层错误：超时、断网、连接失败等 */
  NETWORK: 'NETWORK',
  /** HTTP 响应状态码错误 */
  HTTP_STATUS: 'HTTP_STATUS',
  /** 业务逻辑错误：后端返回 code !== 0 */
  BUSINESS: 'BUSINESS',
  /** 未知/未分类错误 */
  UNKNOWN: 'UNKNOWN'
}

/** 错误级别 */
export const ERROR_LEVEL = {
  /** 静默处理，不展示给用户 */
  SILENT: 'silent',
  /** 轻量提示（Element Message） */
  TIP: 'tip',
  /** 中等警告（Element Notification） */
  WARN: 'warn',
  /** 严重错误（跳转错误页） */
  FATAL: 'fatal'
}

// ==================== HTTP 网络层错误 ====================

export const NETWORK_ERROR = {
  TIMEOUT: {
    code: 'NET_TIMEOUT',
    type: 'timeout',
    message: '请求超时，请检查网络连接后重试',
    category: ERROR_CATEGORY.NETWORK,
    level: ERROR_LEVEL.TIP
  },
  OFFLINE: {
    code: 'NET_OFFLINE',
    type: 'offline',
    message: '网络已断开，请检查网络连接',
    category: ERROR_CATEGORY.NETWORK,
    level: ERROR_LEVEL.WARN
  },
  CONNECTION_REFUSED: {
    code: 'NET_CONNECTION_REFUSED',
    type: 'connection_refused',
    message: '无法连接到服务器，请稍后重试',
    category: ERROR_CATEGORY.NETWORK,
    level: ERROR_LEVEL.WARN
  },
  DNS_ERROR: {
    code: 'NET_DNS_ERROR',
    type: 'dns_error',
    message: '域名解析失败，请检查网络设置',
    category: ERROR_CATEGORY.NETWORK,
    level: ERROR_LEVEL.WARN
  },
  UNKNOWN_NETWORK: {
    code: 'NET_UNKNOWN',
    type: 'unknown',
    message: '网络请求异常，请稍后重试',
    category: ERROR_CATEGORY.NETWORK,
    level: ERROR_LEVEL.TIP
  }
}

// ==================== HTTP 状态码错误 ====================

export const HTTP_STATUS_ERROR = {
  400: {
    code: 400,
    message: '请求参数有误',
    level: ERROR_LEVEL.TIP
  },
  401: {
    code: 401,
    message: '登录已过期，请重新登录',
    level: ERROR_LEVEL.FATAL,
    redirect: '/login'
  },
  403: {
    code: 403,
    message: '您没有权限执行此操作',
    level: ERROR_LEVEL.FATAL,
    redirect: '/403'
  },
  404: {
    code: 404,
    message: '请求的资源不存在',
    level: ERROR_LEVEL.FATAL,
    redirect: '/404'
  },
  405: {
    code: 405,
    message: '不支持的请求方法',
    level: ERROR_LEVEL.WARN
  },
  408: {
    code: 408,
    message: '请求超时，请稍后重试',
    level: ERROR_LEVEL.TIP
  },
  413: {
    code: 413,
    message: '上传文件过大',
    level: ERROR_LEVEL.TIP
  },
  422: {
    code: 422,
    message: '请求参数校验失败',
    level: ERROR_LEVEL.TIP
  },
  429: {
    code: 429,
    message: '请求过于频繁，请稍后重试',
    level: ERROR_LEVEL.TIP
  },
  500: {
    code: 500,
    message: '服务器内部错误',
    level: ERROR_LEVEL.FATAL,
    redirect: '/500'
  },
  502: {
    code: 502,
    message: '网关错误',
    level: ERROR_LEVEL.FATAL,
    redirect: '/500'
  },
  503: {
    code: 503,
    message: '服务暂时不可用',
    level: ERROR_LEVEL.FATAL,
    redirect: '/500'
  },
  504: {
    code: 504,
    message: '网关超时',
    level: ERROR_LEVEL.FATAL,
    redirect: '/500'
  }
}

// ==================== 业务错误码（与 renren-fast 后端约定对应） ====================

/**
 * 业务错误码映射表
 * key: 后端返回的 code 值
 * value: { message, level, action? }
 *
 * 约定规则：
 *  - 0: 成功（非错误）
 *  - 401: token 失效 / 未授权（已在 HTTP 层面处理，此处为业务兜底）
 *  - 403: 无权限
 *  - 500: 业务执行异常
 *  - 其他业务码由各模块自行扩展
 */
export const BUSINESS_ERROR = {
  // ---- 通用 ----
  401: {
    message: '登录已过期，请重新登录',
    level: ERROR_LEVEL.FATAL,
    action: 'logout'
  },
  403: {
    message: '权限不足，无法执行此操作',
    level: ERROR_LEVEL.WARN
  },
  404: {
    message: '请求的数据不存在',
    level: ERROR_LEVEL.TIP
  },
  500: {
    message: '服务处理异常，请联系管理员',
    level: ERROR_LEVEL.WARN
  },
  // ---- 表单/参数校验（适用于 JSR303 参数校验场景） ----
  /** 通用参数校验失败 */
  PARAM_VALID: {
    code: 'VALID001',
    message: '参数校验不通过',
    level: ERROR_LEVEL.TIP
  },
  /** 字段级校验失败，返回格式：{ field: "xxx", msg: "xxx" } */
  FIELD_VALID: {
    code: 'VALID002',
    message: '请检查表单输入',
    level: ERROR_LEVEL.TIP,
    isFieldError: true // 标记为字段级错误，可用于内联提示
  },
  // ---- 登录 / 认证 ----
  CAPTCHA_ERROR: {
    code: 'AUTH001',
    message: '验证码错误',
    level: ERROR_LEVEL.TIP
  },
  ACCOUNT_LOCKED: {
    code: 'AUTH002',
    message: '账号已被锁定',
    level: ERROR_LEVEL.WARN
  },
  CREDENTIALS_ERROR: {
    code: 'AUTH003',
    message: '用户名或密码错误',
    level: ERROR_LEVEL.TIP
  },
  // ---- 业务通用 ----
  DUPLICATE_KEY: {
    code: 'BIZ001',
    message: '数据已存在，请勿重复添加',
    level: ERROR_LEVEL.TIP
  },
  DATA_NOT_FOUND: {
    code: 'BIZ002',
    message: '数据不存在或已被删除',
    level: ERROR_LEVEL.TIP
  },
  DATA_IN_USE: {
    code: 'BIZ003',
    message: '数据正在被使用，无法删除',
    level: ERROR_LEVEL.WARN
  },
  OPERATION_FAILED: {
    code: 'BIZ004',
    message: '操作执行失败',
    level: ERROR_LEVEL.WARN
  },
  // ---- 文件上传 ----
  FILE_TOO_LARGE: {
    code: 'FILE001',
    message: '文件大小超过限制',
    level: ERROR_LEVEL.TIP
  },
  FILE_TYPE_INVALID: {
    code: 'FILE002',
    message: '不支持的文件类型',
    level: ERROR_LEVEL.TIP
  },
  UPLOAD_FAILED: {
    code: 'FILE003',
    message: '文件上传失败',
    level: ERROR_LEVEL.WARN
  }
}

/**
 * 根据后端错误码获取业务错误配置
 * @param {number|string} code 后端返回的 code 字段
 * @returns {object|null} 匹配的业务错误配置，未匹配返回 null
 */
export function matchBusinessError (code) {
  // 精确匹配
  if (BUSINESS_ERROR[code]) {
    return BUSINESS_ERROR[code]
  }
  // 遍历特殊 code 字段的对象
  const keys = Object.keys(BUSINESS_ERROR)
  for (let i = 0; i < keys.length; i++) {
    const item = BUSINESS_ERROR[keys[i]]
    if (item.code === code) {
      return item
    }
  }
  return null
}

/**
 * 根据 HTTP 状态码获取错误配置
 * @param {number} status HTTP 状态码
 * @returns {object} 匹配的 HTTP 状态码错误配置
 */
export function matchHttpStatusError (status) {
  return HTTP_STATUS_ERROR[status] || {
    code: status,
    message: `服务异常（${status}）`,
    level: ERROR_LEVEL.WARN
  }
}
