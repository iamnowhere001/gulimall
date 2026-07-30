package com.xunqi.gulimall.search.service;

import com.xunqi.gulimall.search.vo.SearchParam;
import com.xunqi.gulimall.search.vo.SearchResult;

/**
 * 商品检索服务接口。
 * 根据页面检索参数（SearchParam）在 ES 中执行查询与聚合，返回页面渲染所需的 SearchResult。
 */
public interface MallSearchService {

    /**
     * @param param 检索的所有参数
     * @return  返回检索的结果，里面包含页面需要的所有信息
     */
    SearchResult search(SearchParam param);
}
