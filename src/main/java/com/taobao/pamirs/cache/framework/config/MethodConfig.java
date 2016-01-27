package com.taobao.pamirs.cache.framework.config;

import java.io.Serializable;
import java.util.List;

import com.taobao.pamirs.cache.load.verify.Verfication;

/**
 * 基本bean配置
 * 
 * @author xiaocheng 2012-11-2
 */
public class MethodConfig implements Serializable {

	//
	private static final long serialVersionUID = 1L;

	@Verfication(name = "方法名称", notEmpty = true)
	private String methodName;
	/**
	 * 参数类型
	 */
	private List<Class<?>> parameterTypes;

	/**
	 * 缓存前缀
	 */
	private String prefix;
	
	
	/**
	 * 参数配置
	 */
	private List<ParameterIndex> parameterIndexs;
	
	private String customKey;

	
	/**
	 * 失效时间，单位：秒。<br>
	 * 可以是相对时间，也可以是绝对时间(大于当前时间戳是绝对时间过期)。不传或0都是不过期 <br>
	 * 【可选项】
	 */
	private Integer expiredTime;

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * null: 代表没有set,装载配置时需要重新赋值 <br>
	 * 空: 代表无参方法
	 * 
	 * @return
	 */
	public List<Class<?>> getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(List<Class<?>> parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Integer getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(Integer expiredTime) {
		this.expiredTime = expiredTime;
	}

	public boolean isMe(String method, List<Class<?>> types) {
		if (!this.methodName.equals(method))
			return false;

		if (this.parameterTypes == null && types != null)
			return false;

		if (this.parameterTypes != null && types == null)
			return false;

		if (this.parameterTypes != null) {
			if (this.parameterTypes.size() != types.size())
				return false;

			for (int i = 0; i < parameterTypes.size(); i++) {
				if (!parameterTypes.get(i).getSimpleName()
						.equals(types.get(i).getSimpleName()))
					return false;
			}

		}

		return true;
	}

}
