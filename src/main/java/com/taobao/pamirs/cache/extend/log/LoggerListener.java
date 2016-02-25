package com.taobao.pamirs.cache.extend.log;

import static com.taobao.pamirs.cache.util.CacheCodeUtil.parameterTypesToString;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.pamirs.cache.framework.listener.CacheOprateInfo;
import com.taobao.pamirs.cache.framework.listener.CacheOprateListener;
import com.taobao.pamirs.cache.framework.listener.CacheOprator;

/**
 * 打印给xray统计
 * 
 * @author xiaocheng 2012-11-13
 */
public class LoggerListener implements CacheOprateListener {

	private static Logger logger=LoggerFactory.getLogger(LoggerListener.class);
	private String beanName;
	private String methodName;
	private List<Class<?>> parameterTypes;

	/** 关键字 */
	private static final String XRAY_KEYWORD = "PAMIRS_CACHE";
	private static final String SEPARATOR = ",";

	public LoggerListener(String beanName, String methodName,
			List<Class<?>> parameterTypes) {
		this.beanName = beanName;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
	}

	@Override
	public void oprate(CacheOprator oprator, CacheOprateInfo cacheInfo) {
		logger.info(getXrayLog(oprator, cacheInfo.isHitting(),
				cacheInfo.getMethodTime(), cacheInfo.getIp(),
				cacheInfo.getKey()));
	}

	/**
	 * 日志格式
	 * 
	 * @param type
	 * @param isHit
	 * @param useTime
	 * @param ip
	 * @param key
	 * @return
	 */
	private String getXrayLog(CacheOprator type, boolean isHit, long useTime,
			String ip, Serializable key) {
		StringBuilder sb = new StringBuilder();
		sb.append(XRAY_KEYWORD);
		//sb.append(SEPARATOR).append(ip);
		sb.append(SEPARATOR).append(beanName);
		sb.append(SEPARATOR).append(methodName);
		sb.append(SEPARATOR).append(parameterTypesToString(parameterTypes));
		sb.append(SEPARATOR).append(type.name());
		sb.append(SEPARATOR).append(isHit);
		sb.append(SEPARATOR).append(useTime);
		sb.append(SEPARATOR).append(key);

		return sb.toString();
	}

}
