package com.taobao.pamirs.cache.store.threadcache;

import static com.taobao.pamirs.cache.util.CacheCodeUtil.CODE_PARAM_VALUES_SPLITE_SIGN;
import static com.taobao.pamirs.cache.util.CacheCodeUtil.KEY_SPLITE_SIGN;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.PriorityOrdered;

import com.taobao.pamirs.cache.extend.jmx.annotation.JmxClass;
import com.taobao.pamirs.cache.extend.jmx.annotation.JmxMethod;
import com.taobao.pamirs.cache.util.ParameterSupportTypeUtil;
import com.taobao.pamirs.cache.util.lru.ConcurrentLRUCacheMap;

/**
 * 线程缓存
 * 
 * @author xiaocheng 2012-9-3
 */
@JmxClass
public class ThreadCacheHandle extends AbstractAutoProxyCreator implements
		BeanFactoryAware, PriorityOrdered {

	private static final Log log = LogFactory.getLog(ThreadCacheHandle.class);

	/**  */
	private static final long serialVersionUID = 1L;

	/** 代理开关，默认bean注册即开启 */
	private boolean openThreadCache = true;

	/** 命中日志开关 */
	private boolean printHitLog = false;

	/** 打印详细日志，命中方法返回结果对象等 */
	private boolean printLogDetail = false;
	
	/** 记录方法命中次数 */
	private ConcurrentLRUCacheMap<String, AtomicLong> logDetailInfo;

	/**
	 * 白名单--注解的另一种选择方式
	 */
	private Map<String, List<String>> beansMap;

	public ThreadCacheHandle() {
		super.setOrder(LOWEST_PRECEDENCE);
		this.setExposeProxy(true);// do call another advised method on itself
	}

	@Override
	protected Object[] getAdvicesAndAdvisorsForBean(
			@SuppressWarnings("rawtypes") Class beanClass, String beanName,
			TargetSource customTargetSource) throws BeansException {
		if (this.beansMap != null && this.beansMap.keySet().contains(beanName)) {

			if (log.isWarnEnabled()) {
				log.warn("本地线程cache代理：" + beanClass + ":" + beanName);
			}

			if (targetBeanIsFinal(beanClass)) {// must implements a interface
				this.setProxyTargetClass(false);// JDK
			} else {
				this.setProxyTargetClass(true);// CGLIB
			}

			return new ThreadMethodAdvisor[] { new ThreadMethodAdvisor(
					beanClass, beanName, this) };
		}
		return DO_NOT_PROXY;
	}

	private boolean targetBeanIsFinal(Class<?> clazz) {
		String inMods = Modifier.toString(clazz.getModifiers());
		if (inMods.contains("final")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isOpenThreadCache() {
		return openThreadCache;
	}

	@JmxMethod
	public void setOpenThreadCache(boolean openThreadCache) {
		this.openThreadCache = openThreadCache;
	}

	public boolean isPrintHitLog() {
		return printHitLog;
	}

	@JmxMethod
	public void setPrintHitLog(boolean printHitLog) {
		this.printHitLog = printHitLog;
	}

	public boolean isPrintLogDetail() {
		return printLogDetail;
	}

	@JmxMethod
	public void setPrintLogDetail(boolean printLogDetail) {
		this.printLogDetail = printLogDetail;
	}
	
	public ConcurrentLRUCacheMap<String, AtomicLong> getLogDetailInfo() {
		if (logDetailInfo == null)
			logDetailInfo = new ConcurrentLRUCacheMap<String, AtomicLong>();
		
		return logDetailInfo;
	}

	public Map<String, List<String>> getBeansMap() {
		return beansMap;
	}

	public void setBeansMap(Map<String, String> beansMapString) {
		if (beansMapString == null)
			this.beansMap = null;

		Map<String, List<String>> map = new HashMap<String, List<String>>();

		Iterator<String> it = beansMapString.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			map.put(key, Arrays.asList(beansMapString.get(key).split(",")));
		}

		this.beansMap = map;
	}

}

class ThreadMethodAdvisor implements Advisor {

	ThreadMethodRoundAdvice advice;

	public ThreadMethodAdvisor(Class<?> aBeanClass, String beanName,
			ThreadCacheHandle handle) {
		advice = new ThreadMethodRoundAdvice(aBeanClass, beanName, handle);
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

class ThreadMethodRoundAdvice implements Advice, MethodInterceptor {

	private static final Log log = LogFactory.getLog(ThreadCacheHandle.class);

	@SuppressWarnings("unused")
	private Class<?> beanClass;
	private ThreadCacheHandle handle;
	private String beanName;

	public ThreadMethodRoundAdvice(Class<?> aBeanClass, String beanName,
			ThreadCacheHandle handle) {
		this.beanClass = aBeanClass;
		this.beanName = beanName;
		this.handle = handle;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {

		String methodName = invocation.getMethod().getName();
		List<String> mothods = handle.getBeansMap().get(beanName);

		if (!handle.isOpenThreadCache() || mothods == null
				|| !mothods.contains(methodName)) {
			// do nothing
			return invocation.proceed();
		}

		String key = null;
		try {
			key = getCacheKey(methodName, invocation);
		} catch (IllegalArgumentException e) {
			log.warn("有不支持的参数类型，无法生成缓存key，将会放弃缓存！" + beanName + ":"
					+ methodName);
			return invocation.proceed();
		}

		Object value = ThreadContext.get(key);
		// 1. cache 有值，直接返回
		if (value != null) {
			doPrintLog(key, value);
			return value;
		}

		// 2. 没有缓存，走原有逻辑，完成后注入cache
		value = invocation.proceed();
		ThreadContext.put(key, value);

		return value;
	}

	/**
	 * 生成缓存key策略
	 * 
	 * @param methodName
	 * @param invocation
	 * @return
	 */
	private String getCacheKey(String methodName, MethodInvocation invocation) {
		StringBuilder args = new StringBuilder();

		Object[] paramObjects = invocation.getArguments();
		Class<?>[] paramTypes = invocation.getMethod().getParameterTypes();

		if (paramObjects != null) {
			for (int i = 0; i < paramObjects.length; i++) {
				if (!ParameterSupportTypeUtil
						.isSupportParameterTypes(paramTypes[i]))
					throw new IllegalArgumentException();

				if (args.toString().length() != 0)
					args.append(CODE_PARAM_VALUES_SPLITE_SIGN);

				args.append(paramObjects[i]);
			}
		}
		
		StringBuilder key = new StringBuilder();
		key.append(beanName).append(KEY_SPLITE_SIGN);
		key.append(methodName).append("{..}");
		key.append(args.toString());

		return key.toString();
	}

	private void doPrintLog(String key, Object value) {
		if (handle.isPrintHitLog()) {
			StringBuilder logInfo = new StringBuilder("线程缓存命中!");
			logInfo.append("key=").append(key);

			// for debug
			if (handle.isPrintLogDetail()) {
				logInfo.append("，value=").append(value);
				
				ConcurrentLRUCacheMap<String, AtomicLong> logDetailInfo = handle.getLogDetailInfo();
				AtomicLong atomicLong = logDetailInfo.get(key);
				if (atomicLong == null) {
					atomicLong = new AtomicLong(0);
				}
				
				atomicLong.incrementAndGet();
				logDetailInfo.put(key, atomicLong);
			}
			
			log.warn(logInfo.toString());
		}
	}
}
