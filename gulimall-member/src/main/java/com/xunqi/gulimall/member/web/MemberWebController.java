package com.xunqi.gulimall.member.web;

import com.xunqi.common.utils.R;
import com.xunqi.gulimall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 会员中心 Web 控制器
 * 渲染会员相关页面（如我的订单）
 */
@Controller
public class MemberWebController {

    @Autowired
    private OrderFeignService orderFeignService;

    /**
     * 我的订单页面
     * 远程调用订单服务分页查询当前登录用户的订单列表
     *
     * @param pageNum  页码
     * @param model    视图模型
     * @param request  当前请求
     * @return 订单列表页面
     */
    @GetMapping(value = "/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum",required = false,defaultValue = "0") Integer pageNum,
                                  Model model, HttpServletRequest request) {

        // 构建分页查询参数
        Map<String,Object> page = new HashMap<>();
        page.put("page",pageNum.toString());

        // 远程查询订单服务获取订单列表数据
        R orderInfo = orderFeignService.listWithItem(page);
        model.addAttribute("orders", orderInfo);

        return "orderList";
    }

}
