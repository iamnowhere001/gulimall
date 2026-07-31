import axios from 'axios'

// 统一请求实例：baseURL 为 /api，由 Vite(dev) 或 nginx(prod) 代理到网关(gulimall-gateway:88)
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 后端统一返回包装结构 R{ code, msg, data }
request.interceptors.response.use(
  (response) => {
    const data = response.data
    if (data && typeof data === 'object' && 'code' in data) {
      if (data.code === 0) {
        return data.data
      }
      return Promise.reject(new Error(data.msg || '请求失败'))
    }
    return data
  },
  (error) => {
    return Promise.reject(error)
  }
)

export default request
