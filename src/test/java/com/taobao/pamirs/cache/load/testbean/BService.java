package com.taobao.pamirs.cache.load.testbean;

import java.util.Date;

/**
 * 测试Bean-B（测试支持的各种参数）
 * 
 * @author xiaocheng 2012-11-19
 */
public interface BService {

	String doVarietyArgs(boolean a, Boolean b, char c, Character d, byte e,
			Byte f, short g, Short h, int j, Integer k, long l, Long m,
			float n, Float o, double p, Double q, Date r, String s);

}
