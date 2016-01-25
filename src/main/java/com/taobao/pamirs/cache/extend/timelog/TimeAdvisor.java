package com.taobao.pamirs.cache.extend.timelog;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;

/**
 * advisor
 * 
 * @author xiaocheng Nov 12, 2015
 */
public class TimeAdvisor implements Advisor {

	TimeRoundAdvice advice;

	public TimeAdvisor(Class<?> aBeanClass, String beanName,
			TimeHandle timeHandle) {
		advice = new TimeRoundAdvice(aBeanClass, beanName, timeHandle);
	}

	@Override
	public Advice getAdvice() {
		return advice;
	}

	@Override
	public boolean isPerInstance() {
		return false;
	}

}
