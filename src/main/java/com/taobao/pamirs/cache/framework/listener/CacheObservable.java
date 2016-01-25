package com.taobao.pamirs.cache.framework.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ª∫¥Êπ€≤Ï’ﬂ
 * 
 * @author xiaocheng 2012-10-31
 */
public class CacheObservable {

	private Lock lock = new ReentrantLock(true);

	private List<CacheOprateListener> listeners = new ArrayList<CacheOprateListener>();

	public void addListener(CacheOprateListener listener) {
		lock.lock();
		try {
			if (listener != null)
				listeners.add(listener);
		} finally {
			lock.unlock();
		}
	}

	public void deleteListener(CacheOprateListener listener) {
		lock.lock();
		try {
			if (listener != null)
				listeners.remove(listener);
		} finally {
			lock.unlock();
		}
	}

	public void notifyListeners(CacheOprator oprator, CacheOprateInfo cacheInfo) {
		lock.lock();
		try {
			if (listeners != null) {
				for (CacheOprateListener obs : listeners) {
					obs.oprate(oprator, cacheInfo);
				}
			}
		} finally {
			lock.unlock();
		}
	}

}
