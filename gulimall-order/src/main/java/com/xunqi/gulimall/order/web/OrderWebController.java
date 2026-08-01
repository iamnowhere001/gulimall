package com.xunqi.gulimall.order.web;

import com.xunqi.common.exception.NoStockException;
import com.xunqi.common.utils.PageUtils;
import com.xunqi.common.utils.R;
import com.xunqi.gulimall.order.service.OrderService;
import com.xunqi.gulimall.order.vo.OrderConfirmVo;
import com.xunqi.gulimall.order.vo.OrderSubmitVo;
import com.xunqi.gulimall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单页（Thymeleaf）控制器，承载用户下单主流程的页面跳转。
 *
 *  - /toTrade：进入结算确认页，调用 confirmOrder() 并发查询收货地址、购物车勾选项与库存；
 *  - /submitOrder：提交下单，调用 submitOrder()（含防重令牌、价格校验、锁库存），
 *    成功后跳转支付页（pay），失败/库存不足则携带提示重定向回确认页。
 */
@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    /**
     * 去结算确认页
     * @param model
     * @param request
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping(value = "/toTrade")
    public String toTrade(Model model, HttpServletRequest request) throws ExecutionException, InterruptedException {

        OrderConfirmVo confirmVo = orderService.confirmOrder();

        model.addAttribute("confirmOrderData",confirmVo);
        //展示订单确认的数据

        return "confirm";
    }

    /**
     * 下单功能
     * @param vo
     * @return
     */
    @PostMapping(value = "/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes attributes) {

        try {
            SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);
            //下单成功来到支付选择页
            //下单失败回到订单确认页重新确定订单信息
            if (responseVo.getCode() == 0) {
                //成功
                model.addAttribute("submitOrderResp",responseVo);
                return "pay";
            } else {
                String msg = "下单失败";
                switch (responseVo.getCode()) {
                    case 1: msg += "令牌订单信息过期，请刷新再次提交"; break;
                    case 2: msg += "订单商品价格发生变化，请确认后再次提交"; break;
                    case 3: msg += "库存锁定失败，商品库存不足"; break;
                }
                attributes.addFlashAttribute("msg",msg);
                return "redirect:http://order.gulimall.com/toTrade";
            }
        } catch (Exception e) {
            if (e instanceof NoStockException) {
                String message = ((NoStockException)e).getMessage();
                attributes.addFlashAttribute("msg",message);
            }
            return "redirect:http://order.gulimall.com/toTrade";
        }
    }

    /* ============== 以下为前台门户(gulimall-portal) JSON 接口 ============== */

    /**
     * 订单确认页数据（JSON）
     * 网关 /api/order/confirm -> /order/confirm
     */
    @ResponseBody
    @GetMapping(value = "/order/confirm")
    public R confirm() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        return R.ok().put("data", confirmVo);
    }

    /**
     * 提交订单（JSON）
     * 网关 /api/order/submit -> /order/submit
     */
    @ResponseBody
    @PostMapping(value = "/order/submit")
    public R submit(@RequestBody OrderSubmitVo vo) {
        try {
            SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);
            if (responseVo.getCode() == 0) {
                return R.ok().put("data", responseVo);
            }
            return R.error(responseVo.getCode(), "下单失败，请稍后重试");
        } catch (Exception e) {
            return R.error(500, "下单异常：" + e.getMessage());
        }
    }

    /**
     * 我的订单列表（JSON，分页）
     * 网关 /api/order/myOrders -> /order/myOrders
     *
     * @param page  当前页码，默认 1
     * @param limit 每页条数，默认 10
     */
    @ResponseBody
    @GetMapping(value = "/order/myOrders")
    public R myOrders(@RequestParam(value = "page", defaultValue = "1") String page,
                      @RequestParam(value = "limit", defaultValue = "10") String limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("limit", limit);
        PageUtils pageUtils = orderService.queryPageWithItem(params);
        return R.ok().put("data", pageUtils);
    }

}
