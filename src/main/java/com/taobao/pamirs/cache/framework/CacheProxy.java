package com.taobao.pamirs.cache.framework;

import static com.taobao.pamirs.cache.framework.listener.CacheOprator.GET;
import static com.taobao.pamirs.cache.framework.listener.CacheOprator.PUT;
import static com.taobao.pamirs.cache.framework.listener.CacheOprator.PUT_EXPIRE;
import static com.taobao.pamirs.cache.framework.listener.CacheOprator.REMOVE;

import java.io.Serializable;

import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.framework.listener.CacheObservable;
import com.taobao.pamirs.cache.framework.listener.CacheOprateInfo;
import com.taobao.pamirs.cache.store.StoreType;

/**
 * 缓存处理适配器
 * 
 * @author xiaocheng 2012-10-31
 */
public class CacheProxy<K extends Serializable, V extends Serializable> extends
		CacheObservable {

	private StoreType storeType;
	private String storeRegion;
	private String key;

	/** 注入真正的cache实现 */
	private ICache<K, V> cache;

	private String beanName;
	private MethodConfig methodConfig;

	public CacheProxy(StoreType storeType, String storeRegion, String key,
			ICache<K, V> cache, String beanName, MethodConfig methodConfig) {
		this.storeType = storeType;
		this.storeRegion = storeRegion;
		this.key = key;
		this.cache = cache;
		this.beanName = beanName;
		this.methodConfig = methodConfig;
	}

	public V get(K key, String ip) {
		if (!isUseCache)
			return null;

		CacheException cacheException = null;
		V v = null;

		long start = System.currentTimeMillis();
		try {
			v = cache.get(key);
		} catch (CacheException e) {
			cacheException = e;
		}

		long end = System.currentTimeMillis();

		if (v != null) // 命中，通知listener
			notifyListeners(GET, new CacheOprateInfo(key, end - start, true,
					beanName, methodConfig, cacheException, ip));

		return v;
	}

	public void put(K key, V value, String ip) {

		CacheException cacheException = null;

		long start = System.currentTimeMillis();
		try {
			cache.put(key, value);
		} catch (CacheException e) {
			cacheException = e;
		}

		long end = System.currentTimeMillis();

		// listener
		notifyListeners(PUT, new CacheOprateInfo(key, end - start, true,
				beanName, methodConfig, cacheException, ip));
	}

	public void put(K key, V value, int expireTime, String ip) {
		CacheException cacheException = null;

		long start = System.currentTimeMillis();
		try {
			cache.put(key, value, expireTime);
		} catch (CacheException e) {
			cacheException = e;
		}
		long end = System.currentTimeMillis();

		// listener
		notifyListeners(PUT_EXPIRE, new CacheOprateInfo(key, end - start, true,
				beanName, methodConfig, cacheException, ip));
	}

	public void remove(K key, String ip) {
		CacheException cacheException = null;

		long start = System.currentTimeMillis();
		try {
			cache.remove(key);
		} catch (CacheException e) {
			cacheException = e;
		}
		long end = System.currentTimeMillis();

		// listener
		notifyListeners(REMOVE, new CacheOprateInfo(key, end - start, true,
				beanName, methodConfig, cacheException, ip));
	}

	public void clear() {
		cache.clear();
	}

	public int size() {
		return cache.size();
	}

	public void invalidBefore() {
		cache.invalidBefore();
	}

	/** 单个方法的缓存开关 */
	private boolean isUseCache = true;

	public boolean isUseCache() {
		return isUseCache;
	}

	public void setUseCache(boolean isUseCache) {
		this.isUseCache = isUseCache;
	}

	public StoreType getStoreType() {
		return storeType;
	}

	public String getStoreRegion() {
		return storeRegion;
	}

	public String getKey() {
		return key;
	}

	public String getBeanName() {
		return beanName;
	}

	public MethodConfig getMethodConfig() {
		return methodConfig;
	}

}
