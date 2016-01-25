package com.taobao.pamirs.cache.framework;

/**
 * ª∫¥Ê“Ï≥£
 * 
 * @author xiaocheng 2012-11-1
 */
public class CacheException extends RuntimeException {

	//
	private static final long serialVersionUID = 5468618571943477362L;

	private int errCode;
	private String errMsg;

	public CacheException(int errCode, String errMsg) {
		this.errCode = errCode;
		this.errMsg = errMsg;
	}

	public int getErrCode() {
		return errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

}
