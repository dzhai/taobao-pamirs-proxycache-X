package com.taobao.pamirs.cache.framework.aop.advisor;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;

import com.taobao.pamirs.cache.CacheManager;
import com.taobao.pamirs.cache.framework.aop.advice.CacheManagerRoundAdvice;

/**
 * 观察者
 * 
 * @author xuannan
 * @author xiaocheng 2012-10-30
 */
public class CacheManagerAdvisor implements Advisor {
	
	private CacheManagerRoundAdvice advice;

	public CacheManagerAdvisor(CacheManager cacheManager, String beanName) {
		this.advice = new CacheManagerRoundAdvice(cacheManager, beanName);
	}

	public Advice getAdvice() {
		return advice;
	}

	public boolean isPerInstance() {
		return false;
	}
}
