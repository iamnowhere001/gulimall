import request from './request'

// 订单确认页数据（后端 /order/confirm，返回 OrderConfirmVo）
export function getOrderConfirm() {
  return request.get('/order/confirm')
}

// 提交订单（后端 /order/submit，需传 OrderSubmitVo：addrId / payType / orderToken）
export function submitOrder(data) {
  return request.post('/order/submit', data)
}

// 我的订单列表（待后端补充 /order/list）
export function getMyOrders(params) {
  return request.get('/order/list', { params })
}
