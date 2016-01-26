package com.taobao.pamirs.cache.extend.lock.impl;

import static com.taobao.pamirs.cache.extend.lock.util.LockUtil.combineKey;
import static com.taobao.pamirs.cache.extend.lock.util.LockUtil.isTairTimeout;
import static com.taobao.pamirs.cache.extend.lock.util.LockXrayLog.write;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.taobao.pamirs.cache.extend.lock.PessimisticLock;
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
public class TairPessimisticLock implements PessimisticLock {

	private static final Logger log = getLogger(PessimisticLock.class);

	private TairManager tairManager;
	private Integer namespace;
	private String region;

	private static final int CUR_VALUE = 1;

	private static final String LOCK = "PESSIMISTIC_LOCK";
	private static final String UNLOCK = "PESSIMISTIC_UNLOCK";

	@Override
	public boolean lock(long objType, String objId) {
		return this.lock(objType, objId, DEFAULT_EXPIRE_SECONDS);
	}

	@Override
	public boolean lock(long objType, String objId, int es) {
		long start = System.currentTimeMillis();
		String key = combineKey(objType, objId, region);
		boolean success = false;
		Result<Integer> incr = null;

		try {
			// 查询一次，避免一直put不释放
			Result<DataEntry> data = tairManager.get(namespace, key);

			if (ResultCode.DATANOTEXSITS.equals(data.getRc())) {
				incr = tairManager.incr(namespace, key, CUR_VALUE, 0, es);// incr没有key数据时会创建
				if (isTairTimeout(incr.getRc()))// 超时自动重试一次
					incr = tairManager.incr(namespace, key, CUR_VALUE, 0, es);

				// 获取锁
				if (ResultCode.SUCCESS.equals(incr.getRc())
						&& incr.getValue() != null
						&& incr.getValue().intValue() == CUR_VALUE)
					success = true;
			}
		} catch (Throwable e) {
			log.error("Get Lock Fail!", e);
		}

		long end = System.currentTimeMillis();
		write(LOCK, end - start, success, objType, objId, es, incr, null);

		return success;
	}

	@Override
	public boolean unlock(long objType, String objId) {
		long start = System.currentTimeMillis();
		boolean success = false;
		ResultCode rc = null;

		try {
			rc = tairManager.delete(namespace,
					combineKey(objType, objId, region));

			if (isTairTimeout(rc))// 超时自动重试一次
				rc = tairManager.delete(namespace,
						combineKey(objType, objId, region));

			success = rc.isSuccess();
		} catch (Throwable e) {
			log.error("Release Lock Fail!", e);
		}

		long end = System.currentTimeMillis();
		write(UNLOCK, end - start, success, objType, objId, 0, null, rc);

		return success;
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
