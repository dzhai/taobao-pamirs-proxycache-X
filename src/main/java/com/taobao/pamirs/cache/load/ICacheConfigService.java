package com.taobao.pamirs.cache.load;

import com.taobao.pamirs.cache.framework.config.CacheConfig;

/**
 * 缓存配置获取接口
 * 
 * @author xiaocheng 2012-11-12
 */
public interface ICacheConfigService {

	/**
	 * 加载缓存配置
	 * 
	 * @return
	 */
	CacheConfig loadConfig();

	/**
	 * 自动修正默认配置
	 * 
	 * @param cacheConfig
	 */
	void autoFillCacheConfig(CacheConfig cacheConfig);

	/**
	 * 校验配置合法性
	 * 
	 * @param cacheConfig
	 */
	void verifyCacheConfig(CacheConfig cacheConfig);
}
