package com.x.sqlbuilder.jdbchelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import com.x.sqlbuilder.builder.SqlBuilder;
import com.x.sqlbuilder.handler.ResultSetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * JDBC帮助工具
 *
 * @param <T>
 * @author Mo
 * @version 1.2
 */
public class JdbcHelper<T> {
	private static Logger log = LoggerFactory.getLogger(JdbcHelper.class);

	private JdbcTemplate jdbcTemplate;
	private SqlBuilder sqlbuilder;

	public JdbcHelper(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * 插入
	 *
	 * @param t 插入的对象
	 * @version 1.2
	 */
	public int save(T t) {
		sqlbuilder = new SqlBuilder(t);
		String sql = sqlbuilder.insertSql();
		Object[] prams = sqlbuilder.getPrams();
		return doUpdate(sql, prams);
	}

	/**
	 * 插入
	 *
	 * @param t 插入的对象
	 * @version 1.2
	 */
	public long saveReturnKey(T t) {
		sqlbuilder = new SqlBuilder(t);
		String sql = sqlbuilder.insertSql();
		Object[] prams = sqlbuilder.getPrams();
		return doUpdateReturnKey(sql, prams);
	}

	/**
	 * 更新
	 *
	 * @param t 更新的对象
	 * @version 1.2
	 */
	public int update(T t) {
		sqlbuilder = new SqlBuilder(t);
		String sql = sqlbuilder.updateSql();

		if (null == sql)
			return 0;

		Object[] prams = sqlbuilder.getPrams();

		return doUpdate(sql, prams);
	}

	/**
	 * 更新
	 *
	 * @param t         更新的对象
	 * @param queryKeys 更新时查询的关键字
	 * @version 1.2
	 */
	public int update(T t, Map<String, Object> queryPrams) {
		sqlbuilder = new SqlBuilder(t);
		String sql = sqlbuilder.updateSql(queryPrams);

		if (null == sql)
			return 0;

		Object[] prams = sqlbuilder.getPrams();

		return doUpdate(sql, prams);
	}

	/**
	 * 排序查询
	 *
	 * @param t
	 * @param orderByKeys 排序的Key
	 * @return
	 * @version 1.3
	 */
	public List<T> query(final T t, List<String> orderByKeys) {
		sqlbuilder = new SqlBuilder(t);
		int keysize = orderByKeys.size();
		if (null != orderByKeys && keysize > 0) {
			for (int i = 0; i < keysize; i++) {
				sqlbuilder.setOrderByKey(orderByKeys.get(i));
			}
		}
		String sql = sqlbuilder.querySql();
		Object[] prams = sqlbuilder.getPrams();

		return doQuery(t.getClass(), sql, prams);
	}

	/**
	 * 自定排序方式排序查询
	 *
	 * @param t
	 * @param orderByKeys 排序的Key
	 * @param orderBy
	 * @return
	 * @version 1.3
	 */
	public List<T> query(final T t, List<String> orderByKeys, String orderBy) {
		sqlbuilder = new SqlBuilder(t);
		int keysize = orderByKeys.size();
		if (null != orderByKeys && keysize > 0) {
			for (int i = 0; i < keysize; i++) {
				sqlbuilder.setOrderByKey(orderByKeys.get(i));
			}
		}
		if (null != orderBy && !"".equals(orderBy))
			sqlbuilder.setOrderBy(orderBy);
		String sql = sqlbuilder.querySql();
		Object[] prams = sqlbuilder.getPrams();

		return doQuery(t.getClass(), sql, prams);
	}

	/**
	 * 分页查询
	 *
	 * @param t
	 * @param orderBy
	 * @param firstResult
	 * @param maxResult
	 * @return
	 * @version 1.3
	 */
	public List<T> query(final T t, Integer firstResult, Integer maxResult) {
		sqlbuilder = new SqlBuilder(t);
		if (null != firstResult && null != maxResult) {
			sqlbuilder.setFirstResult(firstResult);
			sqlbuilder.setMaxResult(maxResult);
		}
		String sql = sqlbuilder.querySql();
		Object[] prams = sqlbuilder.getPrams();

		return doQuery(t.getClass(), sql, prams);
	}

	/**
	 * 分页、排序查询
	 *
	 * @param t
	 * @param orderByKeys 排序的Key
	 * @param orderBy
	 * @param firstResult
	 * @param maxResult
	 * @return
	 * @version 1.3
	 */
	public List<T> query(final T t, List<String> orderByKeys, String orderBy, Integer firstResult, Integer maxResult) {
		sqlbuilder = new SqlBuilder(t);
		int keysize = orderByKeys.size();
		if (null != orderByKeys && keysize > 0) {
			for (int i = 0; i < keysize; i++) {
				sqlbuilder.setOrderByKey(orderByKeys.get(i));
			}
		}
		if (null != orderBy && !"".equals(orderBy))
			sqlbuilder.setOrderBy(orderBy);
		if (null != firstResult && null != maxResult) {
			sqlbuilder.setFirstResult(firstResult);
			sqlbuilder.setMaxResult(maxResult);
		}
		String sql = sqlbuilder.querySql();
		Object[] prams = sqlbuilder.getPrams();

		return doQuery(t.getClass(), sql, prams);
	}

	/**
	 * 查询
	 *
	 * @param t
	 * @return
	 * @version 1.3
	 */
	public List<T> query(T t) {
		sqlbuilder = new SqlBuilder(t);
		String sql = sqlbuilder.querySql();
		Object[] prams = sqlbuilder.getPrams();
		return doQuery(t.getClass(), sql, prams);
	}

	/**
	 * 执行更新操作
	 *
	 * @param sql
	 * @param prams
	 * @return
	 * @version 1.2
	 */
	public int doUpdate(String sql, final Object[] prams) {
		log.debug(sql);
		return jdbcTemplate.update(sql, new PreparedStatementSetter() {
			public void setValues(PreparedStatement ps) throws SQLException {
				int pramsLength = prams.length;
				for (int i = 0; i < pramsLength; i++) {
					ps.setObject(i + 1, prams[i]);
				}
			}
		});
	}

	/**
	 * 执行更新操作返回主键
	 *
	 * @param sql
	 * @param prams
	 * @return
	 * @version 1.2
	 */
	public long doUpdateReturnKey(final String sql, final Object[] prams) {
		log.debug(sql);

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			                    @Override
			                    public PreparedStatement createPreparedStatement(Connection connection)
					                    throws SQLException {
				                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				                    int pramsLength = prams.length;
				                    for (int i = 0; i < pramsLength; i++) {
					                    ps.setObject(i + 1, prams[i]);
				                    }
				                    return ps;
			                    }
		                    },
				keyHolder);
		return keyHolder.getKey().longValue();
	}

	/**
	 * 批量添加
	 *
	 * @param addList
	 * @return
	 */
	public int[] batchSave(final List<T> addList) {
		sqlbuilder = new SqlBuilder(addList.get(0));
		String sql = sqlbuilder.insertSql();
		log.debug(sql);
		return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				sqlbuilder = new SqlBuilder(addList.get(i));
				sqlbuilder.insertSql();
				Object[] prams = sqlbuilder.getPrams();
				int pramsLength = prams.length;
				for (int j = 0; j < pramsLength; j++) {
					ps.setObject(j + 1, prams[j]);
				}
			}

			public int getBatchSize() {
				return addList.size();
			}
		});
	}

	/**
	 * 批量更新
	 *
	 * @param updateList
	 * @return
	 */
	public int[] batchUpdate(final List<T> updateList) {
		sqlbuilder = new SqlBuilder(updateList.get(0));
		String sql = sqlbuilder.updateSql();
		log.debug(sql);
		return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				sqlbuilder = new SqlBuilder(updateList.get(i));
				sqlbuilder.updateSql();
				Object[] prams = sqlbuilder.getPrams();
				int pramsLength = prams.length;
				for (int j = 0; j < pramsLength; j++) {
					ps.setObject(j + 1, prams[j]);
				}
			}

			public int getBatchSize() {
				return updateList.size();
			}
		});
	}

	/**
	 * 执行查询操作
	 *
	 * @param clazz
	 * @param sql
	 * @param prams
	 * @return
	 * @version 1.3
	 */
	@SuppressWarnings("unchecked")
	public List<T> doQuery(final T t, String sql, final Object[] prams) {
		log.debug(sql);
		return (List<T>) jdbcTemplate.query(sql, prams, new RowMapper<Object>() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Object obj = null;
				try {
					obj = t.getClass().newInstance();
					ResultSetHandler.beanHandler(rs, obj);
					return obj;
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				return obj;
			}
		});
	}

	/**
	 * 执行查询操作
	 *
	 * @param clazz
	 * @param sql
	 * @return
	 * @version 1.3
	 */
	@SuppressWarnings("unchecked")
	public List<T> doQuery(final Class<?> clazz, String sql) {
		log.debug(sql);
		return (List<T>) jdbcTemplate.query(sql, new RowMapper<Object>() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Object obj = null;
				try {
					obj = clazz.newInstance();
					ResultSetHandler.beanHandler(rs, obj);
					return obj;
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				return obj;
			}
		});
	}

	/**
	 * 执行查询操作
	 *
	 * @param clazz
	 * @param sql
	 * @param prams
	 * @return
	 * @version 1.3
	 */
	@SuppressWarnings("unchecked")
	public List<T> doQuery(final Class<?> clazz, String sql, final Object[] prams) {
		log.debug(sql);
		System.out.println("执行SQL:" + sql);
		return (List<T>) jdbcTemplate.query(sql, prams, new RowMapper<Object>() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Object obj = null;
				try {
					obj = clazz.newInstance();
					ResultSetHandler.beanHandler(rs, obj);
					return obj;
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				return obj;
			}
		});
	}

	/**
	 * 统计查询
	 *
	 * @param sql
	 * @param prams
	 * @return
	 */
	public int queryCount(String sql, final Object[] prams) {
		return jdbcTemplate.queryForObject(sql, prams, Integer.class);
	}

	/**
	 * 统计查询
	 *
	 * @param sql
	 * @param prams
	 * @return
	 */
	public int queryCount(T t) {
		sqlbuilder = new SqlBuilder(t);
		String sql = sqlbuilder.countSql();
		Object[] prams = sqlbuilder.getPrams();
		return jdbcTemplate.queryForObject(sql, prams, Integer.class);
	}
}
