package com.sqlbuilder.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sqlbuilder.annotation.DBColumn;
import com.sqlbuilder.annotation.DBId;
import com.sqlbuilder.annotation.DBTable;
import com.sqlbuilder.annotation.DBTransient;
import com.sqlbuilder.utils.FieldUtils;

/**
 * @author Mo
 */
public class SqlBuilder {
	
	public static String ORDER_BY_DESC = "DESC"; //降序
	public static String ORDER_BY_ASC = "ASC";	//升序
	
	private Object obj;
	private String orderBy;
	private Integer firstResult;
	private Integer maxResult;
	private List<Object> allPrams = new ArrayList<Object>();
	/**
	 * @version 1.2
	 */
	private List<Object> orderByKeys = new ArrayList<Object>();
	
	public SqlBuilder(Object obj){
		this.obj = obj;
	}
	public SqlBuilder(Object obj, String orderBy) {
		super();
		this.obj = obj;
		this.orderBy = orderBy;
	}
	public SqlBuilder(Object obj,Integer firstResult,Integer maxResult) {
		super();
		this.obj = obj;
		this.firstResult = firstResult;
		this.maxResult = maxResult;
	}
	public SqlBuilder(Object obj, String orderBy, Integer firstResult,
			Integer maxResult) {
		super();
		this.obj = obj;
		this.orderBy = orderBy;
		this.firstResult = firstResult;
		this.maxResult = maxResult;
	}
	/**
	 * @version 1.2
	 */
	public SqlBuilder(Object obj,List<Object> orderByKeys,String orderBy, Integer firstResult,Integer maxResult) {
		super();
		this.obj = obj;
		this.orderBy = orderBy;
		this.firstResult = firstResult;
		this.maxResult = maxResult;
		this.orderByKeys = orderByKeys;
	}
	
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}
	public void setMaxResult(Integer maxResult) {
		this.maxResult = maxResult;
	}
	/**
	 * 添加排序字段
	 * @version 1.2
	 */
	public void setOrderByKey(Object orderByKey) {
		this.orderByKeys.add(orderByKey);
	}
	
	/**
	 * 添加SQLwhere参数，顺序对应queryKeys
	 * @param pram
	 * @deprecated
	 * @version 1.1
	 */
	public void addPram(Object pram) {
		allPrams.add(pram);
	}
	
	/**
	 * 获取SQL设置？对应的值
	 * @return
	 */
	public Object[] getPrams() {
		return allPrams.toArray();
	}
	
	/**
	 * 创建插入sql
	 * @return
	 */
	public String insertSql(){
		/*
		 * 获取数据库表名
		 */
		String tableName = getTableName(obj);
		
		StringBuffer sql = new StringBuffer();
		StringBuffer dbFields = new StringBuffer();
		sql.append("INSERT INTO ")
			.append(tableName)
			.append(" (");
		/*
		 * 获取对象属性名和值
		 */
		Field[] fields = obj.getClass().getDeclaredFields();
		for(int i=0;i<fields.length;i++){
			Field field = fields[i];
			String fieldName = field.getName();
			
			String columnName = getColumnName(field);
			
			/*
			 * 获取对象属性值
			 */
			Object objValue = FieldUtils.getFieldValue(obj, fieldName);
			
			DBTransient fieldTransient = field.getAnnotation(DBTransient.class);
			
			/*
			 * 如果值为空,则不添加到插入字段列表里
			 */
			if(null!=objValue && !"".equals(objValue) && null == fieldTransient){
				dbFields.append(columnName);
				allPrams.add(objValue);
				dbFields.append(",");
			}
		}
		sql.append(dbFields.toString().substring(0, dbFields.length()-1)).append(")").append(" VALUES (");
		
		for(int i =0;i<allPrams.size();i++){
			sql.append("?");
			if(i!=allPrams.size()-1){
				sql.append(",");
			}
		}
		sql.append(")");
		
		return sql.toString();
	}
	
	/**
	 * 创建根据ID更新的SQL
	 * @version 1.2
	 * @return
	 */
	public String updateSql(){
		return updateSql(new String[]{});
	}
	
	/** 
	 * 创建更新sql
	 * @version 1.2
	 * @param queryPrams 需要添加的WHERE语句键值
	 * @return
	 */
	public String updateSql(Map<String,Object> queryPrams){
		/*
		 * 获取数据库表名
		 */
		String tableName = getTableName(obj);
		
		StringBuffer sql = new StringBuffer();
		StringBuffer dbFields = new StringBuffer();
		StringBuffer where = new StringBuffer();
		
		List<Object> wherePrams = new ArrayList<Object>();
		
		sql.append("UPDATE ")
		.append(tableName)
		.append(" SET ");
		
		where.append(" WHERE 1=1 ");
		
		Object id = null;
		
		/*
		 * 获取对象属性名和值
		 */
		Field[] fields = obj.getClass().getDeclaredFields();
		for(int i=0;i<fields.length;i++){
			Field field = fields[i];
			String fieldName = field.getName();
			/*
			 * 获取对象属性值
			 */
			Object objValue = FieldUtils.getFieldValue(obj, fieldName);
			
			String columnName = getColumnName(field);
			
			/*
			 * 判断ID是否为空,如果为空,抛出异常
			 */
			DBId dbId = field.getAnnotation(DBId.class);
			DBTransient fieldTransient = field.getAnnotation(DBTransient.class);
			
			if(null!=dbId && (null==queryPrams)){//ID字段
				if(null==objValue || "".equals(objValue) || 0 == (Long)objValue){
					try {
						throw new Exception("id不能为空");
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}
				id = objValue;
				where.append(" AND ").append(columnName).append("=?");
				wherePrams.add(id);
				
			}else{
				/*
				 * 如果值为空,则不添加到插入字段列表里
				 */
				if(null!=objValue && !"".equals(objValue) && null ==fieldTransient){
					dbFields.append(columnName).append("=?");
					allPrams.add(objValue);
					dbFields.append(",");
				}
				/*
				 * 获取到对应的数据库字段名,并添加到WHERE语句
				 */
				if(null!=queryPrams){
					Set<String> keySet = queryPrams.keySet();
					Iterator<String> iterator = keySet.iterator();
					while(iterator.hasNext()){
						String key = iterator.next();
						if(key.equals(fieldName)){
							where.append(" AND ").append(columnName).append("=?");
							wherePrams.add(queryPrams.get(key));
							break;
						}
					}
				}
			}
			
		}
		allPrams.addAll(wherePrams);
		
		sql.append(dbFields.toString().substring(0, dbFields.length()-1));
		sql.append(where.toString());
		
		return sql.toString();
	}
	
	/**
	 * 创建更新sql
	 * @version 1.1
	 * @deprecated
	 * @param queryKeys 需要添加的WHERE语句字段名,需要另外调用 addPram(queryValue)添加对应的字段值
	 * @return
	 */
	public String updateSql(String...queryKeys){
		/*
		 * 获取数据库表名
		 */
		String tableName = getTableName(obj);
		
		StringBuffer sql = new StringBuffer();
		StringBuffer dbFields = new StringBuffer();
		StringBuffer where = new StringBuffer();
		sql.append("UPDATE ")
			.append(tableName)
			.append(" SET ");
		
		where.append(" WHERE 1=1 ");
		
		Object id = null;
		
		/*
		 * 获取对象属性名和值
		 */
		Field[] fields = obj.getClass().getDeclaredFields();
		for(int i=0;i<fields.length;i++){
			Field field = fields[i];
			String fieldName = field.getName();
			/*
			 * 获取对象属性值
			 */
			Object objValue = FieldUtils.getFieldValue(obj, fieldName);
			
			String columnName = getColumnName(field);
			
			/*
			 * 判断ID是否为空,如果为空,抛出异常
			 */
			DBId dbId = field.getAnnotation(DBId.class);
			DBTransient fieldTransient = field.getAnnotation(DBTransient.class);
			
			if(null!=dbId && null!=objValue && "0".equals(objValue.toString()) )
				objValue = null;
			
			if(null!=dbId && (null==queryKeys || queryKeys.length<=0)){//ID字段
				if(null==objValue || "".equals(objValue) || "0".equals(objValue.toString())){
					try {
						throw new Exception("id不能为空");
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}
				id = objValue;
				where.append(" AND ").append(columnName).append("=?");
			}else{
				/*
				 * 如果值为空,则不添加到插入字段列表里
				 */
				if(null!=objValue && !"".equals(objValue) && null ==fieldTransient){
					dbFields.append(columnName).append("=?");
					allPrams.add(objValue);
					dbFields.append(",");
				}
				/*
				 * 获取到对应的数据库字段名,并添加到WHERE语句
				 */
				for(String key:queryKeys){
					if(key.equals(fieldName)){
						where.append(" AND ").append(columnName).append("=?");
					}
				}
			}
			
		}
		
		if(null != id && Long.parseLong(id+"") != 0){
			allPrams.add(id);
		}
		
		sql.append(dbFields.toString().substring(0, dbFields.length()-1));
		sql.append(where.toString());
		
		return sql.toString();
	}
	
	/**
	 * 创建查询sql
	 * 1.如没有设置排序方式，则默认使用倒序（DESC
	 * 2.默认排序为：
	 * 		如果存在（设置了DBId）ID字段，根据ID倒序（DESC），否则不排序;
	 * @param queryKeys  需要添加的WHERE语句字段名
	 * @return
	 */
	public String querySql(){
		String tableName = getTableName(obj);
		
		StringBuffer sql = new StringBuffer();
		StringBuffer dbFields = new StringBuffer();
		StringBuffer where = new StringBuffer();
		
		int orderByKeySize = orderByKeys.size();
		List<Object> tempOrderByKeys = new ArrayList<Object>();
		
		sql.append("SELECT ");
		where.append(" WHERE 1=1");
		/*
		 * 获取对象属性名和值
		 */
		Field[] fields = obj.getClass().getDeclaredFields();
		for(int i=0;i<fields.length;i++){
			Field field = fields[i];
			String fieldName = field.getName();
			/*
			 * 获取对象属性值
			 */
			Object objValue = FieldUtils.getFieldValue(obj, fieldName);
			
			String columnName = getColumnName(field);
			
			
			DBId dbId = field.getAnnotation(DBId.class);
			DBTransient fieldTransient = field.getAnnotation(DBTransient.class);
			if(null == fieldTransient){
				dbFields.append(columnName)
				.append(" AS ")
				.append(fieldName);
				dbFields.append(",");
			}
			
			if(0>=orderByKeySize && null!=dbId){ //默认ID排序
				orderByKeys.add(columnName);
			}else{
				if(orderByKeys.contains(fieldName)){
					orderByKeys.remove(fieldName);
					tempOrderByKeys.add(columnName);
				}
			}
			
			if(null!=dbId && null!=objValue && "0".equals(objValue.toString()) )
				objValue = null;
			
			if(null!=objValue && !"".equals(objValue) && null == fieldTransient){
				where.append(" AND ").append(columnName).append("=?");
				allPrams.add(objValue);
			}
		}
		sql.append(dbFields.toString().substring(0, dbFields.length()-1));
		sql.append(" FROM ").append(tableName).append(where.toString());
		
		orderByKeySize = tempOrderByKeys.size();
		if(0<orderByKeySize){
			if(null==orderBy)
				orderBy = ORDER_BY_DESC;
			StringBuilder orderByKeyStr = new StringBuilder();
			for(int i=0;i<orderByKeySize;i++){
				orderByKeyStr.append(tempOrderByKeys.get(i)).append(" ").append(orderBy).append(",");
			}
			sql.append(" ORDER BY ").append(orderByKeyStr.substring(0, orderByKeyStr.toString().length()-1));
		}
		if(null != firstResult && null!=maxResult){
			sql.append(" LIMIT ").append(firstResult).append(",").append(maxResult);
		}
		
		return sql.toString();
	}
	
	/**
	 * 获取数据库表名
	 * @param obj
	 * @return
	 */
	private  String getTableName(Object obj){
		DBTable dbTable = obj.getClass().getAnnotation(DBTable.class);
		return null == dbTable? obj.getClass().getSimpleName() :dbTable.name();
	}
	
	/**
	 * 获取对象在数据库中对应的字段名
	 * @param field
	 * @return
	 */
	private String getColumnName(Field field){
		String fieldName = field.getName();
		DBColumn dbColumn = field.getAnnotation(DBColumn.class);
		return null == dbColumn? fieldName:dbColumn.name();
	}
}
