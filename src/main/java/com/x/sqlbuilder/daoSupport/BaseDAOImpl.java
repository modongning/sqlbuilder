package com.x.sqlbuilder.daoSupport;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.x.sqlbuilder.jdbchelper.JdbcHelper;
import com.x.sqlbuilder.vo.Page;

public class BaseDAOImpl<T> implements BaseDAO<T> {

	protected JdbcTemplate jdbcTemplate;

	@Override
	public List<T> query(T t) {
		JdbcHelper<T> helper = new JdbcHelper<>(this.jdbcTemplate);
		return helper.query(t);
	}

	@Override
	public Page<T> query(Page<T> page) {
		JdbcHelper<T> helper = new JdbcHelper<>(this.jdbcTemplate);
		int pageSize = page.getPageSize();
		int pageNumber = page.getPageNumber();
		int firstResult = (pageNumber - 1) * pageSize;
		List<T> dataList = helper.query(page.getQueryObj(), firstResult, pageSize);
		int count = helper.queryCount(page.getQueryObj());
		page.setTotal(count);
		page.setData(dataList);
		return page;
	}
	
	@Override
	public Page<T> query(Page<T> page, List<String> orderByKeys, String orderBy) {
		JdbcHelper<T> helper = new JdbcHelper<>(this.jdbcTemplate);
		int pageSize = page.getPageSize();
		int pageNumber = page.getPageNumber();
		int firstResult = (pageNumber - 1) * pageSize;
		List<T> dataList = helper.query(page.getQueryObj(),orderByKeys,orderBy,firstResult, pageSize);
		int count = helper.queryCount(page.getQueryObj());
		page.setTotal(count);
		page.setData(null == dataList || dataList.isEmpty() ? new ArrayList<>() : dataList);
		return page;
	}

	@Override
	public int save(T t) {
		JdbcHelper<T> helper = new JdbcHelper<>(this.jdbcTemplate);
		return helper.save(t);
	}

	@Override
	public int[] batchSave(List<T> list) {
		JdbcHelper<T> helper = new JdbcHelper<>(this.jdbcTemplate);
		return helper.batchSave(list);
	}

	@Override
	public int update(T t) {
		JdbcHelper<T> helper = new JdbcHelper<>(this.jdbcTemplate);
		return helper.update(t);
	}

	@Override
	public int[] batchUpdate(List<T> list) {
		JdbcHelper<T> helper = new JdbcHelper<>(this.jdbcTemplate);
		return helper.batchUpdate(list);
	}

	@Override
	public long saveReturnKey(T t) {
		JdbcHelper<T> helper = new JdbcHelper<>(this.jdbcTemplate);
		return helper.saveReturnKey(t);
	}

}
