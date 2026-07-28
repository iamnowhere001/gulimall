package com.xunqi.gulimall.member.feign;

import com.xunqi.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 优惠券服务 Feign 客户端
 * 远程调用 gulimall-coupon 服务查询会员优惠券
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    /**
     * 查询当前会员的优惠券列表
     */
    @RequestMapping("/coupon/coupon/member/list")
    public R membercoupons();

}
