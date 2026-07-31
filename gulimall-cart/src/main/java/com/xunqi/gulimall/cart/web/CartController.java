package com.xunqi.gulimall.cart.web;

import com.xunqi.common.utils.R;
import com.xunqi.gulimall.cart.service.CartService;
import com.xunqi.gulimall.cart.vo.CartItemVo;
import com.xunqi.gulimall.cart.vo.CartVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {

    @Resource
    private CartService cartService;

    /**
     * 获取当前用户的购物车商品项（供订单等服务远程调用）
     */
    @GetMapping(value = "/currentUserCartItems")
    @ResponseBody
    public List<CartItemVo> getCurrentCartItems() {
        return cartService.getUserCartItems();
    }

    /**
     * 去购物车页面的请求（Thymeleaf）
     */
    @GetMapping(value = "/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        CartVo cartVo = cartService.getCart();
        model.addAttribute("cart",cartVo);
        return "cartList";
    }

    /**
     * 添加商品到购物车（Thymeleaf 重定向）
     */
    @GetMapping(value = "/addCartItem")
    public String addCartItem(@RequestParam("skuId") Long skuId,
                              @RequestParam("num") Integer num,
                              org.springframework.web.servlet.mvc.support.RedirectAttributes attributes) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId,num);
        attributes.addAttribute("skuId",skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccessPage.html";
    }

    /**
     * 跳转到添加购物车成功页面
     */
    @GetMapping(value = "/addToCartSuccessPage.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model) {
        CartItemVo cartItemVo = cartService.getCartItem(skuId);
        model.addAttribute("cartItem",cartItemVo);
        return "success";
    }

    @GetMapping(value = "/checkItem")
    public String checkItem(@RequestParam(value = "skuId") Long skuId,
                            @RequestParam(value = "checked") Integer checked) {
        cartService.checkItem(skuId,checked);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping(value = "/countItem")
    public String countItem(@RequestParam(value = "skuId") Long skuId,
                            @RequestParam(value = "num") Integer num) {
        cartService.changeItemCount(skuId,num);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping(value = "/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId) {
        cartService.deleteIdCartInfo(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /* ============== 以下为前台门户(gulimall-portal) JSON 接口 ============== */

    /**
     * 获取完整购物车
     * 网关 /api/cart/items -> /cart/items
     */
    @GetMapping(value = "/cart/items")
    @ResponseBody
    public CartVo cartItems() throws ExecutionException, InterruptedException {
        return cartService.getCart();
    }

    /**
     * 加入购物车
     * 网关 /api/cart/add?skuId=&num= -> /cart/add
     */
    @PostMapping(value = "/cart/add")
    @ResponseBody
    public R addItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId, num);
        return R.ok();
    }

    /**
     * 修改购物项数量
     */
    @PostMapping(value = "/cart/update")
    @ResponseBody
    public R updateCount(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) {
        cartService.changeItemCount(skuId, num);
        return R.ok();
    }

    /**
     * 删除购物项
     * 网关 /api/cart/remove/{skuId} -> /cart/remove/{skuId}
     */
    @DeleteMapping(value = "/cart/remove/{skuId}")
    @ResponseBody
    public R removeItem(@PathVariable("skuId") Long skuId) {
        cartService.deleteIdCartInfo(skuId);
        return R.ok();
    }
}
