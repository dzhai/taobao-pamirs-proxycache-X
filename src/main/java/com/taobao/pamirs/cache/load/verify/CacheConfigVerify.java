package com.taobao.pamirs.cache.load.verify;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.taobao.pamirs.cache.framework.config.CacheBean;
import com.taobao.pamirs.cache.framework.config.CacheCleanBean;
import com.taobao.pamirs.cache.framework.config.CacheCleanMethod;
import com.taobao.pamirs.cache.framework.config.CacheConfig;
import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.load.LoadConfigException;
import com.taobao.pamirs.cache.util.CacheCodeUtil;

/**
 * 缓存配置合法性校验
 * 
 * <pre>
 * 	  缓存校验内容：
 * 		1：缓存关键配置静态校验, @see {@link Verfication}
 * 		2：缓存方法是否存在重复配置校验
 * 		3：缓存清理方法是否存在重复配置校验
 * 		4：缓存清理方法的关联方法是否存在重复配置校验
 * 		5：缓存方法配置在Spring中的需要存在，并且合法
 * 		6：缓存清理方法在Spring中需要存在，并且合法
 * </pre>
 * 
 * @author poxiao.gj
 * @author xiaocheng 2012-11-29
 */
public class CacheConfigVerify {

	/**
	 * 校验缓存配置
	 * 
	 * @param applicationContext
	 * @param cacheConfig
	 * @throws LoadConfigException
	 */
	public static void checkCacheConfig(CacheConfig cacheConfig,
			ApplicationContext applicationContext) throws LoadConfigException {
		Assert.notNull(applicationContext);
		Assert.notNull(cacheConfig);
		Assert.isTrue(!CollectionUtils.isEmpty(cacheConfig.getCacheBeans())
				|| !CollectionUtils.isEmpty(cacheConfig.getCacheBeans()),
				"配置中缓存和清理缓存不能同时为空！");

		// 1. 静态校验
		try {
			StaticCheck.check(cacheConfig);

			if (cacheConfig.getCacheBeans() != null) {
				for (CacheBean bean : cacheConfig.getCacheBeans()) {
					StaticCheck.check(bean);

					if (bean.getCacheMethods() != null) {
						for (MethodConfig method : bean.getCacheMethods())
							StaticCheck.check(method);
					}
				}
			}

			if (cacheConfig.getCacheCleanBeans() != null) {
				for (CacheCleanBean bean : cacheConfig.getCacheCleanBeans()) {
					StaticCheck.check(bean);

					if (bean.getMethods() != null) {
						for (CacheCleanMethod method : bean.getMethods()) {
							StaticCheck.check(method);

							for (MethodConfig subMethod : method
									.getCleanMethods()) {
								StaticCheck.check(subMethod);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new LoadConfigException(e.getMessage());
		}

		// 2. 动态Spring校验
		if (cacheConfig.getCacheBeans() != null) {
			for (CacheBean cacheBean : cacheConfig.getCacheBeans()) {
				for (MethodConfig methodConfig : cacheBean.getCacheMethods()) {
					doValidSpringMethod(applicationContext,
							cacheBean.getBeanName(),
							methodConfig.getMethodName(),
							methodConfig.getParameterTypes());
				}
			}
		}

		if (cacheConfig.getCacheCleanBeans() != null) {
			for (CacheCleanBean cleanBean : cacheConfig.getCacheCleanBeans()) {
				for (CacheCleanMethod method : cleanBean.getMethods()) {
					doValidSpringMethod(applicationContext,
							cleanBean.getBeanName(), method.getMethodName(),
							method.getParameterTypes());

					for (MethodConfig clearMethod : method.getCleanMethods()) {
						doValidSpringMethod(applicationContext,
								cleanBean.getBeanName(),
								clearMethod.getMethodName(),
								clearMethod.getParameterTypes());
					}
				}
			}
		}

		// 3. 配置重复校验
		checkRepeatMethod(cacheConfig);
	}

	/**
	 * 校验配置的method是否存在
	 * 
	 * @param applicationContext
	 * @param beanName
	 * @param methodName
	 * @param parameterTypes
	 */
	private static void doValidSpringMethod(
			ApplicationContext applicationContext, String beanName,
			String methodName, List<Class<?>> parameterTypes) {
		Assert.notNull(applicationContext);
		Assert.notNull(beanName);
		Assert.notNull(methodName);
		Assert.notNull(parameterTypes);// autoFill时，参数都会填充，null会被填充为空List

		Object bean = applicationContext.getBean(beanName);
		Assert.notNull(bean, "找不到Bean:" + beanName);

		Method[] methods = bean.getClass().getMethods();

		boolean isOk = false;

		for (Method m : methods) {
			if (m.getName().equals(methodName)) {
				// 参数类型也要一致
				Class<?>[] toCompareParams = m.getParameterTypes();

				if (toCompareParams.length != parameterTypes.size())
					continue;

				boolean haveDiff = false;

				for (int i = 0; i < toCompareParams.length; i++) {
					if (!toCompareParams[i].equals(parameterTypes.get(i))) {
						haveDiff = true;
						break;
					}
				}

				if (!haveDiff) {
					isOk = true;
					break;
				}
			}
		}

		if (!isOk) {
			throw new LoadConfigException("找不到配置的方法,Bean=" + beanName
					+ ",method=" + methodName + ",params=" + parameterTypes.toString());
		}
	}

	private static void checkRepeatMethod(CacheConfig cacheConfig) {
		// 3.1 缓存方法是否存在重复配置校验
		List<String> keys = new ArrayList<String>();
		if (cacheConfig.getCacheBeans() != null) {
			for (CacheBean cacheBean : cacheConfig.getCacheBeans()) {
				for (MethodConfig methodConfig : cacheBean.getCacheMethods()) {
					String cacheAdapterKey = CacheCodeUtil.getCacheAdapterKey(
							cacheConfig.getStoreRegion(),
							cacheBean.getBeanName(), methodConfig);

					if (keys.contains(cacheAdapterKey))
						throw new LoadConfigException("缓存配置中方法重复了,Bean:"
								+ cacheBean.getBeanName() + ",method="
								+ methodConfig.getMethodName());

					keys.add(cacheAdapterKey);
				}
			}
		}

		// 3.2 缓存清理方法是否存在重复配置校验
		keys.clear();
		if (cacheConfig.getCacheCleanBeans() != null) {
			for (CacheCleanBean cleanBean : cacheConfig.getCacheCleanBeans()) {
				for (CacheCleanMethod method : cleanBean.getMethods()) {
					String cacheAdapterKey = CacheCodeUtil.getCacheAdapterKey(
							cacheConfig.getStoreRegion(),
							cleanBean.getBeanName(), method);

					if (keys.contains(cacheAdapterKey))
						throw new LoadConfigException("缓存清理配置中方法重复了,Bean:"
								+ cleanBean.getBeanName() + ",method="
								+ method.getMethodName());

					keys.add(cacheAdapterKey);
				}
			}
		}

		// 3.3 缓存清理方法的关联方法是否存在重复配置校验
		keys.clear();
		if (cacheConfig.getCacheCleanBeans() != null) {
			for (CacheCleanBean cleanBean : cacheConfig.getCacheCleanBeans()) {
				for (CacheCleanMethod method : cleanBean.getMethods()) {
					for (MethodConfig clearMethod : method.getCleanMethods()) {
						String cacheAdapterKey = CacheCodeUtil
								.getCacheAdapterKey(
										cacheConfig.getStoreRegion(),
										cleanBean.getBeanName(), clearMethod);

						if (keys.contains(cacheAdapterKey))
							throw new LoadConfigException(
									"缓存清理关联方法配置中方法重复了,Bean:"
											+ cleanBean.getBeanName()
											+ ",method="
											+ method.getMethodName()
											+ ",clearMethod="
											+ clearMethod.getMethodName());

						keys.add(cacheAdapterKey);
					}
				}
			}
		}
	}

}
