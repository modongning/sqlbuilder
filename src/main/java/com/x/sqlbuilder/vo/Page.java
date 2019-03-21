package com.x.sqlbuilder.vo;

public class Page<T> {
	private int pageSize;
	private int pageNumber;
	private int total;
	private T queryObj;
	private Object data;
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public T getQueryObj() {
		return queryObj;
	}
	public void setQueryObj(T queryObj) {
		this.queryObj = queryObj;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}
