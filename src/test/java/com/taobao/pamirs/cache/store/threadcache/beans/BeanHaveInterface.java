package com.taobao.pamirs.cache.store.threadcache.beans;

import org.springframework.stereotype.Component;

@Component("beanHaveInterface")
public class BeanHaveInterface implements Ibean {

	@Override
	public void sayHello() {
		System.out.println("BeanHaveInterface's saHello!");
	}

	@Override
	public String getName() {
		String name = "BeanHaveInterface's name!";
		System.out.println(name);
		return name;
	}

}
