package com.taobao.pamirs.cache.util.asynlog;

/**
 * 异步Log接口
 * 
 * @author xiaocheng 2012-11-9
 */
public interface IWriter<T> {

	/**
	 * 单个写
	 * 
	 * @param content
	 */
	public void write(T content);

}
