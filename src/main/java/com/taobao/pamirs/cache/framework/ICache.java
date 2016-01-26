/**
 * 
 */
package com.taobao.pamirs.cache.framework;

import java.io.Serializable;

/**
 * 缓存支持接口
 * 
 * @author xuanyu
 * @author xiaocheng 2012-10-30
 */
public interface ICache<K extends Serializable, V extends Serializable> {

	/**
	 * 获取数据
	 * 
	 * @param key
	 * @return
	 */
	public V get(K key);

	/**
	 * 设置数据，如果数据已经存在，则覆盖，如果不存在，则新增
	 * 
	 * @param key
	 * @param value
	 */
	public void put(K key, V value);

	/**
	 * 设置数据，如果数据已经存在，则覆盖，如果不存在，则新增
	 * 
	 * @param key
	 * @param value
	 * @param expireTime
	 *            数据的有效时间（绝对时间），单位毫秒
	 */
	public void put(K key, V value, int expireTime);

	/**
	 * 删除key对应的数据
	 * 
	 * @param key
	 */
	public void remove(K key);
	
	/**
	 * 清除所有的数据【针对LocalMap】
	 */
	public void clear();
	
	/**
	 * 失效之前存储缓存【针对Tair】
	 */
	public void invalidBefore();

	/**
	 * 获取缓存数据量
	 * 
	 * @return
	 */
	public int size();
}
