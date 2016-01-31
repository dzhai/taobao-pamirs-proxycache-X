package com.taobao.pamirs.cache.extend.lock.util;

import org.apache.commons.logging.LogFactory;
import com.taobao.pamirs.cache.util.asynlog.AsynWriter;

/**
 * 分布式锁xray打印
 * 
 * @author xiaocheng Sep 29, 2015
 */
public class LockXrayLog {

	private static AsynWriter<String> log = new AsynWriter<String>(
			LogFactory.getLog(LockXrayLog.class));

	private static final String SEPARATOR = ",";

	
}
