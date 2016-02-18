package com.taobao.pamirs.cache.framework.config;

import com.taobao.pamirs.cache.load.verify.Verfication;

public class CleanBean {

	/**
	 * 删除前缀，与 cacheBeans中前缀对应
	 */
	@Verfication(name = "删除前缀", notEmpty = true)
	private String prefix;

	/**
	 * 使用的缓存，与 cacheBeans中使用缓存对应
	 */
	private String cache;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getCache() {
		return cache;
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

}
