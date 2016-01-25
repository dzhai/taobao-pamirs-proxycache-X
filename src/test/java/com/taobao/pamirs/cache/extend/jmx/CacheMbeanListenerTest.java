package com.taobao.pamirs.cache.extend.jmx;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.taobao.pamirs.cache.framework.listener.CacheOprateInfo;
import com.taobao.pamirs.cache.framework.listener.CacheOprator;
import com.taobao.pamirs.cache.util.IpUtil;

/**
 * JMX–≈œ¢
 * 
 * @author xiaocheng 2012-11-20
 */
public class CacheMbeanListenerTest {

	CacheMbeanListener listener = new CacheMbeanListener();

	@Test
	public void testOprate() {
		CacheOprateInfo cacheInfo = new CacheOprateInfo("123", 100L, true,
				"beanName", null, null, IpUtil.getLocalIp());
		listener.oprate(CacheOprator.GET, cacheInfo);
		assertThat(listener.getReadSuccessCount().intValue(), equalTo(1));
		assertThat(listener.getReadHitRate(), equalTo("100%"));
	}

}
