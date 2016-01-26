package com.taobao.pamirs.cache.store;

/**
 * ª∫¥Ê¥Ê¥¢¿‡–Õ
 * 
 * @author xiaocheng 2012-11-1
 */
public enum StoreType {

	MAP("map"), TAIR("tair"),REDIS("redis");

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

		if (TAIR.getName().equals(name))
			return TAIR;

		return null;
	}

}
