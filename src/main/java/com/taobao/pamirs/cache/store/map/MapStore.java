/**
 * 
 */
package com.taobao.pamirs.cache.store.map;

import java.io.Serializable;

import com.taobao.pamirs.cache.framework.ICache;
import com.taobao.pamirs.cache.util.lru.ConcurrentLRUCacheMap;

/**
 * MapStore 使用本地 ConcurrentLRUCacheMap 作为 CacheManage 的缓存存储方案.
 * <p>
 * 
 * <pre>
 * 通过 Key-Value 的形式将对象存入 本地内存中.
 * 
 * 可以采用 PUT , PUT_EXPIRETIME , GET , REMOVE 这三种 Key 操作. 
 * 可以采用 CLEAR , CLEAN 这种范围清除操作.
 * 
 * 使用该 Store . 数据量较小. 0 ~ 1G 访问耗时极低. 
 * 适用于数据量小但变化较多的场合.
 * 
 * 例如基础型数据.
 * </pre>
 * 
 * @author xuanyu
 * @author xiaocheng 2012-11-2
 */
public class MapStore<K extends Serializable, V extends Serializable>
		implements ICache<K, V> {

	private final ConcurrentLRUCacheMap<K, ObjectBoxing<V>> datas;

	public MapStore() {
		datas = new ConcurrentLRUCacheMap<K, ObjectBoxing<V>>();
	}

	public MapStore(int size, int segmentSize) {
		datas = new ConcurrentLRUCacheMap<K, ObjectBoxing<V>>(size, segmentSize);
	}

	@Override
	public V get(K key) {
		System.out.println(key);
		ObjectBoxing<V> storeObject = datas.get(key);
		if (storeObject == null) {
			datas.remove(key);
			return null;
		}

		V v = storeObject.getObject();
		if (v == null)
			datas.remove(key);

		return v;
	}

	@Override
	public void put(K key, V value) {
		this.put(key, value, 0);
	}

	@Override
	public void put(K key, V value, int expireTime) {
		if (value == null)
			return;

		ObjectBoxing<V> storeObject = new ObjectBoxing<V>(value, expireTime);
		datas.put(key, storeObject);
	}

	@Override
	public void remove(K key) {
		datas.remove(key);
	}

	@Override
	public void clear() {
		datas.clear();
	}

	@Override
	public void invalidBefore() {
		throw new RuntimeException("NotSupport for MapCache");
	}

	@Override
	public int size() {
		return datas.size();
	}

	@Override
	public void removeByReg(K regKey) {
		datas.clear();
		System.out.println(datas.size());
		// TODO Auto-generated method stub
		System.out.println("reomveByReg "+regKey);
	}

}
