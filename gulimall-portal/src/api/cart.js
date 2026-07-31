import request from './request'

// 购物车（后端 /cart/items 等，返回 CartVo）
export function getCart() {
  return request.get('/cart/items')
}

export function addToCart(skuId, num = 1) {
  return request.post('/cart/add', null, { params: { skuId, num } })
}

export function updateCartItem(skuId, num) {
  return request.post('/cart/update', null, { params: { skuId, num } })
}

export function removeCartItem(skuId) {
  return request.delete(`/cart/remove/${skuId}`)
}
