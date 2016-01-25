package com.taobao.pamirs.cache.extend.lock.impl;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.taobao.pamirs.cache.extend.lock.PessimisticLock;

@SpringApplicationContext({ "/store/tair-store.xml",
		"/extend/lock/lock-tair.xml" })
public class TairPessimisticLockTest extends UnitilsJUnit4 {

	@SpringBeanByName
	PessimisticLock pessimisticLock;

	@Test
	public void test() {
		boolean lock = pessimisticLock.lock(1, "a");
		assertThat(lock, is(true));

		lock = pessimisticLock.lock(1, "a");
		assertThat(lock, is(false));

		boolean unLock = pessimisticLock.unlock(1, "a");
		assertThat(unLock, is(true));

		lock = pessimisticLock.lock(1, "a");
		assertThat(lock, is(true));

		unLock = pessimisticLock.unlock(1, "a");
		assertThat(unLock, is(true));
	}

}
