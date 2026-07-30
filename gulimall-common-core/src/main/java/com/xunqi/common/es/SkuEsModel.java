package com.xunqi.common.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品 SKU 在 Elasticsearch 中的索引文档模型。
 * 用于商品检索服务（gulimall-search）建立检索索引、接收检索结果及参与聚合筛选。
 */
@Data
public class SkuEsModel {

    /** SKU 编号 */
    private Long skuId;

    /** 所属 SPU 编号 */
    private Long spuId;

    /** SKU 标题（检索主字段，参与全文检索） */
    private String skuTitle;

    /** SKU 价格 */
    private BigDecimal skuPrice;

    /** SKU 默认图片地址 */
    private String skuImg;

    /** 销量 */
    private Long saleCount;

    /** 是否有库存 */
    private Boolean hasStock;

    /** 热度评分（用于综合排序） */
    private Long hotScore;

    /** 品牌编号 */
    private Long brandId;

    /** 分类编号 */
    private Long catalogId;

    /** 品牌名称 */
    private String brandName;

    /** 品牌 Logo 地址 */
    private String brandImg;

    /** 分类名称 */
    private String catalogName;

    /** 可检索的基本属性列表（以 nested 形式存储，便于精确筛选） */
    private List<Attrs> attrs;

    /** SKU 的可检索基本属性 */
    @Data
    public static class Attrs {

        /** 属性编号 */
        private Long attrId;

        /** 属性名称 */
        private String attrName;

        /** 属性值 */
        private String attrValue;

    }

}
