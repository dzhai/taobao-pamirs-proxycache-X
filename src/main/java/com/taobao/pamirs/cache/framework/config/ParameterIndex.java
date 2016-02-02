package com.taobao.pamirs.cache.framework.config;

public class ParameterIndex {

	/**
	 * 
	 * 配置的位置
	 */
	private Integer index;
	/**
	 * 
	 * 如果参数是object 需要配置属性的名字
	 */
	private String name;

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
