package com.xunqi.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 会员专属价格（SKU 的会员阶梯价）传输对象。
 * 用于商品上架时把不同会员等级对应的优惠价同步给相关服务。
 */
@Data
public class MemberPrice {

  /** 会员等级 id */
  private Long id;
  /** 会员等级名称 */
  private String name;
  /** 该会员等级对应的价格 */
  private BigDecimal price;

}
