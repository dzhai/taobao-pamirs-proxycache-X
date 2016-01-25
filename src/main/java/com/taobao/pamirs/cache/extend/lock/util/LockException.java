package com.taobao.pamirs.cache.extend.lock.util;

/**
 * 并发控制锁异常
 * 
 * @author xiaocheng Sep 29, 2015
 */
public class LockException extends Exception {

	private static final long serialVersionUID = 1L;

	public LockException() {
		super();
	}

	public LockException(String msg) {
		super(msg);
	}

	public LockException(Throwable cause) {
		super(cause);
	}

	public LockException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
