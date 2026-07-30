package com.xunqi.gulimall.search.service;

import com.xunqi.common.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * 商品上架（写入 ES）服务接口。
 * 由商品服务在上架时调用，将 SKU 列表批量导入 ES 索引。
 */
public interface ProductSaveService {

    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
