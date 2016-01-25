package com.taobao.pamirs.cache.extend.jmx.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否需要暴露成JmxMethod
 * 
 * @author xiaocheng 2012-11-8
 */
@Target({ METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JmxMethod {

}
