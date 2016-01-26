package com.taobao.pamirs.cache.store.redis;

import java.io.Serializable;

import com.taobao.pamirs.cache.framework.ICache;

public class RedisStore<K extends Serializable, V extends Serializable> implements ICache<K, V> {

	@Override
	public V get(K key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void put(K key, V value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void put(K key, V value, int expireTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(K key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public void invalidBefore() {
		// TODO Auto-generated method stub

	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}
}
