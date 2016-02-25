package com.taobao.pamirs.cache.store.map;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.taobao.pamirs.cache.framework.ICache;

public class MapStore<K extends Serializable, V extends Serializable> implements ICache<K, V> {

	private Map<K, V> cache = new ConcurrentHashMap<K, V>();

	@Override
	public V get(K key) {
		return cache.get(key);
	}

	@Override
	public void put(K key, V value) {
		cache.put(key, value);
	}

	@Override
	public void put(K key, V value, int expireTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeByReg(K regKey) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(K key) {
		cache.remove(key);

	}

	@Override
	public void clear() {
		cache.clear();

	}

	@Override
	public void invalidBefore() {
		// TODO Auto-generated method stub

	}

	@Override
	public int size() {
		return cache.size();
	}

}
