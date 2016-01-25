package com.taobao.pamirs.cache.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 获取本机IP
 * 
 * @author xiaocheng 2012-11-21
 */
public class IpUtil {

	public static String getLocalIp() {
		String ip = "";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			//
		}

		return ip;
	}

}
