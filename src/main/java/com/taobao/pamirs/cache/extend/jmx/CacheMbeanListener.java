package com.taobao.pamirs.cache.extend.jmx;

import com.taobao.pamirs.cache.framework.listener.DefaultStatisticInfoListener;

/**
 * Cache Mbean信息处理计数类
 * 
 * @author xiaocheng 2012-11-7
 */
public class CacheMbeanListener extends DefaultStatisticInfoListener {

	public CacheMbeanListener() {
		setTimeWindow(24 * 60 * 60 * 1000L);// 1天
	}
	
}
