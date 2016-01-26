package com.taobao.pamirs.cache.framework.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 缓存加载模块--给load用
 * 
 * @author poxiao.gj
 * @author xiaocheng 2012-11-19
 */
public class CacheModule implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1194734355755304229L;

	/**
	 * 缓存bean配置
	 */
	private List<CacheBean> cacheBeans;

	/**
	 * 清理缓存bean配置
	 */
	private List<CacheCleanBean> cacheCleanBeans;

	public List<CacheBean> getCacheBeans() {
		if (cacheBeans == null)
			cacheBeans = new ArrayList<CacheBean>();

		return cacheBeans;
	}

	public void setCacheBeans(List<CacheBean> cacheBeans) {
		this.cacheBeans = cacheBeans;
	}

	public List<CacheCleanBean> getCacheCleanBeans() {
		if (cacheCleanBeans == null)
			cacheCleanBeans = new ArrayList<CacheCleanBean>();

		return cacheCleanBeans;
	}

	public void setCacheCleanBeans(List<CacheCleanBean> cacheCleanBeans) {
		this.cacheCleanBeans = cacheCleanBeans;
	}

}
