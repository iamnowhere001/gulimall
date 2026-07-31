import request from './request'

// 商品检索（后端 /search/list，返回 SearchResult）
// SearchParam 字段：keyword / catalog3Id / brandId / attrs / sort / hasStock / pageNum
export function searchProducts(params = {}) {
  return request.get('/search/list', {
    params: {
      keyword: params.keyword,
      catalog3Id: params.catalog3Id,
      brandId: params.brandId,
      attrs: params.attrs,
      sort: params.sort,
      hasStock: params.hasStock,
      pageNum: params.pageNum || 1
    }
  })
}
