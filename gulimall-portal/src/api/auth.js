import request from './request'

// 登录（待后端新增 JSON 接口，替代原 Thymeleaf login.html 表单提交）
export function login(data) {
  return request.post('/auth/login', data)
}

// 注册
export function register(data) {
  return request.post('/auth/register', data)
}

// 发送短信验证码
export function sendCode(phone) {
  return request.get('/auth/sms/sendCode', { params: { phone } })
}
