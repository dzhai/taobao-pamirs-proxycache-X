package com.taobao.pamirs.cache.util;

/**
 * Cache Code 生成规则
 * 
 * 1.region@prefix#cache 2.bean 3.method 4.parameter 5.value
 * 
 */
public enum CacheCodeType {

	/**
	 * region@prefix#cache#beanName#methodName#abc@@123
	 */
	DEFAULT_TYPE("default"),
	
	/**
	 * region@prefix#cache#beanName#methodName#{string|long}abc@@123
	 */
	ALL_TYPE("all"),
	
	/**
	 * region@prefix#cache#beanName#abc@@123
	 */
	SHORT_TYPE("short");

	private String name;

	private CacheCodeType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static CacheCodeType toEnum(String name) {
		if (DEFAULT_TYPE.getName().equals(name)) {
			return DEFAULT_TYPE;
		}
		if (ALL_TYPE.getName().equals(name)) {
			return ALL_TYPE;
		}
		if (SHORT_TYPE.getName().equals(name)) {
			return SHORT_TYPE;
		}
		return null;
	}
}
