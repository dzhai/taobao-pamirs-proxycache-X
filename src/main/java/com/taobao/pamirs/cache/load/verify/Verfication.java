package com.taobao.pamirs.cache.load.verify;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.taobao.pamirs.cache.store.StoreType;

/**
 * 校验注解
 * 
 * @author xiaocheng 2012-11-29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Verfication {

	/**
	 * 校验字段名称,用于错误提示
	 * 
	 * @return
	 */
	String name();

	/**
	 * 判断是否可以为null
	 * 
	 * @return
	 */
	boolean notNull() default false;

	/**
	 * 判断String是否可以为null或空字符串 <br>
	 * （only for String Type）
	 * 
	 * @return
	 */
	boolean notEmpty() default false;

	/**
	 * 正则表达式检验<br>
	 * 用法：{"regx", "提示信息"} <br>
	 * （only for String Type）
	 * 
	 * @return
	 */
	String[] regx() default {};

	/**
	 * 字符串最大长度，中文算两个字符，全角英文算两个字符 <br>
	 * （only for String）
	 * 
	 * @return
	 */
	int maxlength() default 0;

	/**
	 * 字符串最小长度，中文算两个字符，全角英文算两个字符 <br>
	 * （only for String）
	 * 
	 * @return
	 */
	int minlength() default 0;

	/**
	 * 判断List是否为空
	 * 
	 * @return
	 */
	boolean notEmptyList() default false;

	/* ------ 业务相关 ------- */

	/**
	 * 判断是否在属于StoreType
	 * 
	 * @return
	 */
	boolean isStoreType() default false;

	/**
	 * 当Type时才会校验，默认都校验
	 * 
	 * @return
	 */
	StoreType[] when() default {};

}
