package com.xunqi.gulimall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunqi.common.to.MemberPrice;
import com.xunqi.common.to.SkuReductionTo;
import com.xunqi.common.utils.PageUtils;
import com.xunqi.common.utils.Query;
import com.xunqi.gulimall.coupon.dao.SkuFullReductionDao;
import com.xunqi.gulimall.coupon.entity.MemberPriceEntity;
import com.xunqi.gulimall.coupon.entity.SkuFullReductionEntity;
import com.xunqi.gulimall.coupon.entity.SkuLadderEntity;
import com.xunqi.gulimall.coupon.service.MemberPriceService;
import com.xunqi.gulimall.coupon.service.SkuFullReductionService;
import com.xunqi.gulimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SKU 优惠（满减 / 折扣 / 会员价）服务实现。
 *
 * 商品上架时由 product 服务调用 {@link #saveSkuReduction(SkuReductionTo)}，
 * 将优惠规则拆分落库到三张表：
 *  - sms_sku_ladder（满件打折，数量门槛）
 *  - sms_sku_full_reduction（满额减价，金额门槛）
 *  - sms_member_price（各会员等级的专属价格）
 */
@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<SkuFullReductionEntity> queryWrapper = new QueryWrapper<SkuFullReductionEntity>();

        String key = (String) params.get("key");

        if (!StringUtils.isEmpty(key)) {
            queryWrapper.eq("id",key).or().eq("sku_id",key);
        }

        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 保存 SKU 的全部优惠规则（满件打折 + 满额减价 + 会员价）。
     * 入参由商品服务在商品上架时通过 MQ/Feign 传入。
     * @param skuReductionTo 包含满减/折扣/会员价的优惠传输对象
     */
    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {

        //1、保存满减打折、会员价
        //1.1 保存 sku 的满件打折信息到 sms_sku_ladder（仅当满件数量 > 0 时落库）
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuReductionTo,skuLadderEntity);
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());

        if (skuReductionTo.getFullCount() > 0) {
            skuLadderService.save(skuLadderEntity);
        }

        //2、保存 sms_sku_full_reduction（满额减价，仅当满减金额 > 0 时落库）
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo,skuFullReductionEntity);
        if (skuFullReductionEntity.getFullPrice().compareTo(BigDecimal.ZERO) == 1) {
            this.save(skuFullReductionEntity);
        }

        //3、保存 sms_member_price（各会员等级价格，过滤掉价格为 0 的项）
        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();

        List<MemberPriceEntity> collect = memberPrice.stream().map(mem -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
            memberPriceEntity.setMemberLevelId(mem.getId());
            memberPriceEntity.setMemberLevelName(mem.getName());
            memberPriceEntity.setMemberPrice(mem.getPrice());
            memberPriceEntity.setAddOther(1);
            return memberPriceEntity;
        }).filter(item -> {
            return item.getMemberPrice().compareTo(BigDecimal.ZERO) == 1;
        }).collect(Collectors.toList());

        memberPriceService.saveBatch(collect);
    }

}
