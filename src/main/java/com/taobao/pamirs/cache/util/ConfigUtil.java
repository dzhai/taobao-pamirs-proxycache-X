package com.taobao.pamirs.cache.util;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.taobao.pamirs.cache.framework.config.CacheBean;
import com.taobao.pamirs.cache.framework.config.CacheCleanBean;
import com.taobao.pamirs.cache.framework.config.CacheCleanMethod;
import com.taobao.pamirs.cache.framework.config.CacheConfig;
import com.taobao.pamirs.cache.framework.config.CacheModule;
import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.framework.config.Parameter;
import com.taobao.pamirs.cache.load.LoadConfigException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 配置辅助类
 * 
 * @author xiaocheng 2012-11-2
 */
public class ConfigUtil {

	/**
	 * 是否bean在cache中配置
	 * 
	 * @param cacheConfig
	 * @param beanName
	 * @return
	 */
	public static boolean isBeanHaveCache(CacheConfig cacheConfig,
			String beanName) {
		if (cacheConfig == null || beanName == null)
			return false;

		List<CacheBean> cacheBeans = cacheConfig.getCacheBeans();
		List<CacheCleanBean> cacheCleanBeans = cacheConfig.getCacheCleanBeans();

		if ((cacheBeans == null || cacheBeans.size() == 0)
				&& (cacheCleanBeans == null || cacheCleanBeans.size() == 0))
			return false;

		for (CacheBean bean : cacheBeans) {
			if (beanName.equals(bean.getBeanName()))
				return true;
		}

		for (CacheCleanBean bean : cacheCleanBeans) {
			if (beanName.equals(bean.getBeanName()))
				return true;
		}

		return false;
	}

	/**
	 * 获取对应的缓存MethodConfig配置
	 * 
	 * @param cacheConfig
	 * @param beanName
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	public static MethodConfig getCacheMethod(CacheConfig cacheConfig,
			String beanName, String methodName, List<Class<?>> parameterTypes) {

		List<CacheBean> cacheBeans = cacheConfig.getCacheBeans();

		for (CacheBean bean : cacheBeans) {
			if (!beanName.equals(bean.getBeanName()))
				continue;

			List<MethodConfig> cacheMethods = bean.getCacheMethods();

			return (MethodConfig) getMethodConfig(cacheMethods, cacheConfig,
					beanName, methodName, parameterTypes);
		}

		return null;
	}

	private static MethodConfig getMethodConfig(
			List<? extends MethodConfig> list, CacheConfig cacheConfig,
			String beanName, String methodName, List<Class<?>> parameterTypes) {

		if (cacheConfig == null || beanName == null || methodName == null)
			return null;

		if (list == null || list.isEmpty())
			return null;

		for (MethodConfig bean : list) {
			if (bean.isMe(methodName, parameterTypes))
				return bean;
		}

		return null;
	}

	/**
	 * 获取对应的缓存清理的MethodConfig配置列表
	 * 
	 * @param cacheConfig
	 * @param beanName
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	public static List<MethodConfig> getCacheCleanMethods(
			CacheConfig cacheConfig, String beanName, String methodName,
			List<Class<?>> parameterTypes) {

		List<CacheCleanBean> cacheCleanBeans = cacheConfig.getCacheCleanBeans();

		for (CacheCleanBean bean : cacheCleanBeans) {
			if (!beanName.equals(bean.getBeanName()))
				continue;

			List<CacheCleanMethod> methods = bean.getMethods();
			for (CacheCleanMethod cacheCleanMethod : methods) {
				if (cacheCleanMethod.isMe(methodName, parameterTypes))
					return cacheCleanMethod.getCleanMethods();
			}
		}

		return null;
	}

	/**
	 * xml转换成CacheModule
	 * 
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static CacheModule getCacheConfigModule(InputStream inputStream) {
		XStream xStream = new XStream(new DomDriver());
		xStream.alias("cacheModule", CacheModule.class);
		xStream.alias("cacheBean", CacheBean.class);
		xStream.alias("methodConfig", MethodConfig.class);
		xStream.alias("parameter", Parameter.class);
		xStream.useAttributeFor(Parameter.class, "index");
		xStream.useAttributeFor(Parameter.class, "name");  
		xStream.alias("cacheCleanBean", CacheCleanBean.class);
		xStream.alias("cacheCleanMethod", CacheCleanMethod.class);
		if (inputStream != null) {
			CacheModule cacheConfig = (CacheModule) xStream
					.fromXML(inputStream);
			return cacheConfig;
		}

		throw new LoadConfigException("输入的配置信息为Null");
	}

	/**
	 * 自动填充配置信息
	 * 
	 * @param cacheConfig
	 * @param applicationContext
	 */
	public static void autoFillCacheConfig(CacheConfig cacheConfig,
			ApplicationContext applicationContext) {
		Assert.notNull(applicationContext);
		Assert.notNull(cacheConfig);
		Assert.isTrue(!CollectionUtils.isEmpty(cacheConfig.getCacheBeans())
				|| !CollectionUtils.isEmpty(cacheConfig.getCacheBeans()),
				"配置中缓存和清理缓存不能同时为空！");

		// 1. 对method定义，如果没有parameterTypes，则自动寻找配对（有重名方法报错）
		// 1.1 包括：cacheBean.methodConfig
		if (cacheConfig.getCacheBeans() != null) {
			for (CacheBean cacheBean : cacheConfig.getCacheBeans()) {
				for (MethodConfig methodConfig : cacheBean.getCacheMethods()) {
					if (methodConfig.getParameterTypes() != null)
						continue;

					List<Class<?>> parameterTypes = fillParameterTypes(
							cacheBean.getBeanName(), applicationContext,
							methodConfig.getMethodName());
					methodConfig.setParameterTypes(parameterTypes);
				}
			}
		}
		// 1.2 包括：cacheCleanBean.cacheCleanMethod
		if (cacheConfig.getCacheCleanBeans() != null) {
			for (CacheCleanBean cleanBean : cacheConfig.getCacheCleanBeans()) {
				for (CacheCleanMethod method : cleanBean.getMethods()) {
					if (method.getParameterTypes() != null)
						continue;

					List<Class<?>> parameterTypes = fillParameterTypes(
							cleanBean.getBeanName(), applicationContext,
							method.getMethodName());
					method.setParameterTypes(parameterTypes);
				}
			}
		}

		// 2. 填充缓存清理关联的方法参数：cacheCleanBean.methods.cleanMethods.parameterTypes
		if (cacheConfig.getCacheCleanBeans() != null) {
			for (CacheCleanBean cleanBean : cacheConfig.getCacheCleanBeans()) {
				for (CacheCleanMethod method : cleanBean.getMethods()) {
					for (MethodConfig clearMethod : method.getCleanMethods()) {
						clearMethod.setParameterTypes(method
								.getParameterTypes());// 继承
					}
				}
			}
		}

	}

	private static List<Class<?>> fillParameterTypes(String beanName,
			ApplicationContext applicationContext, String methodName) {
		// fill
		Object bean = applicationContext.getBean(beanName);
		Assert.notNull(bean, "找不到Bean:" + beanName);
		
		Method[] methods = bean.getClass().getMethods();
		int num = 0;
		Method index = null;
		for (Method m : methods) {
			if (m.getName().equals(methodName)) {
				num++;
				index = m;
			}
		}

		if (num > 1)
			throw new LoadConfigException("有重名方法但没有指定参数:" + beanName + "#"
					+ methodName);

		return Arrays.asList(index.getParameterTypes());
	}

}
