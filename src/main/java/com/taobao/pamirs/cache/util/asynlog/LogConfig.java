package com.taobao.pamirs.cache.util.asynlog;

import org.apache.commons.logging.Log;

/**
 * 异步日志配置信息
 * 
 * @author xiaocheng 2012-11-9
 */
public class LogConfig {

	/**
	 * 异步日志线程池大小
	 */
	private int asynWriterThreadSize = 3;

	/**
	 * 日志列表的最大记录数
	 */
	private int recordsMaxSize = 5000;

	/**
	 * 日志刷新时间间隔(单位:秒)
	 */
	private int flushInterval = 2;

	/**
	 * 日志LOG
	 */
	private Log log;

	public int getAsynWriterThreadSize() {
		return asynWriterThreadSize;
	}

	public void setAsynWriterThreadSize(int asynWriterThreadSize) {
		this.asynWriterThreadSize = asynWriterThreadSize;
	}

	public int getRecordsMaxSize() {
		return recordsMaxSize;
	}

	public void setRecordsMaxSize(int recordsMaxSize) {
		this.recordsMaxSize = recordsMaxSize;
	}

	public int getFlushInterval() {
		return flushInterval;
	}

	public void setFlushInterval(int flushInterval) {
		this.flushInterval = flushInterval;
	}

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

}
