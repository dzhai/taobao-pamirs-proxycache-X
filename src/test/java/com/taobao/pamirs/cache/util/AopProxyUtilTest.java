package com.taobao.pamirs.cache.util;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.pamirs.cache.load.testbean.ASerivce;

/**
 * 代理相关测试类
 * 
 * @author xiaocheng 2012-11-22
 */
public class AopProxyUtilTest {

	ApplicationContext context = new ClassPathXmlApplicationContext(
			new String[] { "/store/tair-store.xml", "/load/cache-spring.xml" });

	@Test
	public void testGetPrimitiveProxyTarget() throws Exception {
		Object bean = context.getBean("aService");
		assertThat(bean, notNullValue());

		// cglib
		Object target = AopProxyUtil.getPrimitiveProxyTarget(bean);
		assertThat(target, notNullValue());

		// jdkDynamicProxy 也测过了，
		// 需要修改CacheManagerHandle.setProxyTargetClass(false);
	}

	@Test
	public void testInnerMethod() {
		ASerivce aSerivce = (ASerivce) context.getBean("aService");
		assertThat(aSerivce, notNullValue());

		aSerivce.testInner(false);// 第一次不走缓存
		aSerivce.testInner(true);// 第一次不走缓存
		aSerivce.testInner(false);// 第二次也不走缓存
		aSerivce.testInner(true);// 第二次走缓存，赞！
	}
}
