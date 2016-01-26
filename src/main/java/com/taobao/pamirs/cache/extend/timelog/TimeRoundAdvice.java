package com.taobao.pamirs.cache.extend.timelog;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * advice
 * 
 * @author xiaocheng Nov 12, 2015
 */
public class TimeRoundAdvice implements Advice, MethodInterceptor {

	@SuppressWarnings("unused")
	private Class<?> beanClass;
	private TimeHandle timeHandle;
	private String beanName;

	public TimeRoundAdvice(Class<?> aBeanClass, String beanName,
			TimeHandle timeHandle) {
		this.beanClass = aBeanClass;
		this.timeHandle = timeHandle;
		this.beanName = beanName;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		long startTime = System.currentTimeMillis();

		String methodName = beanName + "$" + invocation.getMethod().getName();
		TimeLogManager.addCount();

		Object result = null;
		try {
			result = invocation.proceed();
		} finally {

			if (timeHandle.isOpenPrint()) {// 开关
				// 打印格式
				StringBuilder sb = new StringBuilder();
				sb.append("[").append(methodName).append("]");

				if (timeHandle.isPrintParams()
						&& invocation.getArguments() != null
						&& invocation.getArguments().length != 0) {// 打印参数
					sb.append("(");
					sb.append(ToStringBuilder.reflectionToString(
							invocation.getArguments(),
							ToStringStyle.SIMPLE_STYLE));
					sb.append(")");
				}

				sb.append(":消费 ")
						.append(System.currentTimeMillis() - startTime)
						.append(" ms");

				TimeLogManager.addLogInfo(sb.toString());
			} else {
				TimeLogManager.remove();
			}
		}

		return result;
	}
}
