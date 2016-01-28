package com.taobao.pamirs.cache.store.tair;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByName;

import com.taobao.tair.TairManager;

/**
 * tair store单元测试
 * 
 * @author xiaocheng 2012-11-19
 */
@SpringApplicationContext({ "/store/tair-store.xml" })
public class TairStoreTest extends UnitilsJUnit4 {

	@SpringBeanByName
	TairManager tairManager;
	TairStore<String, String> store;

	@Before
	public void init() {
		if (store == null)
			store = new TairStore<String, String>(tairManager, 318);
	}

	@Test
	public void testPutAndGet() {
		String key = "123";
		String value = "jeck";

		assertThat(store.get(key), nullValue());

		store.put(key, value);
		assertThat(store.get(key), is(value));

		// 清理测试数据
		store.remove(key);
	}

	@Test
	public void testPutAndExpireTime() throws Exception {
		String key = "999";
		String value = "expire jeck";

		store.put(key, value, 3);
		assertThat(store.get(key), is(value));

		Thread.sleep(5000);
		assertThat(store.get(key), equalTo(null));

		// 清理测试数据
		store.remove(key);
	}

	@Test
	public void testRemove() {
		String key = "remove";
		String value = "remove jeck";

		store.put(key, value);
		assertThat(store.get(key), is(value));

		store.remove(key);
		assertThat(store.get(key), nullValue());
	}

	@Test
	public void testClear() {
		try {
			store.clear();
			assertThat("不应该运行到此", true, is(false));
		} catch (Exception e) {
			assertThat(e instanceof RuntimeException, is(true));
		}
	}

	@Test
	public void testSize() {
		try {
			store.size();
			assertThat("不应该运行到此", true, is(false));
		} catch (Exception e) {
			assertThat(e instanceof RuntimeException, is(true));
		}
	}

}
