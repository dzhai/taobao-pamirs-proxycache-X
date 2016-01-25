package com.taobao.pamirs.cache.store.map;

import java.io.Serializable;

/**
 * 缓存对象包装类，支持expireTime
 * 
 * @author xuanyu
 * @author xiaocheng 2012-10-30
 */
public class ObjectBoxing<V extends Serializable> implements Serializable {
	//
	private static final long serialVersionUID = 2186360043715004471L;

	private Long timestamp = new Long(System.currentTimeMillis() / 1000);

	/**
	 * 失效时间（绝对时间），单位毫秒<br>
	 * Null表示永不失效
	 */
	private Integer expireTime;

	private V value;

	public ObjectBoxing(V value) {
		this(value, null);
	}

	public ObjectBoxing(V value, Integer expireTime) {
		this.value = value;
		this.expireTime = expireTime;
	}

	public V getObject() {
		// 已经失效
		if (expireTime != null && expireTime != 0) {
			long now = System.currentTimeMillis() / 1000;

			if (timestamp.longValue() > expireTime.longValue()) {// 相对时间
				if (now >= (expireTime.longValue() + timestamp.longValue()))
					return null;
			} else {// 绝对时间
				if (now >= expireTime.longValue())
					return null;
			}
		}

		return this.value;
	}
}