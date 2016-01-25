package com.taobao.pamirs.cache.extend.lock.impl;

import static com.taobao.pamirs.cache.extend.lock.util.LockUtil.combineKey;
import static com.taobao.pamirs.cache.extend.lock.util.LockUtil.isTairTimeout;
import static com.taobao.pamirs.cache.extend.lock.util.LockXrayLog.write;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.taobao.pamirs.cache.extend.lock.OptimisticLock;
import com.taobao.pamirs.cache.extend.lock.util.LockException;
import com.taobao.pamirs.cache.extend.timelog.annotation.TimeLog;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;
import com.taobao.tair.TairManager;

/**
 * Tair实现的分布式悲观锁锁
 * 
 * @author xiaocheng Aug 17, 2015
 */
@TimeLog
public class TairOptimisticLock implements OptimisticLock {

	private static final Logger log = getLogger(OptimisticLock.class);
	private static final int es = 3;// 默认锁过期时间，单位：秒

	private TairManager tairManager;
	private Integer namespace;
	private String region;

	private static String VALUE = "pamirs lock";
	private static final String LOCK = "OPTIMISTIC_LOCK";
	private static final String UNLOCK = "OPTIMISTIC_UNLOCK";

	@Override
	public int getLockVersion(long objType, String objId) throws LockException {
		long start = System.currentTimeMillis();
		String key = combineKey(objType, objId, region);
		int lockVersion = 0;
		Result<DataEntry> r = null;
		ResultCode put = null;

		try {
			r = tairManager.get(namespace, key);
			if (isTairTimeout(r.getRc()))// 超时自动重试一次
				r = tairManager.get(namespace, key);

			// Tair没有数据时，创建
			if (ResultCode.DATANOTEXSITS.equals(r.getRc())) {
				put = tairManager.put(namespace, key, VALUE, 2, es);
				if (isTairTimeout(put))// 超时自动重试一次
					put = tairManager.put(namespace, key, VALUE, 2, es);

				if (ResultCode.SUCCESS.equals(put))
					lockVersion = 1;// 这里初始版本一定是1
			} else if (ResultCode.SUCCESS.equals(r.getRc())
					&& r.getValue() != null) {
				lockVersion = r.getValue().getVersion();
			}
		} catch (Throwable e) {
			log.error("Get Lock Version Fail!", e);
		}

		boolean success = (lockVersion != 0);

		long end = System.currentTimeMillis();
		write(LOCK, end - start, success, objType, objId, es, r, put);

		// 返回
		if (success)
			return lockVersion;
		else
			throw new LockException("获取并发锁失败: type=" + objType + ",id=" + objId);
	}

	@Override
	public void freeLock(long objType, String objId, int lockVersion)
			throws LockException {
		if (lockVersion == 0)
			throw new LockException("Tair并发锁版本号不能为0，会导致所有都成功!");

		long start = System.currentTimeMillis();
		String key = combineKey(objType, objId, region);
		ResultCode put = null;

		try {
			put = tairManager.put(namespace, key, VALUE, lockVersion, es);

			if (isTairTimeout(put))// 超时自动重试一次
				put = tairManager.put(namespace, key, VALUE, lockVersion, es);

		} catch (Throwable e) {
			log.error("Free Lock Fail!", e);
		}

		boolean success = ResultCode.SUCCESS.equals(put);

		long end = System.currentTimeMillis();
		write(UNLOCK, end - start, success, objType, objId, es, null, put);

		if (!success)
			throw new LockException("并发锁版本号失效: type=" + objType + ",id="
					+ objId + ",v=" + lockVersion);
	}

	public void setTairManager(TairManager tairManager) {
		this.tairManager = tairManager;
	}

	public void setNamespace(Integer namespace) {
		this.namespace = namespace;
	}

	public void setRegion(String region) {
		this.region = region;
	}

}
