package com.taobao.pamirs.cache.store.threadcache.beans;

import org.springframework.stereotype.Component;

@Component("beanFinal")
public final class BeanFinal implements Ibean {

	public void sayHello() {
		System.out.println("BeanFinal's saHello!");
	}

	public String getName() {
		String name = "BeanFinal's name!";
		System.out.println(name);
		return name;
	}

}
