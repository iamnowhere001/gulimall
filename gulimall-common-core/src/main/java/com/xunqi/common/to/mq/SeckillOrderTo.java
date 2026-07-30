package com.xunqi.common.to.mq;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 秒杀订单消息对象（MQ）。
 * 由秒杀服务（gulimall-seckill）在秒杀成功后发送到消息队列，
 * 订单服务消费后异步创建真实订单。包含订单号、场次、SKU、秒杀价、数量、会员等。
 */
@Data
public class SeckillOrderTo {

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;

    /**
     * 购买数量
     */
    private Integer num;

    /**
     * 会员ID
     */
    private Long memberId;

}
