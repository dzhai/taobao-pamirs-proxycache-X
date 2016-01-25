package com.taobao.pamirs.cache.framework.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ª∫¥Êº”‘ÿƒ£øÈ--∏¯load”√
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
	 * ª∫¥Êbean≈‰÷√
	 */
	private List<CacheBean> cacheBeans;

	/**
	 * «Â¿Ìª∫¥Êbean≈‰÷√
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
