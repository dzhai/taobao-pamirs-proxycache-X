package com.taobao.pamirs.cache.store.redis;

import java.io.Serializable;
import java.util.Set;

import com.taobao.pamirs.cache.framework.ICache;
import com.taobao.pamirs.cache.utils.SerializeUtil;
import com.taobao.pamirs.cache.utils.redis.IRedisUtils;

public class RedisStore<K extends Serializable, V extends Serializable> implements ICache<K, V> {
	
	private Integer nameSpace;
	
	private String storeRegion;
	
	private IRedisUtils redis;
	
	public RedisStore(){

	}

	public RedisStore(Integer nameSpace,String storeRegion){
		this.nameSpace=nameSpace;
		this.storeRegion=storeRegion;
	}
	
	public RedisStore(IRedisUtils redis,Integer nameSpace,String storeRegion){
		this.nameSpace=nameSpace;
		this.storeRegion=storeRegion;
		this.redis=redis;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public V get(K key) {
		Object obj=redis.get(convertKey(key).getBytes(), nameSpace);
		if(obj==null){
			return null;
		}
		return (V) SerializeUtil.unserialize((byte[])obj);
	}

	@Override
	public void put(K key, V value) {
		redis.set(convertKey(key).getBytes(), SerializeUtil.serialize(value), 0, nameSpace);
	}

	@Override
	public void put(K key, V value, int expireTime) {
		redis.set(convertKey(key).getBytes(), SerializeUtil.serialize(value), expireTime, nameSpace);

	}

	@Override
	public void remove(K key) {
		redis.del(convertKey(key), nameSpace);

	}

	/**
	 * 不建议使用 实现逻辑不好
	 */
	@Override
	public void clear() {
		Set<String> keys=redis.keys(storeRegion+"*", nameSpace);
		if(keys==null || keys.size()==0){
			return ;
		}
		redis.del(keys, nameSpace);
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

	//region@prefix@cache#beanName#methodName#{String,Long}abc@@123
	private String convertKey(Object key){
//		String ckey=key.toString();
//		String [] strs=ckey.split(CacheCodeUtil.KEY_SPLITE_SIGN);
//		if(strs.length<=1){
//			return ckey;
//		}
//		StringBuffer sb=new StringBuffer();
//		for(int i=0;i<strs.length;i++){
//			sb.append(strs[i]);
//			if(i<strs.length-1){
//				sb.append(":");
//			}		
//		}		
//		return sb.toString();
		
		return key.toString();
	}
	
	
	@Override
	public void removeByReg(K prefixKey) {
		Set<String> keys=redis.keys(convertKey(prefixKey), nameSpace);
		if(keys==null || keys.size()==0){
			return ;
		}
		redis.del(keys, nameSpace);		
	}

	public void setNameSpace(Integer nameSpace) {
		this.nameSpace = nameSpace;
	}

	public void setStoreRegion(String storeRegion) {
		this.storeRegion = storeRegion;
	}
	
	public void setRedis(IRedisUtils redis) {
		this.redis = redis;
	}
}
