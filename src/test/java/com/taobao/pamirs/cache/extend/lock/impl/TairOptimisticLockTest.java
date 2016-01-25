package com.taobao.pamirs.cache.extend.lock.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.taobao.pamirs.cache.extend.lock.OptimisticLock;
import com.taobao.pamirs.cache.extend.lock.util.LockException;

@SpringApplicationContext({ "/store/tair-store.xml",
		"/extend/lock/lock-tair.xml" })
public class TairOptimisticLockTest extends UnitilsJUnit4 {

	@SpringBeanByName
	OptimisticLock optimisticLock;

	@Test
	public void test() {
		try {
			int lockVersion = optimisticLock.getLockVersion(1, "abc");
			optimisticLock.freeLock(1, "abc", lockVersion);
		} catch (LockException e) {
			assertThat(false, is(true));// not run here
		}

		try {
			int lockVersion1 = optimisticLock.getLockVersion(1, "abc");
			int lockVersion2 = optimisticLock.getLockVersion(1, "abc");
			optimisticLock.freeLock(1, "abc", lockVersion1);
			optimisticLock.freeLock(1, "abc", lockVersion2);

			assertThat(false, is(true));// not run here, must 1 fail
		} catch (LockException e) {
		}
	}

}
