package com.taobao.pamirs.cache.load.verify;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.util.CollectionUtils;

import com.taobao.pamirs.cache.store.StoreType;

/**
 * 基于Annotation静态校验，减少重复编码
 * 
 * @author xiaocheng 2012-11-29
 */
public class StaticCheck {

	/**
	 * 校验对象FIELD中的Verfication
	 * 
	 * @param o
	 * @throws Exception
	 *             ：失败会抛异常
	 */
	@SuppressWarnings("rawtypes")
	public static void check(Object o) throws Exception {

		// 1. fields have annotation
		Map<String, Field> map = new HashMap<String, Field>();
		Class<?> superclass = o.getClass();
		do {
			Field[] fields = superclass.getDeclaredFields();
			for (Field f : fields) {
				Verfication annotation = f.getAnnotation(Verfication.class);
				if (annotation != null)
					map.put(f.getName(), f);
			}

			superclass = superclass.getSuperclass();
		} while (superclass != null && superclass != Object.class);

		// 2. do validate
		PropertyDescriptor[] pds = java.beans.Introspector.getBeanInfo(
				o.getClass()).getPropertyDescriptors();
		for (PropertyDescriptor pd : pds) {
			Field f = map.get(pd.getName());
			if (f == null)
				continue;

			// must have get() method
			Object fieldValue = pd.getReadMethod().invoke(o);

			Verfication v = f.getAnnotation(Verfication.class);

			StoreType[] vWhen = v.when();
			String vName = v.name();
			boolean vNotNull = v.notNull();
			boolean vNotEmpty = v.notEmpty();
			int vMaxlength = v.maxlength();
			int vMinlength = v.minlength();
			String[] vRegx = v.regx();
			boolean vNotEmptyList = v.notEmptyList();
			boolean vIsStoreType = v.isStoreType();

			// typeName\when
			if (vWhen.length > 0) {
				Method m = o.getClass().getMethod("getStoreType");
				if (m != null) {
					String storeType = (String) m.invoke(o);

					// 不在vWhenTypes内，不验证
					if (!isIn(storeType, vWhen))
						continue;
				}
			}

			if (vNotNull && fieldValue == null)
				throw new Exception(vName + "不能为空！");

			if (f.getType().equals(String.class)) {
				if (vNotEmpty) {
					if (fieldValue == null
							|| "".equals(((String) fieldValue).trim()))
						throw new Exception(vName + "不能为空！");
				}

				if (vMaxlength > 0) {
					if (getStringActualLength((String) fieldValue) > vMaxlength)
						throw new Exception(vName + "不能超过" + vMaxlength
								+ "个字符(一半数目的中文字符)！");
				}

				if (vMinlength > 0) {
					if (getStringActualLength((String) fieldValue) < vMinlength)
						throw new Exception(vName + "不能小于" + vMinlength
								+ "个字符！");
				}

				if (vRegx != null && vRegx.length == 2) {
					if (!Pattern.compile(vRegx[0]).matcher((String) fieldValue)
							.matches()) {
						throw new Exception(vName + vRegx[1] + "！");
					}
				}
			}

			if (List.class.isAssignableFrom(f.getType())) {
				if (vNotEmptyList && CollectionUtils.isEmpty((List) fieldValue)) {
					throw new Exception(vName + "不能为空List！");
				}
			}
			
			if (vIsStoreType) {
				if (StoreType.toEnum((String) fieldValue) == null)
					throw new Exception(vName + "=" + fieldValue
							+ ",不是合法的StoreType！");
			}

		}
	}

	private static boolean isIn(String storeType, StoreType[] types) {
		for (StoreType type : types) {
			if (StoreType.toEnum(storeType) == type)
				return true;
		}

		return false;
	}

	/**
	 * 计算字符串长度，中文算两个字符，全角英文算两个字符
	 * 
	 * @param str
	 *            要计算的字符串，不可以为空
	 * @return
	 */
	private static int getStringActualLength(String str) {
		if (isBlank(str))
			return 0;

		int length = 0;
		for (int i = 0; i < str.length(); i++) {
			if (isChineseChar(str.charAt(i))) {
				length += 2;
			} else {
				if (isFullSpaceChar(str.charAt(i))) {
					length += 2;
				} else {
					length++;
				}
			}
		}
		return length;
	}

	/**
	 * 判斷字符串是否為空白 str == null true str == "" true str == "    " true
	 * 
	 * @param str
	 * @return
	 */
	private static boolean isBlank(String str) {
		if (str == null) {
			return true;
		}

		if (str.trim().length() == 0) {
			return true;
		}

		return false;
	}

	/**
	 * 判断是否为全角字符
	 * 
	 * @param ch
	 * @return
	 */
	private static boolean isFullSpaceChar(char ch) {
		return (ch >= 0xff00 && ch <= 0xffff);
	}

	/**
	 * 判断字符是不是中文字符
	 * 
	 * @param str
	 *            字符
	 * @return true 是中文字符，false 不是中文字符
	 */
	private static boolean isChineseChar(char str) {
		return (str >= 0x4e00 && str <= 0x9fbb);
	}

}
