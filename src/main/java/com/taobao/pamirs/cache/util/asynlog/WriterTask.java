package com.taobao.pamirs.cache.util.asynlog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 日志写任务
 * 
 * @author xiaocheng 2012-11-9
 */
public class WriterTask<T> implements Runnable {

	private static final Log log = LogFactory.getLog(AsynWriter.class);

	/**
	 * 日志队列
	 */
	private BlockingQueue<T> logQueue;

	/**
	 * 配置对象
	 */
	private LogConfig config;

	/**
	 * 开关
	 */
	private volatile boolean activeFlag = true;

	private List<T> records = new ArrayList<T>();

	private long timestamp = System.currentTimeMillis();

	@Override
	public void run() {

		try {
			while (activeFlag) {

				// 记录满
				if (records.size() >= config.getRecordsMaxSize()) {
					flush();
				}

				// 定时
				if (records.size() > 0
						&& System.currentTimeMillis() >= (timestamp + config
								.getFlushInterval() * 1000L)) {
					flush();
				}

				T r = logQueue.poll(100, TimeUnit.MILLISECONDS);
				if (r != null)
					records.add(r);
			}
		} catch (Exception e) {
			log.error("【严重】日志任务失败!", e);
		}

	}

	private void flush() {
		Log logWriter = config.getLog();
		if (logWriter == null)
			logWriter = log;

		for (T r : records) {
			if (logWriter.isWarnEnabled()) {
				logWriter.warn(r);
			}
		}

		records.clear();
		timestamp = System.currentTimeMillis();
	}

	public BlockingQueue<T> getLogQueue() {
		return logQueue;
	}

	public void setLogQueue(BlockingQueue<T> logQueue) {
		this.logQueue = logQueue;
	}

	public boolean isActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
	}

	public LogConfig getConfig() {
		return config;
	}

	public void setConfig(LogConfig config) {
		this.config = config;
	}

}
