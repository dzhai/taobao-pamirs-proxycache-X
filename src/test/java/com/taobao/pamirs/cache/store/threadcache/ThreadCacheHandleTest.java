package com.taobao.pamirs.cache.store.threadcache;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.pamirs.cache.store.threadcache.beans.BeanHaveInterface;
import com.taobao.pamirs.cache.store.threadcache.beans.BeanNormal;
import com.taobao.pamirs.cache.store.threadcache.beans.BeanVarietyArgs;
import com.taobao.pamirs.cache.store.threadcache.beans.Ibean;
import com.taobao.pamirs.cache.util.lru.ConcurrentLRUCacheMap;

/**
 * threadcache测试
 * 
 * @author xiaocheng 2012-9-6
 */
public class ThreadCacheHandleTest {

	private static ApplicationContext context;

	private static BeanNormal beanNormal;
	private static BeanHaveInterface beanHaveInterface;
	private static Ibean beanFinal;
	private static BeanVarietyArgs beanVarietyArgs;
	private static ThreadCacheHandle threadCacheHandle;

	@BeforeClass
	public static void init() {
		context = new ClassPathXmlApplicationContext(
				new String[] { "store/bean-threadcache.xml" });

		threadCacheHandle = (ThreadCacheHandle) context.getBean("threadCacheHandle");
		beanNormal = (BeanNormal) context.getBean("beanNormal");
		beanHaveInterface = (BeanHaveInterface) context.getBean("beanHaveInterface");
		beanFinal = (Ibean) context.getBean("beanFinal");
		beanVarietyArgs = (BeanVarietyArgs) context.getBean("beanVarietyArgs");
	}

	@Test
	public void testSignleThread() {
		ThreadContext.startLocalCache();
		
		Date date = new Date();
		
		try {
			for (int i = 0; i < 10; i++) {
				beanNormal.sayHello();
				System.out.println("Call: " + beanNormal.getName());
				
				beanHaveInterface.sayHello();
				System.out.println("Call: " + beanHaveInterface.getName());
				
				beanFinal.sayHello();
				System.out.println("Call: " + beanFinal.getName());
				
				
				beanVarietyArgs.sayHelloPrimitive(true, 'a', (byte)1, (short)2, 3, 4L, 5.01F, 6.02D);
				beanVarietyArgs.sayHelloBox(true, 'a', (byte)1, (short)2, 3, 4L, 5.01F, 6.02D, date);
				beanVarietyArgs.sayHelloObject(new HashMap<Object, Object>());
			}
		} finally {
			ThreadContext.remove();
		}
		
		ConcurrentLRUCacheMap<String,AtomicLong> logDetailInfo = threadCacheHandle.getLogDetailInfo();
		assertThat(logDetailInfo.get("beanNormal#getName{..}").longValue(), equalTo(9L));
		assertThat(logDetailInfo.get("beanHaveInterface#getName{..}").longValue(), equalTo(9L));
		assertThat(logDetailInfo.get("beanFinal#getName{..}").longValue(), equalTo(9L));
		assertThat(logDetailInfo.get("beanVarietyArgs#sayHelloPrimitive{..}true@@a@@1@@2@@3@@4@@5.01@@6.02").longValue(), equalTo(9L));
		assertThat(logDetailInfo.get("beanVarietyArgs#sayHelloBox{..}true@@a@@1@@2@@3@@4@@5.01@@6.02@@" + date.toString()).longValue(), equalTo(9L));
		// 不支持void方法
		assertThat(logDetailInfo.get("beanNormal#sayHello{..}"), nullValue());
		assertThat(logDetailInfo.get("beanHaveInterface#sayHello{..}"), nullValue());
		assertThat(logDetailInfo.get("beanFinal#sayHello{..}"), nullValue());
		// 不支持的参数类型
		assertThat(logDetailInfo.get("beanVarietyArgs#sayHelloObject{..}"), nullValue());
		
	}
	
	class MyThread extends Thread {
		@Override
		public void run() {
			super.run();
			
			for (int i = 0; i < 10; i++) {
				beanNormal.sayHello();
				System.out.println(this.getName() + " Call: " + beanNormal.getName());
			}
		}
	}
	
	@Test
	public void testSubMultiThreads() {
		ThreadContext.startLocalCache();
		
		for (int i = 0; i < 5; i++) {
			beanNormal.sayHello();
			System.out.println("Call: " + beanNormal.getName());
		}
		
		MyThread thread1 = new MyThread();
		MyThread thread2 = new MyThread();
		
		thread1.start();
		thread2.start();
		
		ThreadContext.remove();
	}

}
