package com.taobao.pamirs.cache.framework.timer;

import java.io.Serializable;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.pamirs.cache.framework.CacheProxy;

/**
 * 缓存清理Timer任务
 * 
 * @author xiaocheng 2012-11-8
 */
public class CleanCacheTimerManager {

	private static final Log log = LogFactory.getLog(CleanCacheTimerManager.class);

	private Timer timer;

	public CleanCacheTimerManager() {
		timer = new Timer("CleanCacheTimerManager", false);// 守护进程
	}

	public void createCleanCacheTask(
			final CacheProxy<Serializable, Serializable> cache,
			final String aCronTabExpress) throws Exception {

		CronExpression cexp = new CronExpression(aCronTabExpress);
		Date nextTime = cexp.getNextValidTimeAfter(new Date(System
				.currentTimeMillis()));

		CleanCacheTask task = new CleanCacheTask(cache, new CleanNotice() {

			@Override
			public void cleaned(CleanCacheTask task) {
				// 1. 确保废弃自身
				task.cancel();

				// 2. 创建一个新的（因为要支持自定义的调度表达式CronExpression）
				try {
					CleanCacheTimerManager.this.createCleanCacheTask(cache,
							aCronTabExpress);
				} catch (Exception e) {
					log.fatal("严重错误，定时器处理失败：" + e.getMessage(), e);
				}

			}
		});

		this.timer.schedule(task, nextTime);
	}

	/**
	 * 清理完成后通知
	 * 
	 * @author xiaocheng 2012-11-8
	 */
	interface CleanNotice {
		void cleaned(CleanCacheTask task);
	}

	/**
	 * 清理Task
	 * 
	 * @author xiaocheng 2012-11-8
	 */
	class CleanCacheTask extends TimerTask {

		private CacheProxy<Serializable, Serializable> cache;

		/** 清理后通知 */
		private CleanNotice notice;

		public CleanCacheTask(CacheProxy<Serializable, Serializable> cache,
				CleanNotice notice) {
			this.cache = cache;
			this.notice = notice;
		}

		@Override
		public void run() {
			try {
				cache.clear();
			} catch (Exception e) {
				log.error("清理Map失败", e);
			} finally {
				notice.cleaned(this);
			}
		}

	}
}
