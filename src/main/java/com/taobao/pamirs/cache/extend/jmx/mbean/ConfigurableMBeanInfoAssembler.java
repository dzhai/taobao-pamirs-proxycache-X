package com.taobao.pamirs.cache.extend.jmx.mbean;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.management.Descriptor;
import javax.management.modelmbean.ModelMBean;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import javax.management.modelmbean.RequiredModelMBean;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.JdkVersion;
import org.springframework.jmx.export.MBeanExportException;
import org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler;

import com.taobao.pamirs.cache.extend.jmx.annotation.JmxClass;
import com.taobao.pamirs.cache.extend.jmx.annotation.JmxMethod;
import com.taobao.pamirs.cache.util.AopProxyUtil;

/**
 * 只需要配置这个bean与注解，就可以使用mbean了 目前不支持方法中参数是接口的Mbean注入
 * 
 * @author wuxiang
 * @author xiaocheng 2012-11-8
 */
public class ConfigurableMBeanInfoAssembler extends
		AbstractReflectiveMBeanInfoAssembler implements
		ApplicationContextAware, InitializingBean {

	private ApplicationContext applicationContext;

	public void afterPropertiesSet() throws Exception {
		String[] beans = applicationContext.getBeanDefinitionNames();
		for (String beanClass : beans) {
			Object obj;
			try {
				obj = this.applicationContext.getBean(beanClass); // 抽象的类不能实现化
				// 取得被代理的原始对象
				Object target = AopProxyUtil.getPrimitiveProxyTarget(obj);
				if (target.getClass().isAnnotationPresent(JmxClass.class))
					injectMbean(obj, beanClass);
			} catch (Exception e) {
				continue;
			}
		}
	}

	private void injectMbean(Object obj, String beanClass) {
		try {
			ModelMBean mbean = createAndConfigureMBean(obj, beanClass);
			MBeanManagerFactory.registerMBean(
					"Pamirs:name=$" + beanClass + "$", mbean);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ModelMBean createAndConfigureMBean(Object managedResource,
			String beanKey) throws MBeanExportException {
		try {
			ModelMBean mbean = new RequiredModelMBean();
			mbean.setModelMBeanInfo(getMBeanInfo(managedResource, beanKey));
			mbean.setManagedResource(managedResource, "ObjectReference");
			return mbean;
		} catch (Exception ex) {
			throw new MBeanExportException(
					"Could not create ModelMBean for managed resource ["
							+ managedResource + "] with key '" + beanKey + "'",
					ex);
		}
	}

	/**
	 * 去除未注解的方法
	 */
	public ModelMBeanOperationInfo[] getOperationInfo(Object managedBean,
			String beanKey) {

		Object target = null;
		try {
			target = AopProxyUtil.getPrimitiveProxyTarget(managedBean);
		} catch (Exception e) {
		}

		Method[] methods = getClassToExpose(managedBean).getMethods();
		List<ModelMBeanOperationInfo> infos = new ArrayList<ModelMBeanOperationInfo>();

		for (int i = 0; i < methods.length; ++i) {
			Method method = methods[i];
			if ((JdkVersion.isAtLeastJava15()) && (method.isSynthetic()))
				continue;

			if (method.getDeclaringClass().equals(Object.class))
				continue;

			// 取得被代理的原始对象的方法，才能获得Annotation
			Method targetMethod = method;
			if (target != null && target != managedBean) {
				try {
					targetMethod = target.getClass().getMethod(
							method.getName(), method.getParameterTypes());
				} catch (Exception e) {
				}
			}

			if (!targetMethod.isAnnotationPresent(JmxMethod.class))
				continue;

			ModelMBeanOperationInfo info = null;
			PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
			if (pd != null) {
				if (((method.equals(pd.getReadMethod())) && (includeReadAttribute(
						method, beanKey)))
						|| ((method.equals(pd.getWriteMethod())) && (includeWriteAttribute(
								method, beanKey)))) {
					info = createModelMBeanOperationInfo(method, pd.getName(),
							beanKey);
					Descriptor desc = info.getDescriptor();
					if (method.equals(pd.getReadMethod())) {
						desc.setField("role", "getter");
					} else {
						desc.setField("role", "setter");
					}
					desc.setField("visibility", Integer.valueOf(4));
					if (isExposeClassDescriptor()) {
						desc.setField("class",
								getClassForDescriptor(managedBean).getName());
					}
					info.setDescriptor(desc);
				}
			} else if (includeOperation(method, beanKey)) {
				info = createModelMBeanOperationInfo(method, method.getName(),
						beanKey);
				Descriptor desc = info.getDescriptor();
				desc.setField("role", "operation");
				if (isExposeClassDescriptor()) {
					desc.setField("class", getClassForDescriptor(managedBean)
							.getName());
				}
				populateOperationDescriptor(desc, method, beanKey);
				info.setDescriptor(desc);
			}

			if (info != null) {
				infos.add(info);
			}
		}
		return infos.toArray(new ModelMBeanOperationInfo[infos.size()]);
	}

	public void setApplicationContext(ApplicationContext aContext)
			throws BeansException {
		applicationContext = aContext;
	}

	protected boolean includeOperation(Method paramMethod, String paramString) {
		return true;
	}

	protected boolean includeWriteAttribute(Method paramMethod,
			String paramString) {
		return true;
	}

	protected boolean includeReadAttribute(Method paramMethod,
			String paramString) {
		return true;
	}

}
