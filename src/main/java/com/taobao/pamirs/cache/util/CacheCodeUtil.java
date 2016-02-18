package com.taobao.pamirs.cache.util;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.framework.config.ParameterIndex;

/**
 * 缓存Code辅助类
 * 
 * @author xiaocheng 2012-11-2
 */
public class CacheCodeUtil {

	/**
	 * Key的主分隔符<br>
	 * 格式：region@prefix#cache#beanName#methodName#{String}
	 */
	public static final String KEY_SPLITE_SIGN = "#";
	/**
	 * key中方法参数的分隔符<br>
	 * 格式：{String|Long}
	 */
	public static final String KEY_PARAMS_SPLITE_SIGN = "|";

	/** region分隔符 */
	public static final String REGION_SPLITE_SIGN = "@";

	/**
	 * 取得最终的缓存Code中参数值分隔符<br>
	 * 格式：region@prefix#cache#beanName#methodName#{String,Long}abc@@123
	 */
	public static final String CODE_PARAM_VALUES_SPLITE_SIGN = "@@";

	/**
	 * 取得最终的缓存Code<br>
	 * 格式：region@prefix#cache#beanName#methodName#{String|Long}abc@@123
	 * 
	 * @param region
	 * @param beanName
	 * @param methodConfig
	 * @param parameters
	 *            数组长度会以methodConfig.getParameterTypes()优先，多余的会丢失
	 * @return
	 */
	public static String getCacheCode(String region, String beanName,
			MethodConfig methodConfig, Object[] parameters) {
		// 最终的缓存code
		StringBuilder code = new StringBuilder();
		// 1. region
		// 2. prefix
		// 3. bean + method + parameter
		code.append(getCacheAdapterKey(region, beanName, methodConfig));
		
		//反射对象中的属性
		List<ParameterIndex> parameterIndexs=methodConfig.getParameterIndexs();

		// 4. value
		List<Class<?>> parameterTypes = methodConfig.getParameterTypes();
		if (parameterTypes != null) {
			StringBuilder valus = new StringBuilder();
			for (int i = 0; i < parameterTypes.size(); i++) {
				if (valus.length() != 0) {
					valus.append(CODE_PARAM_VALUES_SPLITE_SIGN);
				}
				Object value=parameters[i];
				if(parameterIndexs!=null && parameterIndexs.size()>0){
					ParameterIndex parameterIndex=parameterIndexs.get(i);
					if(parameterIndex==null){
						continue;
					}
					if(StringUtils.isNotBlank(parameterIndex.getName()) && value != null){
						value=ReflectionUtil.invokeGetterMethod(value, parameterIndex.getName());
					}
				}
				valus.append(value == null ? "null" : value.toString());
			}
			code.append(valus.toString());
		}

		return code.toString();
	}
	
	
	/**
	 * 取得最终的缓存Code<br>
	 * 格式：region@prefix#cache#beanName#methodName#{String|Long}abc@@123
	 * 
	 * @param region
	 * @param beanName
	 * @param methodConfig
	 * @param parameters
	 *            数组长度会以methodConfig.getParameterTypes()优先，多余的会丢失
	 * @return
	 */
	public static String getCleanCacheCode(String region, String beanName,
			MethodConfig methodConfig, Object[] parameters) {
		// 最终的缓存code
		return getCommonAdapterKey(region, methodConfig);
	}

	/**
	 * 缓存适配器的key<br> 为了做数据验证
	 * 格式：region@prefix#cache#beanName#methodName#{String|Long}
	 * 
	 * @param region
	 * @param beanName
	 * @param methodConfig
	 * @return
	 */
	public static String getCacheAdapterKeyForVerify(String region, String beanName,
			MethodConfig methodConfig) {
		Assert.notNull(methodConfig);

		// 最终的key
		StringBuilder key = new StringBuilder();
		// 1. region@prefix#cache#
		key.append(getCommonAdapterKey(region,methodConfig));
		
		// 2. bean + method + parameter
		String methodName = methodConfig.getMethodName();
		List<Class<?>> parameterTypes = methodConfig.getParameterTypes();

		key.append(beanName).append(KEY_SPLITE_SIGN);
		key.append(methodName).append(KEY_SPLITE_SIGN);
		key.append(parameterTypesToString(parameterTypes));

		return key.toString();

	}
	
	/**
	 * 缓存适配器的key<br>
	 * 格式：region@prefix@beanName#methodName#
	 * 
	 * @param region
	 * @param beanName
	 * @param methodConfig
	 * @return
	 */
	public static String getCacheAdapterKey(String region, String beanName,
			MethodConfig methodConfig) {
		Assert.notNull(methodConfig);

		// 最终的key
		StringBuilder key = new StringBuilder();
		// 1. region@prefix#cache#
		key.append(getCommonAdapterKey(region,methodConfig));
		if(CacheCodeType.DEFAULT_TYPE.equals(CacheCodeType.toEnum(methodConfig.getCacheCodeType()))){				
			// 2. bean + method
			String methodName = methodConfig.getMethodName();
			key.append(beanName).append(KEY_SPLITE_SIGN);
			key.append(methodName).append(KEY_SPLITE_SIGN);
		}else{
			// 2. bean + method + parameter
			String methodName = methodConfig.getMethodName();
			key.append(beanName).append(KEY_SPLITE_SIGN);
			key.append(methodName).append(KEY_SPLITE_SIGN);
			List<Class<?>> parameterTypes = methodConfig.getParameterTypes();
			key.append(parameterTypesToString(parameterTypes));
			
		}
		return key.toString();

	}
	
	/**
	 * 缓存适配器的key<br>
	 * region@prefix#cache#
	 */
	public static String getCommonAdapterKey(String region,MethodConfig methodConfig){
		StringBuilder key = new StringBuilder();
		// 1. region
		if (StringUtils.isNotBlank(region)){
			key.append(region).append(REGION_SPLITE_SIGN);
		}
					
		// 2. prefix
		if(StringUtils.isNotBlank(methodConfig.getPrefix())){
			key.append(methodConfig.getPrefix()).append(KEY_SPLITE_SIGN);			
		}
		
		// 3. cache
		if(StringUtils.isNotBlank(methodConfig.getCache())){
			key.append(methodConfig.getCache()).append(KEY_SPLITE_SIGN);			
		}
		return key.toString();
	}
	
	/**
	 * 缓存适配器的key<br>
	 * region@prefix#cache#
	 */
	public static String getCleanCacheAdapterKey(String region, String beanName,MethodConfig methodConfig){
		StringBuilder key = new StringBuilder();
		// 1. region
		if (StringUtils.isNotBlank(region)){
			key.append(region).append(REGION_SPLITE_SIGN);
		}
					
		// 2. beanName
		if(StringUtils.isNotBlank(beanName)){
			key.append(beanName).append(KEY_SPLITE_SIGN);			
		}
		
		// 3. methodName
		if(StringUtils.isNotBlank(methodConfig.getMethodName())){
			key.append(methodConfig.getMethodName()).append(KEY_SPLITE_SIGN);			
		}
		return key.toString();
	}

	/**
	 * 参数toString，格式{String|int}
	 * 
	 * @param parameterTypes
	 * @return
	 */
	public static String parameterTypesToString(List<Class<?>> parameterTypes) {
		StringBuilder parameter = new StringBuilder("{");
		if (parameterTypes != null) {
			for (Class<?> clazz : parameterTypes) {
				if (parameter.length() != 1) {
					parameter.append(KEY_PARAMS_SPLITE_SIGN);
				}

				parameter.append(clazz.getSimpleName());
			}
		}
		parameter.append("}");
		return parameter.toString();
	}

}
