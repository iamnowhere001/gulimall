package com.xunqi.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一返回结果（Result）封装类。
 * 继承 HashMap，默认携带 code（状态码）与 msg（提示信息），业务数据统一放在 data 字段。
 * 提供 ok()/error() 静态工厂方法，以及 setData()/getData() 便捷存取，
 * 便于各微服务 Controller 以一致结构返回数据。
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	/**
	 * 向返回结果中放入业务数据，key 固定为 "data"。
	 * @param data 任意业务对象
	 * @return 当前 R 对象（链式调用）
	 */
	public R setData(Object data) {
		put("data",data);
		return this;
	}

	/** 利用 fastjson 将 data 字段反序列化为指定类型 */
	public <T> T getData(TypeReference<T> typeReference) {
		Object data = get("data");	//默认是map
		String jsonString = JSON.toJSONString(data);
		T t = JSON.parseObject(jsonString, typeReference);
		return t;
	}

	/** 利用 fastjson 将指定 key 的字段反序列化为指定类型 */
	public <T> T getData(String key,TypeReference<T> typeReference) {
		Object data = get(key);	//默认是map
		String jsonString = JSON.toJSONString(data);
		T t = JSON.parseObject(jsonString, typeReference);
		return t;
	}

	/** 默认构造：code=0，msg=success，表示成功 */
	public R() {
		put("code", 0);
		put("msg", "success");
	}
	
	/** 返回系统异常（500）的统一结果 */
	public static R error() {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
	}
	
	/** 返回带自定义提示的异常结果（状态码 500） */
	public static R error(String msg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
	}
	
	/** 返回指定状态码与提示的异常结果 */
	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	/** 返回带自定义提示的成功结果 */
	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}
	
	/** 返回携带额外键值对map的成功结果 */
	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}
	
	/** 返回默认成功结果 */
	public static R ok() {
		return new R();
	}

	/** 链式放入单键值对（覆盖父类 put 以返回 R 便于链式调用） */
	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}

	/** 获取状态码 code */
	public Integer getCode() {

		return (Integer) this.get("code");
	}

}
