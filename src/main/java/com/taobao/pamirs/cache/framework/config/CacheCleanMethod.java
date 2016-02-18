package com.taobao.pamirs.cache.framework.config;

import java.util.List;

import com.taobao.pamirs.cache.load.verify.Verfication;

/**
 * 缓存清理的方法，内部包含关联的clean methods
 * 
 * @author xiaocheng 2012-11-12
 */
public class CacheCleanMethod extends MethodConfig {

	//
	private static final long serialVersionUID = 2983763433924222529L;

	/**
	 * 需要关联remove缓存的方法列表
	 */
	@Verfication(name = "需要关联remove缓存的方法列表", notEmptyList = false)
	@Deprecated
	private List<MethodConfig> cleanMethods;
	
	@Verfication(name = "需要remove缓存的列表", notEmptyList = true)
	private List<MethodConfig> cleanBeans;
	
	@Deprecated
	public List<MethodConfig> getCleanMethods() {
		return cleanMethods;
	}
	
	@Deprecated
	public void setCleanMethods(List<MethodConfig> cleanMethods) {
		this.cleanMethods = cleanMethods;
	}

	public List<MethodConfig> getCleanBeans() {
		return cleanBeans;
	}

	public void setCleanBeans(List<MethodConfig> cleanBeans) {
		this.cleanBeans = cleanBeans;
	}

}
