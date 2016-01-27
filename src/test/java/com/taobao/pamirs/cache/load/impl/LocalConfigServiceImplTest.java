package com.taobao.pamirs.cache.load.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.Serializable;
import java.util.ArrayList;

import javax.annotation.Resource;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.taobao.pamirs.cache.framework.CacheProxy;
import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.util.CacheCodeUtil;

@SpringApplicationContext({ "/store/tair-store.xml", "/load/cache-spring.xml" })
public class LocalConfigServiceImplTest extends UnitilsJUnit4 {

	@Resource
	private LocalConfigCacheManager cacheManager;

	@Test
	public void testGetCacheProxy() {
		MethodConfig methodConfig = new MethodConfig();
		methodConfig.setMethodName("firstHaveValue");

		ArrayList<Class<?>> parameterTypes = new ArrayList<Class<?>>();
		parameterTypes.add(String.class);
		methodConfig.setParameterTypes(parameterTypes);

		String key = CacheCodeUtil.getCacheAdapterKey(
				cacheManager.getStoreRegion(), "aService", methodConfig);

		CacheProxy<Serializable, Serializable> cacheProxys = cacheManager
				.getCacheProxy(key);

		assertThat(cacheProxys, notNullValue());
		assertThat(cacheProxys.getKey(), equalTo(key));
	}

}
