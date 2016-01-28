package com.taobao.pamirs.cache.load.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.Serializable;
import java.util.ArrayList;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.taobao.pamirs.cache.framework.CacheProxy;
import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.load.testbean.ASerivce;
import com.taobao.pamirs.cache.util.CacheCodeUtil;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/load/cache-spring.xml" })
public class LocalConfigServiceImplTest {

	@Resource
	private LocalConfigCacheManager cacheManager;
	@Resource
	private ASerivce aService;

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
		System.out.println(aService.firstHaveValue("name"));
		System.out.println(aService.firstHaveValue("name"));
		System.out.println(aService.firstHaveValue("name"));
	}

}
