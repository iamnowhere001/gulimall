package com.xunqi.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xunqi.common.utils.PageUtils;
import com.xunqi.common.utils.Query;
import com.xunqi.common.utils.R;
import com.xunqi.gulimall.ware.dao.WareInfoDao;
import com.xunqi.gulimall.ware.entity.WareInfoEntity;
import com.xunqi.gulimall.ware.feign.MemberFeignService;
import com.xunqi.gulimall.ware.service.WareInfoService;
import com.xunqi.gulimall.ware.vo.FareVo;
import com.xunqi.gulimall.ware.vo.MemberAddressVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    private MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");

        if (!StringUtils.isEmpty(key)) {
            queryWrapper.eq("id",key)
                    .or().like("name",key)
                    .or().like("address",key)
                    .or().like("areacode",key);
        }


        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 计算运费
     * @param addrId
     * @return
     */
    @Override
    public FareVo getFare(Long addrId) {

        FareVo fareVo = new FareVo();

        //收获地址的详细信息
        R addrInfo = memberFeignService.info(addrId);

        MemberAddressVo memberAddressVo = addrInfo.getData("memberReceiveAddress",new TypeReference<MemberAddressVo>() {});

        if (memberAddressVo != null) {
            // 运费计算：基于收货地址的省级行政区划，使用固定运费（教程占位实现）
            BigDecimal fare = new BigDecimal("10.00");

            fareVo.setFare(fare);
            fareVo.setAddress(memberAddressVo);

            return fareVo;
        }
        // 地址不存在时返回默认运费而非null，避免调用方NPE
        fareVo.setFare(new BigDecimal("0"));
        return fareVo;
    }

}