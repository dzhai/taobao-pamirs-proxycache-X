package com.taobao.pamirs.cache.framework.aop.handle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.BeansException;
import org.springframework.core.PriorityOrdered;

import com.taobao.pamirs.cache.CacheManager;
import com.taobao.pamirs.cache.framework.aop.advisor.CacheManagerAdvisor;
import com.taobao.pamirs.cache.util.ConfigUtil;

/**
 * 缓存管理处理类
 * 
 * @author xuannan
 */
@SuppressWarnings("serial")
public class CacheManagerHandle extends AbstractAutoProxyCreator implements
		PriorityOrdered {

	private static final Log log = LogFactory.getLog(CacheManagerHandle.class);

	private CacheManager cacheManager;

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public CacheManagerHandle() {
		this.setOrder(HIGHEST_PRECEDENCE);
		this.setProxyTargetClass(true);
		this.setExposeProxy(true);// do call another advised method on itself
	}

	@SuppressWarnings("rawtypes")
	protected Object[] getAdvicesAndAdvisorsForBean(Class beanClass,
			String beanName, TargetSource targetSource) throws BeansException {

		log.debug("CacheManagerHandle in:" + beanName);

		if (ConfigUtil.isBeanHaveCache(cacheManager.getCacheConfig(), beanName)) {

			log.warn("CacheManager start... ProxyBean:" + beanName);

			return new CacheManagerAdvisor[] { new CacheManagerAdvisor(
					cacheManager, beanName) };
		}

		return DO_NOT_PROXY;
	}

}
