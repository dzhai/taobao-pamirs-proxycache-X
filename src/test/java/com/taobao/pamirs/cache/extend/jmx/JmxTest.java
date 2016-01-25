package com.taobao.pamirs.cache.extend.jmx;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.taobao.pamirs.cache.load.impl.LocalConfigCacheManager;

/**
 * 验证JMX
 * 
 * @author xiaocheng 2012-11-20
 */
@SpringApplicationContext({ "/store/tair-store.xml", "/load/cache-spring.xml",
		"/extend/jmx/jmx-spring.xml" })
public class JmxTest extends UnitilsJUnit4 {

	@SpringBeanByName
	private LocalConfigCacheManager cacheManager;

	@Test
	public void testJMX() throws InterruptedException {
		assertThat(cacheManager, notNullValue());

		// JMX验证时，打开下面的注释，人工验证：http://localhost:5168

		// Thread.sleep(1000 * 60 * 60L);// 1小时
	}

}
