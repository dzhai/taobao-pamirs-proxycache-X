package com.taobao.pamirs.cache.extend.jmx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 以后jmx的注册，可通过注解自动注入，不需要硬编码
 * 
 * @author xiaocheng 2012-11-8
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JmxClass {

}
