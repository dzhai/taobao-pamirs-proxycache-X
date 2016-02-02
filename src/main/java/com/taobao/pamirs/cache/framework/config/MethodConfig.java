package com.taobao.pamirs.cache.framework.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

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
	 * 缓存前缀 prefix_value
	 */
	private String prefix;	
	
	/**
	 * 参数配置
	 */
	private List<ParameterIndex> parameterIndexs;
	
	/**
	 * 参数配置
	 */
	private ParameterIndex parameterIndex;

	/**
	 * 缓存code生成
	 */
	private String cacheCodeType;
	
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

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public List<ParameterIndex> getParameterIndexs() {
		if(parameterIndex!=null && parameterIndexs==null){
			parameterIndexs=new ArrayList<ParameterIndex>();
			parameterIndexs.add(parameterIndex);
		}
		return parameterIndexs;
	}

	public ParameterIndex getParameterIndex() {
		return parameterIndex;
	}

	public void setParameterIndex(ParameterIndex parameterIndex) {
		this.parameterIndex = parameterIndex;
	}

	public void setParameterIndexs(List<ParameterIndex> parameterIndexs) {
		if(parameterIndexs!=null && parameterIndexs.size()>0){
			Collections.sort(parameterIndexs, new Comparator<ParameterIndex>(){
				@Override
				public int compare(ParameterIndex o1, ParameterIndex o2) {
					if(o1.getIndex()>o2.getIndex()){
						return 1;
					}else if(o1.getIndex()<o2.getIndex()){
						return -1;
					}
					return 0;
				}
				
			});
			
			int lindex=parameterIndexs.get(parameterIndexs.size()-1).getIndex();
			
			for(int i=0;i<=lindex;i++){
				if(parameterIndexs.get(i).getIndex()!=i){
					parameterIndexs.add(i, null);					
				}
			}
		}
		this.parameterIndexs = parameterIndexs;
	}

	public String getCacheCodeType() {
		if(StringUtils.isBlank(cacheCodeType)){
			cacheCodeType="prefix_value";
		}
		return cacheCodeType;
	}

	public void setCacheCodeType(String cacheCodeType) {
		this.cacheCodeType = cacheCodeType;
	}

}
