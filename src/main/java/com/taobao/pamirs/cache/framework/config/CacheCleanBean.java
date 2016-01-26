package com.taobao.pamirs.cache.framework.config;

import java.io.Serializable;
import java.util.List;

import com.taobao.pamirs.cache.load.verify.Verfication;

/**
 * 缓存清理bean配置
 * 
 * @author xiaocheng 2012-11-2
 */
public class CacheCleanBean implements Serializable {

	//
	private static final long serialVersionUID = -4582877908557906265L;

	@Verfication(name = "CacheCleanBean名称", notEmpty = true)
	private String beanName;

	/**
	 * 需要清理的原生方法列表
	 */
	@Verfication(name = "需要清理的原生方法列表", notEmptyList = true)
	private List<CacheCleanMethod> methods;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public List<CacheCleanMethod> getMethods() {
		return methods;
	}

	public void setMethods(List<CacheCleanMethod> methods) {
		this.methods = methods;
	}

}
