package com.taobao.pamirs.cache.util.lru;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.taobao.pamirs.cache.util.lru.ConcurrentLRUCacheMap.LRUMapLocked;

/**
 * 线程安全LRU单元测试 <br>
 * 1. put\get\remove\clear\size <br>
 * 2. 并发测试
 * 
 * @author xiaocheng 2012-11-16
 */
public class ConcurrentLRUCacheMapTest {

	@Test
	public void testLRU() {
		ConcurrentLRUCacheMap<String, String> map = new ConcurrentLRUCacheMap<String, String>();// 默认构造器
		map = new ConcurrentLRUCacheMap<String, String>(4, 2);
		// LRU & put
		map.put("1", "value-1");
		map.put("2", "value-2");
		map.put("3", "value-3");
		map.put("4", "value-4");
		map.put("5", "value-1");

		// size
		assertThat(map.size(), is(4));// 删除eldest

		// get
		assertThat(map.get("1"), equalTo("value-1"));

		// remove
		map.remove("1");
		assertThat(map.get("1"), nullValue());

		// clear
		map.clear();
		assertThat(map.size(), is(0));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConcurrentPutSize() throws Exception {
		// 1. 多线程put，size值
		final ConcurrentLRUCacheMap<String, String> map = new ConcurrentLRUCacheMap<String, String>(
				8, 4);
		final CountDownLatch countLatch = new CountDownLatch(10);

		class PutThread extends Thread {
			public PutThread(String name) {
				setName(name);
			}

			@Override
			public void run() {
				for (int i = 0; i < 1000; i++) {
					String keyAndValue = getName() + i;
					map.put(keyAndValue, keyAndValue);
				}
				countLatch.countDown();
			}
		}

		for (int i = 0; i < 10; i++) {
			PutThread t = new PutThread("thread-" + i);
			t.start();
			t.join();
		}

		countLatch.await();

		System.out.println(map.size());
		assertThat(map.size() <= 8, is(true));

		// 2. 并发，验证值有没串掉
		Field field = map.getClass().getDeclaredField("segments");

		// 去除final/static
		if (!Modifier.isFinal(field.getModifiers())
				&& !Modifier.isStatic(field.getModifiers())) {

			// make accessible
			if (!Modifier.isPublic(field.getModifiers())
					|| Modifier.isPublic(field.getDeclaringClass()
							.getModifiers())) {
				field.setAccessible(true);
			}

			LRUMapLocked<String, SoftReference<String>, String>[] segments = ((LRUMapLocked<String, SoftReference<String>, String>[]) field
					.get(map));

			for (LRUMapLocked<String, SoftReference<String>, String> lruMapLocked : segments) {

				List<String> keys = new ArrayList<String>();

				Iterator<String> it = lruMapLocked.keySet().iterator();
				while (it.hasNext())
					keys.add(it.next());

				for (String key : keys)
					assertThat(lruMapLocked.get(key).get(), equalTo(key));//
			}

		}

	}
}
