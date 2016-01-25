package com.taobao.pamirs.cache.extend.lock.util;

import org.apache.commons.logging.LogFactory;

import com.taobao.pamirs.cache.util.asynlog.AsynWriter;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;

/**
 * 分布式锁xray打印
 * 
 * @author xiaocheng Sep 29, 2015
 */
public class LockXrayLog {

	private static AsynWriter<String> log = new AsynWriter<String>(
			LogFactory.getLog(LockXrayLog.class));

	private static final String SEPARATOR = ",";

	public static void write(String method, long useTime, boolean success,
			long objType, String objId, int expireSeconds, Result<?> r,
			ResultCode rc) {
		log.write(getXrayLog(method, useTime, success, objType, objId,
				expireSeconds, r, rc));
	}

	private static String getXrayLog(String method, long useTime,
			boolean success, long objType, String objId, int expireSeconds,
			Result<?> r, ResultCode rc) {
		StringBuilder sb = new StringBuilder();
		sb.append(method);
		sb.append(SEPARATOR).append(success);
		sb.append(SEPARATOR).append(useTime);

		sb.append(SEPARATOR).append(objType);
		sb.append(SEPARATOR).append(objId);
		sb.append(SEPARATOR).append(expireSeconds);

		sb.append(SEPARATOR).append(
				(r != null && r.getRc() != null) ? r.getRc().getCode() : null);

		sb.append(SEPARATOR)
				.append((r != null && r.getValue() != null) ? ((r.getValue() instanceof DataEntry) ? ((DataEntry) r
						.getValue()).getVersion() : r.getValue())
						: null);

		sb.append(SEPARATOR).append(rc == null ? null : rc.getCode());

		System.out.println(sb.toString());
		return sb.toString();
	}

}
