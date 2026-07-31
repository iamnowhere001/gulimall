package com.xunqi.gulimall.search.web;

import com.xunqi.gulimall.search.service.MallSearchService;
import com.xunqi.gulimall.search.vo.SearchParam;
import com.xunqi.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 检索页控制器。
 *  - /list.html：Thymeleaf 页面（原前台）
 *  - /search/list：供前台门户(gulimall-portal)使用的检索 JSON 接口
 *    网关 /api/search/list -> /search/list
 */
@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;

    @GetMapping(value = "/list.html")
    public String listPage(SearchParam param, Model model, HttpServletRequest request) {

        param.set_queryString(request.getQueryString());

        //1、根据传递来的页面的查询参数，去es中检索商品
        SearchResult result = mallSearchService.search(param);

        model.addAttribute("result",result);

        return "list";
    }

    /**
     * 检索 JSON 接口（前台门户调用）
     */
    @GetMapping(value = "/search/list")
    @ResponseBody
    public SearchResult list(SearchParam param, HttpServletRequest request) {
        param.set_queryString(request.getQueryString());
        return mallSearchService.search(param);
    }

}
