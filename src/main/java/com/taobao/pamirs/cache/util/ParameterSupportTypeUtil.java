package com.taobao.pamirs.cache.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 框架支持的参数类型
 * 
 * @author xiaocheng 2012-11-22
 */
public class ParameterSupportTypeUtil {

	/**
	 * 当前支持的参数类型
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isSupportParameterTypes(Class<?> type) {
		if (type.isPrimitive() || Byte.class.isAssignableFrom(type)
				|| Short.class.isAssignableFrom(type)
				|| Integer.class.isAssignableFrom(type)
				|| Long.class.isAssignableFrom(type)
				|| Float.class.isAssignableFrom(type)
				|| Double.class.isAssignableFrom(type)
				|| Character.class.isAssignableFrom(type)
				|| String.class.isAssignableFrom(type)
				|| Date.class.isAssignableFrom(type)
				|| Boolean.class.isAssignableFrom(type))
			return true;

		return false;
	}

	/**
	 * 参数转成当前支持的类型值
	 * 
	 * @param value
	 * @param clz
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T valueConvertToType(String value, Class<T> clz)
			throws Exception {
		T result = null;

		if (clz.isAssignableFrom(boolean.class)
				|| clz.isAssignableFrom(Boolean.class)) {
			result = (T) Boolean.valueOf(value);
		} else if (clz.isAssignableFrom(char.class)
				|| clz.isAssignableFrom(Character.class)) {
			result = (T) Character.valueOf(value.toCharArray()[0]);
		} else if (clz.isAssignableFrom(byte.class)
				|| clz.isAssignableFrom(Byte.class)) {
			result = (T) Byte.valueOf(value);
		} else if (clz.isAssignableFrom(short.class)
				|| clz.isAssignableFrom(Short.class)) {
			result = (T) Short.valueOf(value);
		} else if (clz.isAssignableFrom(int.class)
				|| clz.isAssignableFrom(Integer.class)) {
			result = (T) Integer.valueOf(value);
		} else if (clz.isAssignableFrom(long.class)
				|| clz.isAssignableFrom(Long.class)) {
			result = (T) Long.valueOf(value);
		} else if (clz.isAssignableFrom(float.class)
				|| clz.isAssignableFrom(Float.class)) {
			result = (T) Float.valueOf(value);
		} else if (clz.isAssignableFrom(double.class)
				|| clz.isAssignableFrom(Double.class)) {
			result = (T) Double.valueOf(value);
		} else if (clz.isAssignableFrom(Date.class)) {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			result = (T) format.parse(value);
		} else if (clz.isAssignableFrom(String.class)) {
			result = (T) value;
		}

		return result;
	}

}
