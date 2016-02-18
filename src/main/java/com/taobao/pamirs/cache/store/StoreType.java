package com.taobao.pamirs.cache.store;

/**
 * 缓存存储类型
 * 
 * @author xiaocheng 2012-11-1
 */
public enum StoreType {

	MAP("map"), REDIS("redis");

	private String name;

	private StoreType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static StoreType toEnum(String name) {
		if (MAP.getName().equals(name))
			return MAP;

		if (REDIS.getName().equals(name))
			return REDIS;

		return null;
	}

}
