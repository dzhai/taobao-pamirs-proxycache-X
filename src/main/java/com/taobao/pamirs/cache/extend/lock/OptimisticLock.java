package com.taobao.pamirs.cache.extend.lock;

import com.taobao.pamirs.cache.extend.lock.util.LockException;

/**
 * 分布式乐观锁
 * 
 * <pre>
 * try {
 * 	long lockVersion = optimisticLock.getLockVersion(1L, &quot;abc&quot;);
 * 	// do something
 * 	optimisticLock.freeLock(1L, &quot;abc&quot;, lockVersion);
 * } catch (LockException e) {
 * 	// 锁失败处理
 * }
 * </pre>
 * 
 * @author xiaocheng Sep 29, 2015
 */
public interface OptimisticLock {

	/**
	 * 获取分布式锁版本
	 * 
	 * @param objType
	 * @param objId
	 * @return 当前锁版本号
	 * @throws LockException
	 */
	int getLockVersion(long objType, String objId) throws LockException;

	/**
	 * 释放资源实例锁，如果version已经变化，释放失败抛异常
	 * 
	 * @param objType
	 * @param objId
	 * @param lockVersion
	 * @throws LockException
	 */
	void freeLock(long objType, String objId, int lockVersion)
			throws LockException;

}
