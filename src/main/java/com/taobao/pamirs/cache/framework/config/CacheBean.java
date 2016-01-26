package com.taobao.pamirs.cache.framework.config;

import java.io.Serializable;
import java.util.List;

import com.taobao.pamirs.cache.load.verify.Verfication;

/**
 * 缓存bean配置
 * 
 * @author xiaocheng 2012-11-2
 */
public class CacheBean implements Serializable {

	//
	private static final long serialVersionUID = 4973185401294689002L;

	@Verfication(name = "CacheBean名称", notEmpty = true)
	private String beanName;

	/**
	 * 缓存的方法列表
	 */
	@Verfication(name = "缓存的方法列表", notEmptyList = true)
	private List<MethodConfig> cacheMethods;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public List<MethodConfig> getCacheMethods() {
		return cacheMethods;
	}

	public void setCacheMethods(List<MethodConfig> cacheMethods) {
		this.cacheMethods = cacheMethods;
	}

}
