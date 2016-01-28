package com.taobao.pamirs.cache.store.map;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.taobao.pamirs.cache.util.lru.ConcurrentLRUCacheMapTest;

/**
 * MapStoretest<br>
 * 
 * @see ConcurrentLRUCacheMapTest
 * @author xiaocheng 2012-11-19
 */
public class MapStoreTest {

	MapStore<String, String> store = new MapStore<String, String>();

	@Test
	public void testPutAndGet() {
		String key = "123";
		String value = "jeck";

		assertThat(store.get(key), nullValue());

		store.put(key, value);
		assertThat(store.get(key), is(value));
	}

	@Test
	public void testPutAndExpireTime() throws Exception {
		String key = "999";
		String value = "expire jeck";

		store.put(key, value, 3);
		assertThat(store.get(key), is(value));

		Thread.sleep(3000);
		assertThat(store.get(key), nullValue());
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
	public void testClearAndSize() {
		String key = "clear";
		String value = "clear jeck";

		store.put(key, value);
		assertThat(store.get(key), is(value));
		assertThat(store.size() > 0, is(true));

		store.clear();
		assertThat(store.size() == 0, is(true));
	}

}
