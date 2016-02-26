package com.taobao.pamirs.cache.utils.redis;

import java.util.Set;

public interface IRedisUtils {

	Object get(byte[] key, int db);

	boolean set(byte[] key, byte[] value, int expiresTime, int db);

	boolean del(String key, int db);

	boolean del(Set<String> keys, int db);

	Set<String> keys(String pattern, int db);

	boolean exists(String key, int db);

}
