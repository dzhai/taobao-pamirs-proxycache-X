package com.taobao.pamirs.cache.store.threadcache.beans;

import java.util.Date;
import java.util.HashMap;

import org.springframework.stereotype.Component;

@Component("beanVarietyArgs")
public class BeanVarietyArgs {
	
	public String sayHelloPrimitive(boolean bo, char c, byte b, short s, int i, long l, float f, double d) {
		String result = "" + bo + c + b + s + i + l + f + d;
		System.out.println(result);
		return result;
	}
	
	public String sayHelloBox(Boolean bo, Character c, Byte b, Short s, Integer i, Long l, Float f, Double d, Date time) {
		String result = "" + bo + c + b + s + i + l + f + d + time;
		System.out.println(result);
		return result;
	}
	
	public String sayHelloObject(HashMap<?, ?> map) {
		String result = "map:" + map;
		System.out.println(result);
		return result;
	}

}
