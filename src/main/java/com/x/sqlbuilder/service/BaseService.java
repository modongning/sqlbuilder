package com.x.sqlbuilder.service;

import com.x.sqlbuilder.vo.Page;

import java.util.List;

/**
 * @Author modongning
 * @updateBy modongning
 * @updateBy 2018/3/17 下午4:33
 */
public interface BaseService<T> {
	int save(T t);
	int update(T t);
	T querySingle(T t);
	List<T> queryAll(T t);
	Page<T> pageQuery(Page<T> page);
	int delete(T t);
}
