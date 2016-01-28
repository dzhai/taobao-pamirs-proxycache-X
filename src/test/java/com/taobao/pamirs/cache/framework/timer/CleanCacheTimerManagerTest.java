package com.taobao.pamirs.cache.framework.timer;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;

import com.taobao.pamirs.cache.framework.CacheProxy;
import com.taobao.pamirs.cache.store.StoreType;
import com.taobao.pamirs.cache.store.map.MapStore;

/**
 * 清理Timer任务测试
 * 
 * @author xiaocheng 2012-11-20
 */
public class CleanCacheTimerManagerTest {

	MapStore<Serializable, Serializable> mapStore;

	@Before
	public void init() {
		mapStore = new MapStore<Serializable, Serializable>();
	}

	@Test
	public void testTimer() throws Exception {
		for (int i = 0; i < 10; i++) {
			mapStore.put("k-" + i, "v-" + i);
		}

		// 每5秒执行一次
		String aCronTabExpress = "0,5,10,15,20,25,30,35,40,45,50,55 * * * * ? *";

		CleanCacheTimerManager timeTask = new CleanCacheTimerManager();
		timeTask.createCleanCacheTask(
				new CacheProxy<Serializable, Serializable>(StoreType.MAP, null,
						null, mapStore, null, null), aCronTabExpress);

		// 等5秒
		Thread.sleep(5000);

		for (int i = 0; i < 10; i++) {
			assertThat(mapStore.get("k-" + i), nullValue());
		}

	}

}
