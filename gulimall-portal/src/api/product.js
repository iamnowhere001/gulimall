import request from './request'

// 三级分类树（已存在：gulimall-product CategoryController）
export function getCategoryTree() {
  return request.get('/product/category/list/tree')
}

// 首页一/二/三级分类导航 JSON（已存在：IndexController /index/catalog.json）
export function getCatalogJson() {
  return request.get('/product/index/catalog.json')
}

// SKU 基础信息（已存在：SkuInfoController /info/{skuId}，仅基础字段）
export function getSkuInfo(skuId) {
  return request.get(`/product/skuinfo/info/${skuId}`)
}

// 商品详情聚合（待后端新增：返回 SkuItemVo 风格的完整详情 JSON）
export function getSkuDetail(skuId) {
  return request.get(`/product/sku/${skuId}/detail`)
}
