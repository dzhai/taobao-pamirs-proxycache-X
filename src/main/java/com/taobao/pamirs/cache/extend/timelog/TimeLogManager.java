package com.taobao.pamirs.cache.extend.timelog;

import java.util.LinkedList;

import org.apache.commons.logging.LogFactory;

import com.taobao.pamirs.cache.util.asynlog.AsynWriter;

/**
 * threadlocal 方式保存日志记录，支持方法串联顺序打印<BR>
 * 本身非线程安全，由threadlocal保证单线程
 * 
 * @author xiaocheng 2012-8-24
 */
public class TimeLogManager {

	// 采用异步log打印
	private static final AsynWriter<String> log = new AsynWriter<String>(
			LogFactory.getLog(TimeHandle.class));

	private int count = 0;// 计数器

	private int max = 0;

	private LinkedList<String> methodsLog = new LinkedList<String>();

	public int getCount() {
		return count;
	}

	public void setCount(int num) {
		this.count = num;
		if (max < count)
			max = count;
	}

	public int getMax() {
		return max;
	}

	public LinkedList<String> getMethods() {
		return methodsLog;
	}

	public void setMethods(LinkedList<String> methods) {
		this.methodsLog = methods;
	}

	private void resume() {
		count = 0;
		methodsLog.clear();
	}

	private static final String LINE_SEPARATOR = System
			.getProperty("line.separator");
	private static final String TABLE_BLANK = "\t";
	private static final String DELETED = "&deleted&";
	private static final String SEPARATERS = "===================================";

	//
	private static final ThreadLocal<TimeLogManager> tlm = new ThreadLocal<TimeLogManager>();

	public static void remove() {
		TimeLogManager manager = tlm.get();
		if (manager != null)
			tlm.get().resume();
		tlm.remove();
	}

	/**
	 * 开始打印标识
	 * 
	 */
	public static void addCount() {
		TimeLogManager manager = tlm.get();
		if (manager == null) {
			manager = new TimeLogManager();
			tlm.set(manager);
		}

		manager.setCount(manager.getCount() + 1);
	}

	/**
	 * 加入方法日志，会做判断：只有当所有子方法都打印完成后，才真正打印
	 * 
	 * @param logInfo
	 */
	public static void addLogInfo(String logInfo) {
		TimeLogManager manager = tlm.get();
		int count = manager.getCount();
		int max = manager.getMax();
		LinkedList<String> methods = manager.getMethods();

		StringBuilder sb = new StringBuilder();
		sb.append(count);
		for (int i = 0; i < count; i++) {
			sb.append(TABLE_BLANK);
		}
		sb.append(logInfo);

		methods.add(sb.toString());

		// 还有子方法未执行完，暂且不打印
		if (manager.getCount() > 1) {
			manager.setCount(manager.getCount() - 1);
			return;
		}

		// 开始打印
		while (methods.size() > 1) {
			int size = methods.size();

			int start = -1;
			int end = -1;

			int deleteTimes = 0;

			for (int index = size - 1; index >= 0; index--) {// 倒序
				String info = methods.get(index);
				int prefix = Integer.valueOf(info.substring(0,
						info.indexOf(TABLE_BLANK)));

				// 如果不是当前统计的
				if (prefix != max) {

					if (start != -1) {
						deleteTimes = mergeInfo(methods, start, end,
								deleteTimes);
					}

					start = -1;
					end = -1;
					continue;
				}

				if (start == -1)
					start = index;

				end = index;
			}

			if (start != -1) {
				deleteTimes = mergeInfo(methods, start, end, deleteTimes);
			}

			// 删除
			for (int i = 0; i < deleteTimes; i++) {
				methods.remove(DELETED);
			}

			max--;
		}

		sb = new StringBuilder();
		sb.append(LINE_SEPARATOR);
		sb.append(SEPARATERS);
		sb.append(LINE_SEPARATOR);
		sb.append(deletePrefix(methods.peekFirst().toString()));
		sb.append(LINE_SEPARATOR);
		sb.append(SEPARATERS);
		sb.append(LINE_SEPARATOR);

		log.write(sb.toString());
		manager.resume();
		tlm.remove();
	}

	/**
	 * 合并相邻的同级数据
	 */
	private static int mergeInfo(LinkedList<String> methods, int start,
			int end, int deleteTimes) {
		// 处理之前的数据
		StringBuilder tmp = new StringBuilder(methods.get(start + 1));

		for (int i = end; i <= start; i++) {
			tmp.append(LINE_SEPARATOR).append(deletePrefix(methods.get(i)));
			methods.set(i, DELETED);
			deleteTimes++;
		}

		methods.set(start + 1, tmp.toString());
		return deleteTimes;
	}

	private static String deletePrefix(String source) {
		return source.substring(source.indexOf(TABLE_BLANK));
	}

}
