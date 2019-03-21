package com.x.sqlbuilder.daoSupport;

import java.util.List;

import com.x.sqlbuilder.vo.Page;

public interface BaseDAO<T> {
	/**
	 * 根据对象查询
	 * @param t
	 * @return
	 */
	public List<T> query(T t);
	/**
	 * 根据对象分页查询
	 * @param page
	 * @return
	 */
	public Page<T> query(Page<T> page);
	/**
	 * 根据对象分页查询并排序
	 * @param page
	 * @param orderByKeys
	 * @param orderBy
	 * @return
	 */
	public Page<T> query(Page<T> page,List<String> orderByKeys,String orderBy);
	/**
	 * 保存
	 * @param t
	 * @return
	 */
	public int save(T t);
	/**
	 * 保存并返回主键
	 * @param t
	 * @return
	 */
	public long saveReturnKey(T t);
	/**
	 * 批量保存
	 * @param list
	 * @return
	 */
	public int[] batchSave(List<T> list);
	/**
	 * 更新
	 * @param t
	 * @return
	 */
	public int update(T t);
	/**
	 * 批量更新
	 * @param list
	 * @return
	 */
	public int[] batchUpdate(List<T> list);
}
