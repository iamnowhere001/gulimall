package com.xunqi.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.Serializable;
import java.util.List;

/**
 * 分页工具类（统一分页返回结构）。
 *
 * 将数据库/MyBatis-Plus 的分页结果封装成前端统一需要的结构：
 * 总记录数、每页大小、总页数、当前页、当前页数据列表。
 * 各微服务 Controller 返回分页数据时使用，便于前端直接渲染分页组件。
 */
public class PageUtils implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 总记录数 */
	private int totalCount;
	/** 每页记录数 */
	private int pageSize;
	/** 总页数 */
	private int totalPage;
	/** 当前页数 */
	private int currPage;
	/** 当前页的数据列表 */
	private List<?> list;
	
	/**
	 * 由原始列表与分页元信息构造分页对象。
	 * @param list        当前页的数据列表
	 * @param totalCount  符合条件的总记录数
	 * @param pageSize    每页记录数
	 * @param currPage    当前页码
	 */
	public PageUtils(List<?> list, int totalCount, int pageSize, int currPage) {
		this.list = list;
		this.totalCount = totalCount;
		this.pageSize = pageSize;
		this.currPage = currPage;
		// 通过向上取整计算总页数，避免整除丢失最后一页
		this.totalPage = (int)Math.ceil((double)totalCount/pageSize);
	}

	/**
	 * 由 MyBatis-Plus 的 {@link IPage} 直接构造分页对象，自动转换各字段。
	 * @param page MyBatis-Plus 分页查询结果
	 */
	public PageUtils(IPage<?> page) {
		this.list = page.getRecords();
		this.totalCount = (int)page.getTotal();
		this.pageSize = (int)page.getSize();
		this.currPage = (int)page.getCurrent();
		this.totalPage = (int)page.getPages();
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}
	
}
