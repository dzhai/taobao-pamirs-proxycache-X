package com.taobao.pamirs.cache.store.threadcache.beans;

import org.springframework.stereotype.Component;

@Component("beanNormal")
public class BeanNormal {

	public void sayHello() {
		System.out.println("BeanNormal's saHello!");
	}

	public String getName() {
		String name = "BeanNormal's name!";
		System.out.println(name);
		return name;
	}

}
