package com.xunqi.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xunqi.common.xss.SQLFilter;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * 查询参数工具类。
 *
 * 从前端传入的分页请求参数（Map）中解析页码、每页大小与排序字段，
 * 构造 MyBatis-Plus 的 {@link Page} 分页对象，供 Mapper 查询使用。
 * 这是 renren 后台通用的分页参数解析方式，支持前端排序与默认排序。
 */
public class Query<T> {

    /**
     * 使用默认排序（无默认排序字段）构造分页对象。
     * @param params 前端请求参数（含 page、limit、sidx、order 等键）
     * @return MyBatis-Plus 分页对象
     */
    public IPage<T> getPage(Map<String, Object> params) {
        return this.getPage(params, null, false);
    }

    /**
     * 构造分页对象并解析排序。
     * @param params              前端请求参数（含 page、limit、sidx、order 等键）
     * @param defaultOrderField   未传排序字段时使用的默认排序字段
     * @param isAsc               默认排序是否升序
     * @return 已设置好页码与排序的 MyBatis-Plus 分页对象
     */
    public IPage<T> getPage(Map<String, Object> params, String defaultOrderField, boolean isAsc) {
        //分页参数：默认第 1 页、每页 10 条
        long curPage = 1;
        long limit = 10;

        // 从请求参数中解析页码与每页大小（page、limit）
        if(params.get(Constant.PAGE) != null){
            curPage = Long.parseLong((String)params.get(Constant.PAGE));
        }
        if(params.get(Constant.LIMIT) != null){
            limit = Long.parseLong((String)params.get(Constant.LIMIT));
        }

        // 构造 MyBatis-Plus 分页对象
        Page<T> page = new Page<>(curPage, limit);

        // 将分页对象回写到参数中，便于后续 XML 中引用或透传
        params.put(Constant.PAGE, page);

        // 解析排序字段：先经 SQLFilter 过滤，防止 SQL 注入
        // （因为排序字段是通过拼接 SQL 实现的，存在注入风险，必须过滤）
        String orderField = SQLFilter.sqlInject((String)params.get(Constant.ORDER_FIELD));
        String order = (String)params.get(Constant.ORDER);

        // 前端显式指定了排序字段时，按前端要求排序
        if(StringUtils.isNotEmpty(orderField) && StringUtils.isNotEmpty(order)){
            if(Constant.ASC.equalsIgnoreCase(order)) {
                return  page.addOrder(OrderItem.asc(orderField));
            }else {
                return page.addOrder(OrderItem.desc(orderField));
            }
        }

        // 未指定排序字段且无默认排序字段，则不排序直接返回
        if(StringUtils.isBlank(defaultOrderField)){
            return page;
        }

        // 使用默认排序字段排序
        if(isAsc) {
            page.addOrder(OrderItem.asc(defaultOrderField));
        }else {
            page.addOrder(OrderItem.desc(defaultOrderField));
        }

        return page;
    }
}
