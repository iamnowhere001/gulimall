import axios from 'axios'

const TOKEN_KEY = 'gulimall_token'

// 统一请求实例：baseURL 为 /api，由 Vite(dev) 或 nginx(prod) 代理到网关(gulimall-gateway:88)
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器：自动携带 X-Auth-Token 头（后端 HeaderHttpSessionIdResolver 据此还原 Session）
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(TOKEN_KEY)
    if (token) {
      config.headers['X-Auth-Token'] = token
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：
// 1. 捕获后端通过 X-Auth-Token 响应头返回的 Session ID（新会话创建时），存入 localStorage
// 2. 后端统一返回包装结构 R{ code, msg, data }，code===0 时返回 data 字段
// 3. 401 未登录时清除 token 并跳转登录页
request.interceptors.response.use(
  (response) => {
    // 捕获响应头中的 token（后端 HeaderHttpSessionIdResolver 在新会话创建时写入）
    const respToken = response.headers['x-auth-token']
    if (respToken) {
      localStorage.setItem(TOKEN_KEY, respToken)
    }

    const data = response.data
    if (data && typeof data === 'object' && 'code' in data) {
      if (data.code === 0) {
        return data.data
      }
      // 401 未登录：清除 token
      if (data.code === 401) {
        localStorage.removeItem(TOKEN_KEY)
      }
      return Promise.reject(new Error(data.msg || '请求失败'))
    }
    return data
  },
  (error) => {
    // HTTP 401：后端拦截器返回的未登录错误
    if (error.response && error.response.status === 401) {
      localStorage.removeItem(TOKEN_KEY)
      // 避免在登录页重复跳转
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default request
export { TOKEN_KEY }
