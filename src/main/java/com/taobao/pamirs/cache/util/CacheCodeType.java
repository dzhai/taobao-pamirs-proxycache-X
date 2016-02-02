package com.taobao.pamirs.cache.util;

/**
 * Cache Code 生成规则
 */
public enum CacheCodeType {

	DEFAULT_TYPE("default"),PREFIX_VALUE_TYPE("prefix_value");

	private String name;

	private CacheCodeType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static CacheCodeType toEnum(String name) {
		if (PREFIX_VALUE_TYPE.getName().equals(name)){
			return PREFIX_VALUE_TYPE;			
		}
		return null;
	}
}
