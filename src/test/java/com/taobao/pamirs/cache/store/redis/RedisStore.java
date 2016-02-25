package com.taobao.pamirs.cache.store.redis;

import java.io.Serializable;
import java.util.Set;

import com.taobao.pamirs.cache.framework.ICache;

public class RedisStore<K extends Serializable, V extends Serializable> implements ICache<K, V> {
	
	private Integer nameSpace;
	
	private String storeRegion;
	
	
	public RedisStore(){

	}

	public RedisStore(Integer nameSpace,String storeRegion){
		this.nameSpace=nameSpace;
		this.storeRegion=storeRegion;
	}
	

	
	@SuppressWarnings("unchecked")
	@Override
	public V get(K key) {
		return null;
		
	}

	@Override
	public void put(K key, V value) {
		
	}

	@Override
	public void put(K key, V value, int expireTime) {
		

	}

	@Override
	public void remove(K key) {
		

	}

	/**
	 * 不建议使用 实现逻辑不好
	 */
	@Override
	public void clear() {
		
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


	private String convertKey(Object key){

		
		return key.toString();
	}
	
	
	@Override
	public void removeByReg(K prefixKey) {
			
	}

	public void setNameSpace(Integer nameSpace) {
		this.nameSpace = nameSpace;
	}

	public void setStoreRegion(String storeRegion) {
		this.storeRegion = storeRegion;
	}
	

}
