package com.taobao.pamirs.cache.framework.listener;

/**
 * 缓存操作监听器
 * 
 * @author xiaocheng 2012-10-31
 */
public interface CacheOprateListener {

	/**
	 * 操作通知
	 * 
	 * @param oprator
	 * @param cacheInfo
	 */
	void oprate(CacheOprator oprator, CacheOprateInfo cacheInfo);

}
