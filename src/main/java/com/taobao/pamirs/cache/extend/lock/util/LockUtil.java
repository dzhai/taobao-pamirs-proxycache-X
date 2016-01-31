package com.taobao.pamirs.cache.extend.lock.util;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * 组装Key辅助类
 * 
 * @author xiaocheng Sep 29, 2015
 */
public class LockUtil {

	/**
	 * 组装生成key
	 */
	public static String combineKey(long objType, String objId, String region) {
		StringBuilder sb = new StringBuilder();
		if (isNotEmpty(region))
			sb.append(region).append("@");

		sb.append(objType).append("$").append(objId);
		return sb.toString();
	}

}
