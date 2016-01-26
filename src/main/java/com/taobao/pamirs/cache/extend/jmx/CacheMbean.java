package com.taobao.pamirs.cache.extend.jmx;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import com.taobao.pamirs.cache.extend.jmx.mbean.AbstractDynamicMBean;
import com.taobao.pamirs.cache.framework.CacheProxy;
import com.taobao.pamirs.cache.framework.config.CacheBean;
import com.taobao.pamirs.cache.framework.config.CacheConfig;
import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.util.AopProxyUtil;
import com.taobao.pamirs.cache.util.CacheCodeUtil;
import com.taobao.pamirs.cache.util.IpUtil;
import com.taobao.pamirs.cache.util.ParameterSupportTypeUtil;

/**
 * 缓存bean的Mbean
 * 
 * @author xuanyu
 * @author xiaocheng 2012-11-8
 */
public class CacheMbean<K extends Serializable, V extends Serializable> extends
		AbstractDynamicMBean {

	public static final String MBEAN_NAME = "Pamirs-Cache";

	private CacheProxy<K, V> cacheProxy = null;
	private CacheMbeanListener listener;
	private ApplicationContext applicationContext;
	/**
	 * 失效时间，单位：秒。
	 * 
	 * @see CacheBean.expiredTime
	 */
	private Integer expiredTime;
	/**
	 * Map自动清理表达式
	 * 
	 * @see CacheConfig.storeMapCleanTime
	 */
	private String storeMapCleanTime;

	public CacheMbean(CacheProxy<K, V> cache, CacheMbeanListener listener,
			ApplicationContext applicationContext, String storeMapCleanTime,
			Integer expiredTime) {
		this.cacheProxy = cache;
		this.listener = listener;
		this.applicationContext = applicationContext;
		this.storeMapCleanTime = storeMapCleanTime;
		this.expiredTime = expiredTime;
	}

	public String getCacheName() {
		return cacheProxy.getKey();
	}

	public String getStoreType() {
		return cacheProxy.getStoreType().getName();
	}

	public String getStoreCount() {
		try {
			return cacheProxy.size() + "";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public boolean getIsUseCache() {
		return cacheProxy.isUseCache();
	}

	public long getReadHits() {
		return listener.getReadSuccessCount().get();
	}

	public long getReadUnHits() {
		return listener.getReadFailCount().get();
	}

	public String getReadHitRate() {
		return listener.getReadHitRate();
	}

	public long getReadAvgTime() {
		return listener.getReadAvgTime();
	}

	public long getWriteAvgTime() {
		return listener.getWriteAvgTime();
	}

	public long getRemoveCount() {
		return listener.getRemoveCount().get();
	}

	public long getExpireTime() {
		return expiredTime == null ? 0L : expiredTime.longValue();
	}

	public String getCleanTimeExpression() {
		return storeMapCleanTime;
	}

	@SuppressWarnings("unchecked")
	public V get(K key) {
		try {
			return cacheProxy.get((K) keyToCacheCode((String) key),
					IpUtil.getLocalIp());
		} catch (Exception e) {
			return (V) ("Cache Get Failure Key:" + key + " Exception:" + e
					.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public String put(K key, V value) {
		try {
			cacheProxy.put((K) keyToCacheCode((String) key), value,
					IpUtil.getLocalIp());
			return "Cache Put Successfully Key:" + key + " Value:" + value;
		} catch (Exception e) {
			return "Cache Put Failure Key:" + key + " Value:" + value
					+ " Exception:" + e.getMessage();
		}
	}

	@SuppressWarnings("unchecked")
	public String remove(K key) {
		try {
			cacheProxy.remove((K) keyToCacheCode((String) key),
					IpUtil.getLocalIp());
			return "Cache Remove Successfully Key:" + key;
		} catch (Exception e) {
			return "Cache Remove Failure Key:" + key;
		}
	}

	/**
	 * 这个方法通过 Cache 获取真实值.
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public V getRealValue(K key) {
		try {
			// 重用其中的非法校验
			keyToCacheCode((String) key);

			MethodConfig methodConfig = cacheProxy.getMethodConfig();
			Object bean = AopProxyUtil
					.getPrimitiveProxyTarget(applicationContext
							.getBean(cacheProxy.getBeanName()));// 取得原生对象
			List<Class<?>> parameterTypes = methodConfig.getParameterTypes();

			// 无参
			if (parameterTypes == null) {
				Method method = bean.getClass().getMethod(
						methodConfig.getMethodName());
				return (V) method.invoke(bean);
			}

			// 有参
			Class<?>[] parameterTypeArray = new Class<?>[parameterTypes.size()];
			for (int i = 0; i < parameterTypeArray.length; i++) {
				parameterTypeArray[i] = parameterTypes.get(i);
			}
			Method method = bean.getClass().getMethod(
					methodConfig.getMethodName(), parameterTypeArray);

			String[] keyItems = key.toString().split(
					CacheCodeUtil.CODE_PARAM_VALUES_SPLITE_SIGN);
			Object[] parameterValues = new Object[parameterTypes.size()];
			for (int i = 0; i < parameterTypes.size(); i++) {
				parameterValues[i] = ParameterSupportTypeUtil
						.valueConvertToType(keyItems[i], parameterTypes.get(i));
			}
			return (V) method.invoke(bean, parameterValues);
		} catch (Exception e) {
			return (V) ("getRealValue Failure Key:" + key + " Exception:" + e
					.getMessage());
		}
	}

	public boolean getRealValueAndPut(K key) {
		V realValue = this.getRealValue(key);
		if (realValue != null)
			this.put(key, realValue);
		else
			this.remove(key);

		return true;
	}

	public boolean invalidBeforeCache() {
		cacheProxy.invalidBefore();
		return true;
	}

	private String keyToCacheCode(String key) throws Exception {
		Assert.notNull(key);

		MethodConfig methodConfig = cacheProxy.getMethodConfig();
		List<Class<?>> parameterTypes = methodConfig.getParameterTypes();

		String[] keyItems = key.toString().split(
				CacheCodeUtil.CODE_PARAM_VALUES_SPLITE_SIGN);

		boolean illegal = false;

		if (parameterTypes == null || parameterTypes.isEmpty()) {
			// 1. 无参方法
			if (StringUtils.isNotEmpty(key))
				illegal = true;
		} else {
			// 2. 有参方法
			if (keyItems.length != parameterTypes.size())
				illegal = true;
		}

		if (illegal) {
			String erroMsg = "jmx的参数数量和接口的参数数量不一致,请求：" + key.toString()
					+ "接口参数:" + parameterTypes;
			throw new RuntimeException(erroMsg);
		}

		// 无参
		if (parameterTypes == null) {
			return CacheCodeUtil.getCacheCode(cacheProxy.getStoreRegion(),
					cacheProxy.getBeanName(), cacheProxy.getMethodConfig(),
					null);
		}

		// 有参
		Object[] parameterValues = new Object[parameterTypes.size()];
		for (int i = 0; i < parameterTypes.size(); i++) {
			parameterValues[i] = ParameterSupportTypeUtil.valueConvertToType(
					keyItems[i], parameterTypes.get(i));
		}

		return CacheCodeUtil.getCacheCode(cacheProxy.getStoreRegion(),
				cacheProxy.getBeanName(), cacheProxy.getMethodConfig(),
				parameterValues);
	}

	protected void buildDynamicMBeanInfo() {
		MBeanAttributeInfo[] dAttributes = new MBeanAttributeInfo[] {
				new MBeanAttributeInfo("cacheName", "String", "缓存名称", true,
						false, false),
				new MBeanAttributeInfo("storeType", "String", "缓存类型", true,
						false, false),
				new MBeanAttributeInfo("storeCount", "String", "缓存数据量", true,
						false, false),
				new MBeanAttributeInfo("isUseCache", "boolean", "是否使用缓存", true,
						false, false),
				new MBeanAttributeInfo("readHits", "long", "读命中次数", true,
						false, false),
				new MBeanAttributeInfo("readUnHits", "long", "读未命中次数", true,
						false, false),
				new MBeanAttributeInfo("readHitRate", "double", "缓存命中率", true,
						false, false),
				new MBeanAttributeInfo("readAvgTime", "long", "平均缓存读耗时", true,
						false, false),
				new MBeanAttributeInfo("writeAvgTime", "long", "平均缓存写耗时", true,
						false, false),
				new MBeanAttributeInfo("removeCount", "long", "缓存删除次数", true,
						false, false),
				new MBeanAttributeInfo("expireTime", "long", "缓存数据失效时间", true,
						false, false),
				new MBeanAttributeInfo("cleanTimeExpression", "String",
						"缓存清理时间", true, false, false) };

		String info = "多参数Key需用@@分隔";

		MBeanOperationInfo[] dOperations = new MBeanOperationInfo[] {
				new MBeanOperationInfo("get", "读取缓存",
						new MBeanParameterInfo[] { new MBeanParameterInfo(
								"CacheGet", "java.lang.String", "输入缓存Key。"
										+ info) }, "String",
						MBeanOperationInfo.ACTION),
				new MBeanOperationInfo("put", "写入缓存", new MBeanParameterInfo[] {
						new MBeanParameterInfo("CachePut Key",
								"java.lang.String", "输入缓存Key。" + info),
						new MBeanParameterInfo("CachePut Value",
								"java.lang.String", "输入缓存值Value.") }, "String",
						MBeanOperationInfo.ACTION),
				new MBeanOperationInfo("remove", "删除缓存",
						new MBeanParameterInfo[] { new MBeanParameterInfo(
								"CacheRemove", "java.lang.String", "输入缓存Key。"
										+ info) }, "String",
						MBeanOperationInfo.ACTION),
				new MBeanOperationInfo("getRealValue", "读取原生方法结果数据",
						new MBeanParameterInfo[] { new MBeanParameterInfo(
								"DiskGet", "java.lang.String", "输入方法参数Key。"
										+ info) }, "String",
						MBeanOperationInfo.ACTION),
				new MBeanOperationInfo("invalidBeforeCache", "失效当前时间之前存储缓存",
						null, "String", MBeanOperationInfo.ACTION),
				new MBeanOperationInfo("getRealValueAndPut",
						"读取原生方法结果数据，并且直接Put到缓存中",
						new MBeanParameterInfo[] { new MBeanParameterInfo(
								"DiskGetAndPut", "java.lang.String",
								"输入方法参数Key。" + info) }, "boolean",
						MBeanOperationInfo.ACTION) };
		dMBeanInfo = new MBeanInfo(this.getClass().getName(), MBEAN_NAME,
				dAttributes, null, dOperations, null);
	}

}
