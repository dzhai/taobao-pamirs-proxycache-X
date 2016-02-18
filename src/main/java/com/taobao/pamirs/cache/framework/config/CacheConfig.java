package com.taobao.pamirs.cache.framework.config;

import com.taobao.pamirs.cache.load.verify.Verfication;
import com.taobao.pamirs.cache.store.StoreType;

/**
 * 缓存总配置
 * 
 * @author xiaocheng 2012-11-2
 */
public class CacheConfig extends CacheModule {

	//
	private static final long serialVersionUID = 8164876688008497503L;

	/**
	 * 缓存类型
	 * 
	 * @see StoreType
	 */
	@Verfication(name = "缓存类型", notEmpty = true, isStoreType = true)
	private String storeType;

	/**
	 * Map自动清理表达式（可选）(just for map）
	 * 
	 * @see StoreType.MAP
	 */
	private String storeMapCleanTime;

	/**
	 * 缓存分区（可选）
	 */
	private String storeRegion;

	public String getStoreType() {
		return storeType;
	}

	public void setStoreType(String storeType) {
		this.storeType = storeType;
	}

	public String getStoreMapCleanTime() {
		return storeMapCleanTime;
	}

	public void setStoreMapCleanTime(String storeMapCleanTime) {
		this.storeMapCleanTime = storeMapCleanTime;
	}

	public String getStoreRegion() {
		return storeRegion;
	}

	public void setStoreRegion(String storeRegion) {
		this.storeRegion = storeRegion;
	}


}
