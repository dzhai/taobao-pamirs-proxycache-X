package com.taobao.pamirs.cache.load.testbean;

/**
 * 测试BeanA（测试重载方法、clear操作、expire操作）
 * 
 * @author xiaocheng
 */
public interface ASerivce {

	String md5Name(User user);

	String md5Name(String name, String key);
	
	String clearNames(String name, String key);

	/**
	 * 第一次返回值，第二次调用就会返回null了
	 * 
	 * @param key
	 * @return
	 */
	String firstHaveValue(String key);
	
	/**
	 * 验证没有重名方法时，配置不需要写参数
	 * 
	 * @param arg
	 * @return
	 */
	String noRewirteMethod(String arg);
	
	/**
	 * 测试内部调用inner方法解决方案
	 * @return
	 */
	String testInner(boolean aopInner);
	String inner();
}
