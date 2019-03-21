package com.x.sqlbuilder.handler;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * o-r mapping映射框架
 * @author Mo
 * <pre>表中属性要和javabean属性保持一致(大小写)，否则映射不过去报java.lang.NoSuchFieldException</pre>
 */
public class ResultSetHandler{

	/**
	 * @version1.4
	 * @param rs
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> mapHandler(ResultSet rs) {
		ResultSetMetaData meta;
		try {
			meta = rs.getMetaData();
			//获取字段数量
			int count = meta.getColumnCount();
			Map<String, Object> obj = new HashMap<String, Object>();
			for(int i=1;i<count+1;i++){
				//获取字段名称和值
				String key = meta.getColumnLabel(i);
				Object value = rs.getObject(i);
				obj.put(key, value);
			}
			return obj;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @version1.3
	 * @param rs
	 * @param obj
	 * @return
	 */
	public static Object beanHandler(ResultSet rs,Object obj) {
		try {
			ResultSetMetaData meta = rs.getMetaData();
			//获取字段数量
			int count = meta.getColumnCount();
			for(int i=1;i<count+1;i++){
				//获取字段名称和值
				String key = meta.getColumnLabel(i);
				Object value = rs.getObject(i);
				Field field = obj.getClass().getDeclaredField(key);
				field.setAccessible(true);
				field.set(obj, value);
			}
			return obj;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Object beanHandler(ResultSet rs,Class<?> clazz) {
		try {
			Object obj = clazz.newInstance();
			
			ResultSetMetaData meta = rs.getMetaData();
			//获取字段数量
			int count = meta.getColumnCount();
			for(int i=1;i<count+1;i++){
				//获取字段名称和值
				String key = meta.getColumnLabel(i);
				Object value = rs.getObject(i);
				Field field = obj.getClass().getDeclaredField(key);
				field.setAccessible(true);
				field.set(obj, value);
			}
			return obj;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public static List<Object> listBeanHandler(ResultSet rs, Class<?> clazz) {
		try {
			if(null == rs)
				return null;
			ResultSetMetaData meta = rs.getMetaData();
			int count = meta.getColumnCount();
			List<Object> list = new ArrayList<Object>();
			while(rs.next()){
				Object obj = clazz.newInstance();
				for(int i=1;i<count+1;i++){
					String key = meta.getColumnName(i);
					Object value = rs.getObject(i);
					Field field = clazz.getDeclaredField(key);
					field.setAccessible(true);
					field.set(obj, value);
				}
				list.add(obj);
			}
			return list;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
