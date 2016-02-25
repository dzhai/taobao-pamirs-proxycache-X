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
	 * 缓存空间
	 */
	private Integer nameSpace;

	/**
	 * 缓存环境隔离
	 */
	private String storeRegion;
	
	/**
	 * 失效时间，单位：秒 （可选）
	 */
	private Integer expiredTime;

	public String getStoreType() {
		return storeType;
	}

	public String getStoreRegion() {
		return storeRegion;
	}

	public void setStoreType(String storeType) {
		this.storeType = storeType;
	}

	public void setStoreRegion(String storeRegion) {
		this.storeRegion = storeRegion;
	}

	public Integer getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(Integer nameSpace) {
		this.nameSpace = nameSpace;
	}

	public Integer getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(Integer expiredTime) {
		this.expiredTime = expiredTime;
	}

}
