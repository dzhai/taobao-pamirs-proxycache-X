package com.taobao.pamirs.cache.util.lru;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Iterator;

import org.junit.Test;

/**
 * LRU单元测试
 * 
 * @author xiaocheng 2012-11-16
 */
public class LRUMapTest {

	@Test
	public void testLRU() {
		LRUMap<String, String> map = new LRUMap<String, String>(3);
		map.put("1", "value-1");
		map.put("2", "value-2");
		map.put("3", "value-3");
		map.put("4", "value-4");

		// 删除eldest
		assertThat(map.keySet().size(), is(3));
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String v = it.next();
			System.out.println(v);// FIFO
			assertThat("2,3,4".indexOf(v) != -1, is(true));
		}

		map.get("2");
		map.put("1", "must exit");

		// 删除eldest,此时eldest key应该是3
		assertThat(map.keySet().size(), is(3));
		it = map.keySet().iterator();
		while (it.hasNext()) {
			String v = it.next();
			System.out.println(v);
			assertThat("4,2,1".indexOf(v) != -1, is(true));
		}
	}

}
