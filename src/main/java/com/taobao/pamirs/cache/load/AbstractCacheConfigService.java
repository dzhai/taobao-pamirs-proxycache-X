package com.taobao.pamirs.cache.load;

import com.taobao.pamirs.cache.CacheManager;
import com.taobao.pamirs.cache.store.StoreType;

/**
 * 缓存抽象公共
 * 
 * @author poxiao.gj
 * @date 2012-11-13
 */
public abstract class AbstractCacheConfigService extends CacheManager {

	/**
	 * 缓存存储类型
	 * 
	 * @see StoreType
	 */
	private String storeType;

	/**
	 * 本地缓存清理时间
	 * 
	 * @see StoreType.MAP
	 */
	private String mapCleanTime;

	/**
	 * 缓存环境隔离
	 */
	private String storeRegion;

	public String getStoreType() {
		return storeType;
	}

	public String getMapCleanTime() {
		return mapCleanTime;
	}

	public String getStoreRegion() {
		return storeRegion;
	}

	public void setStoreType(String storeType) {
		this.storeType = storeType;
	}

	public void setMapCleanTime(String mapCleanTime) {
		this.mapCleanTime = mapCleanTime;
	}

	public void setStoreRegion(String storeRegion) {
		this.storeRegion = storeRegion;
	}

}
