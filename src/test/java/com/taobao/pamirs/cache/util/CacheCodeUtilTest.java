package com.taobao.pamirs.cache.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.taobao.pamirs.cache.framework.config.MethodConfig;

/**
 * 缓存Code辅助类单元测试
 * 
 * @author xiaocheng 2012-11-19
 */
public class CacheCodeUtilTest {

	String region = "rg-123";
	String beanName = "bean456";
	MethodConfig methodConfig;

	@Before
	public void init() {
		if (methodConfig != null)
			return;

		methodConfig = new MethodConfig();
		methodConfig.setMethodName("testGetCacheAdapterKey");
		methodConfig.setExpiredTime(100);
		List<Class<?>> parameterTypes = new ArrayList<Class<?>>();
		parameterTypes.add(boolean.class);
		parameterTypes.add(Boolean.class);
		parameterTypes.add(char.class);
		parameterTypes.add(Character.class);
		parameterTypes.add(byte.class);
		parameterTypes.add(Byte.class);
		parameterTypes.add(short.class);
		parameterTypes.add(Short.class);
		parameterTypes.add(int.class);
		parameterTypes.add(Integer.class);
		parameterTypes.add(long.class);
		parameterTypes.add(Long.class);
		parameterTypes.add(float.class);
		parameterTypes.add(Float.class);
		parameterTypes.add(double.class);
		parameterTypes.add(Double.class);
		parameterTypes.add(Date.class);
		parameterTypes.add(String.class);
		methodConfig.setParameterTypes(parameterTypes);
	}

	@Test
	public void testGetCacheCode() {
		// regionbeanName#methodName#{String,Long}abc@@123
		Date now = new Date();
		Object[] parameters = getParameters(now);
		assertCacheCode(now, parameters);
	}
	
	/**
	 * 生成的缓存Code支持关联的清理方法参数比原方法参数少<br>
	 * 如：<br>
	 * cleanCache(1,2,3)有三个参数，而被关联的方法参数少于三个的
	 * 
	 */
	@Test
	public void testGetCacheCodeSupportCleanMethod() {
		Date now = new Date();
		Object[] parameters = getParameters(now);
		Object[] moreParamters = new Object[20];
		for (int i = 0; i < parameters.length; i++) {
			moreParamters[i] = parameters[i];
		}
		
		moreParamters[18] = "123";
		moreParamters[19] = "456";
		
		assertCacheCode(now, moreParamters);
	}

	private void assertCacheCode(Date now, Object[] parameters) {
		String cacheCode = CacheCodeUtil.getCacheCode(region, beanName,
				methodConfig, parameters);
		StringBuilder result = new StringBuilder();
		result.append(GetCacheAdapterKeyResult());

		result.append("true@@false");
		result.append("@@a@@B");
		result.append("@@110@@111");
		result.append("@@998@@999");
		result.append("@@123@@456");
		result.append("@@789@@1000");
		result.append("@@1.23@@2.34");
		result.append("@@6.78@@7.89");
		result.append("@@").append(now.toString());
		result.append("@@xyz");

		assertThat(cacheCode, is(result.toString()));
	}
	
	private Object[] getParameters(Date now) {
		Object[] parameters = new Object[18];
		parameters[0] = true;
		parameters[1] = Boolean.FALSE;
		parameters[2] = 'a';
		parameters[3] = Character.valueOf('B');
		parameters[4] = (byte) 110;
		parameters[5] = Byte.valueOf("111");
		parameters[6] = (short) 998;
		parameters[7] = Short.valueOf("999");
		parameters[8] = 123;
		parameters[9] = Integer.valueOf(456);
		parameters[10] = 789L;
		parameters[11] = Long.valueOf("1000");
		parameters[12] = 1.23F;
		parameters[13] = Float.valueOf(2.34F);
		parameters[14] = 6.78D;
		parameters[15] = Double.valueOf(7.89D);
		parameters[16] = now;
		parameters[17] = "xyz";
		return parameters;
	}

	@Test
	public void testGetCacheAdapterKey() {
		// regionbeanName#methodName#{String,Long}
		String adapterKey = CacheCodeUtil.getCacheAdapterKey(region, beanName,
				methodConfig);

		assertThat(adapterKey, is(GetCacheAdapterKeyResult()));
	}

	private String GetCacheAdapterKeyResult() {
		StringBuilder result = new StringBuilder();
		result.append(region).append("@");
		result.append(beanName);
		result.append("#").append(methodConfig.getMethodName());
		result.append("#{");
		result.append("boolean|Boolean");
		result.append("|char|Character");
		result.append("|byte|Byte");
		result.append("|short|Short");
		result.append("|int|Integer");
		result.append("|long|Long");
		result.append("|float|Float");
		result.append("|double|Double");
		result.append("|Date|String");
		result.append("}");

		return result.toString();
	}
}
