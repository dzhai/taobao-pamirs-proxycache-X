package com.taobao.pamirs.cache.load.impl;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.taobao.pamirs.cache.load.impl.LocalConfigCacheManager;
import com.taobao.pamirs.cache.load.testbean.ASerivce;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/applicationContext.xml" })
public class LocalConfigServiceImplTest {

	@Resource
	private LocalConfigCacheManager cacheManager;
	@Resource
	private ASerivce aService;

	@Test
	public void testGetCacheProxy() {

//		System.out.println(aService.md5Name("md5Name"));
//		System.out.println(aService.md5Name("md5Name"));
		System.out.println(aService.md5Name("md5Name"));
		System.out.println(aService.md5Name("zhangsanfeng","lisi"));
//		System.out.println(aService.md5Name("md5Name","2"));
//		
//		System.out.println(aService.firstHaveValue("firstHaveValue"));
//		System.out.println(aService.firstHaveValue("firstHaveValue"));
//		
//		System.out.println(aService.noRewirteMethod("noRewirteMethod"));
//		System.out.println(aService.noRewirteMethod("noRewirteMethod"));
//		
//		System.out.println(aService.inner());
//		System.out.println(aService.inner());
//		
		aService.clearNames("zhangsanfeng", "lisi");
//		
		System.out.println(aService.md5Name("md5Name"));
//		System.out.println(aService.md5Name("md5Name","2"));
//		System.out.println(aService.firstHaveValue("firstHaveValue"));
//		System.out.println(aService.noRewirteMethod("noRewirteMethod"));
//		System.out.println(aService.inner());
		
	}

}
