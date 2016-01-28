package com.taobao.pamirs.cache.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.taobao.pamirs.cache.CacheManager;
import com.taobao.pamirs.cache.framework.config.CacheBean;
import com.taobao.pamirs.cache.framework.config.CacheCleanBean;
import com.taobao.pamirs.cache.framework.config.CacheCleanMethod;
import com.taobao.pamirs.cache.framework.config.CacheConfig;
import com.taobao.pamirs.cache.framework.config.CacheModule;
import com.taobao.pamirs.cache.framework.config.MethodConfig;

/**
 * 配置辅助类单元测试
 * 
 * @author xiaocheng 2012-11-19
 */
@SpringApplicationContext({ "/store/tair-store.xml", "/load/cache-spring.xml" })
public class ConfigUtilTest extends UnitilsJUnit4 {

	@SpringBeanByName
	private CacheManager cacheManager;

	CacheModule cacheModule;
	CacheConfig cacheConfig;

	@Before
	public void init() {
		InputStream configStream = ConfigUtilTest.class.getClassLoader()
				.getResourceAsStream("load/cache-config.xml");
		cacheModule = ConfigUtil.getCacheConfigModule(configStream);

		cacheConfig = new CacheConfig();
		cacheConfig.getCacheBeans().addAll(cacheModule.getCacheBeans());
		cacheConfig.getCacheCleanBeans().addAll(
				cacheModule.getCacheCleanBeans());
	}

	@Test
	public void testIsBeanHaveCache() {
		assertThat(ConfigUtil.isBeanHaveCache(cacheConfig, "aService"),
				is(true));
		assertThat(ConfigUtil.isBeanHaveCache(cacheConfig, "bService"),
				is(true));
		assertThat(ConfigUtil.isBeanHaveCache(cacheConfig, "cService"),
				is(false));
	}

	@Test
	public void testGetCacheMethod() {
		String beanName = "aService";
		String methodName = "firstHaveValue";
		List<Class<?>> parameterTypes = new ArrayList<Class<?>>();
		parameterTypes.add(String.class);

		MethodConfig method = ConfigUtil.getCacheMethod(cacheConfig, beanName,
				methodName, parameterTypes);
		assertThat(method, notNullValue());
		assertThat(method.getMethodName(), equalTo(methodName));
	}

	@Test
	public void testGetCacheCleanMethods() {
		String beanName = "aService";
		String methodName = "md5Name";
		List<Class<?>> parameterTypes = new ArrayList<Class<?>>();
		parameterTypes.add(String.class);
		ConfigUtil.getCacheCleanMethods(cacheConfig, beanName, methodName,
				parameterTypes);
	}

	@Test
	public void testGetCacheConfigModule() throws Exception {
		assertConfig(cacheModule, false);
	}

	@Test
	public void testAutoFillCacheConfig() {
		ConfigUtil.autoFillCacheConfig(cacheConfig,
				cacheManager.getApplicationContext());

		assertConfig(cacheConfig, true);
	}

	private void assertConfig(CacheModule cacheModule, boolean isFilled) {
		assertThat(cacheModule.getCacheBeans().size(), equalTo(2));
		assertThat(cacheModule.getCacheCleanBeans().size(), equalTo(1));

		// 1. cache bean
		for (CacheBean cacheBean : cacheModule.getCacheBeans()) {

			if (cacheBean.getBeanName().equals("aService")) {
				assertThat(cacheBean.getCacheMethods().size(), equalTo(4));

				for (MethodConfig methodConfig : cacheBean.getCacheMethods()) {
					if (methodConfig.getMethodName().equals("md5Name")) {// 两个

						if (methodConfig.getParameterTypes().size() == 1) {
							assertThat(methodConfig.getParameterTypes().get(0)
									.getSimpleName(), equalTo("String"));
						} else {
							assertThat(methodConfig.getParameterTypes().size(),
									equalTo(2));
							assertThat(methodConfig.getParameterTypes().get(0)
									.getSimpleName(), equalTo("String"));
							assertThat(methodConfig.getParameterTypes().get(1)
									.getSimpleName(), equalTo("String"));
						}

						assertThat(methodConfig.getExpiredTime(), nullValue());
					}

					if (methodConfig.getMethodName().equals("firstHaveValue")) {
						assertThat(methodConfig.getExpiredTime(), equalTo(2));
						assertThat(methodConfig.getParameterTypes().size(),
								equalTo(1));
						assertThat(methodConfig.getParameterTypes().get(0)
								.getSimpleName(), equalTo("String"));
					}

					if (methodConfig.getMethodName().equals("noRewirteMethod")) {
						assertThat(methodConfig.getExpiredTime(), nullValue());
						if (!isFilled) {
							assertThat(methodConfig.getParameterTypes(),
									nullValue());
						} else {
							assertThat(methodConfig.getParameterTypes().size(),
									equalTo(1));
							assertThat(methodConfig.getParameterTypes().get(0)
									.getSimpleName(), equalTo("String"));
						}
					}

				}

			} else {
				assertThat(cacheBean.getCacheMethods().size(), equalTo(1));
				MethodConfig methodConfig = cacheBean.getCacheMethods().get(0);
				assertThat(methodConfig.getMethodName(),
						equalTo("doVarietyArgs"));
				assertThat(methodConfig.getExpiredTime(), nullValue());
				assertThat(methodConfig.getParameterTypes().size(), equalTo(18));

				List<Class<?>> parameterTypes = methodConfig
						.getParameterTypes();
				assertThat(parameterTypes.get(0).getSimpleName(),
						equalTo("boolean"));
				assertThat(parameterTypes.get(1).getSimpleName(),
						equalTo("Boolean"));
				assertThat(parameterTypes.get(2).getSimpleName(),
						equalTo("char"));
				assertThat(parameterTypes.get(3).getSimpleName(),
						equalTo("Character"));
				assertThat(parameterTypes.get(4).getSimpleName(),
						equalTo("byte"));
				assertThat(parameterTypes.get(5).getSimpleName(),
						equalTo("Byte"));
				assertThat(parameterTypes.get(6).getSimpleName(),
						equalTo("short"));
				assertThat(parameterTypes.get(7).getSimpleName(),
						equalTo("Short"));
				assertThat(parameterTypes.get(8).getSimpleName(),
						equalTo("int"));
				assertThat(parameterTypes.get(9).getSimpleName(),
						equalTo("Integer"));
				assertThat(parameterTypes.get(10).getSimpleName(),
						equalTo("long"));
				assertThat(parameterTypes.get(11).getSimpleName(),
						equalTo("Long"));
				assertThat(parameterTypes.get(12).getSimpleName(),
						equalTo("float"));
				assertThat(parameterTypes.get(13).getSimpleName(),
						equalTo("Float"));
				assertThat(parameterTypes.get(14).getSimpleName(),
						equalTo("double"));
				assertThat(parameterTypes.get(15).getSimpleName(),
						equalTo("Double"));
				assertThat(parameterTypes.get(16).getSimpleName(),
						equalTo("Date"));
				assertThat(parameterTypes.get(17).getSimpleName(),
						equalTo("String"));
			}

		}

		// 2. clear cache bean
		CacheCleanBean cacheCleanBean = cacheModule.getCacheCleanBeans().get(0);
		assertThat(cacheCleanBean.getBeanName(), equalTo("aService"));
		assertThat(cacheCleanBean.getMethods().size(), equalTo(1));

		// 2.1 methods
		CacheCleanMethod cacheCleanMethod = cacheCleanBean.getMethods().get(0);
		assertThat(cacheCleanMethod.getMethodName(), equalTo("md5Name"));
		assertThat(cacheCleanMethod.getExpiredTime(), nullValue());
		assertThat(cacheCleanMethod.getParameterTypes().size(), equalTo(2));
		assertThat(cacheCleanMethod.getParameterTypes().get(0).getSimpleName(),
				equalTo("String"));
		assertThat(cacheCleanMethod.getParameterTypes().get(0).getSimpleName(),
				equalTo("String"));

		// 2.2 sub-clear-mothods
		assertThat(cacheCleanMethod.getCleanMethods().size(), equalTo(1));
		MethodConfig clear = cacheCleanMethod.getCleanMethods().get(0);
		assertThat(clear.getMethodName(), equalTo("clearNames"));
		assertThat(clear.getExpiredTime(), nullValue());
		if (!isFilled) {
			assertThat(clear.getParameterTypes(), nullValue());
		} else {
			assertThat(clear.getParameterTypes().size(), equalTo(2));
			assertThat(clear.getParameterTypes().get(0).getSimpleName(),
					equalTo("String"));
			assertThat(clear.getParameterTypes().get(0).getSimpleName(),
					equalTo("String"));
		}
	}

}
