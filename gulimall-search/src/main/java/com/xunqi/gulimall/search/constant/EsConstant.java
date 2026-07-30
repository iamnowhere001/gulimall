package com.xunqi.gulimall.search.constant;

/**
 * Elasticsearch 常量定义。
 * 集中管理检索服务的索引名与分页大小，避免硬编码。
 */
public class EsConstant {

    /** 商品检索索引名（ES 中的 index）：gulimall_product */
    public static final String PRODUCT_INDEX = "gulimall_product";

    /** 检索结果每页大小 */
    public static final Integer PRODUCT_PAGESIZE = 16;
}
