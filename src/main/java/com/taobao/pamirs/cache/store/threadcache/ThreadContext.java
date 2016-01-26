package com.taobao.pamirs.cache.store.threadcache;

import java.util.HashMap;
import java.util.Map;

/**
 * 线程缓存上下文
 * 
 * @author xiaocheng 2012-9-3
 */
public class ThreadContext {

	private static ThreadLocal<Map<String, Object>> context = new ThreadLocal<Map<String, Object>>();

	public static void startLocalCache() {
		context.set(new HashMap<String, Object>());
	}

	public static void put(String key, Object value) {
		Map<String, Object> map = context.get();
		if (map != null) {
			map.put(key, value);
		}
	}

	public static Object get(String key) {
		Map<String, Object> map = context.get();
		if (map != null) {
			return map.get(key);
		}

		return null;
	}

	public static void remove() {
		context.remove();
	}

}
