import request from './request'

// 订单确认页数据（后端 /order/confirm，返回 OrderConfirmVo）
export function getOrderConfirm() {
  return request.get('/order/confirm')
}

// 提交订单（后端 /order/submit，需传 OrderSubmitVo：addrId / payType / orderToken）
export function submitOrder(data) {
  return request.post('/order/submit', data)
}

// 我的订单列表（后端 /order/myOrders，返回 PageUtils）
export function getMyOrders(params = {}) {
  return request.get('/order/myOrders', {
    params: {
      page: params.page || 1,
      limit: params.limit || 10
    }
  })
}
