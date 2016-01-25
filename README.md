#**[taobao-pamirs-proxycache][1]**

**目前稳定版本:** 
```
<dependency>
    <groupId>com.taobao.pamirs.proxycache</groupId>
    <artifactId>taobao-pamirs-proxycache</artifactId>
    <version>2.0.3</version>
</dependency>
```
**此文档内部包括：**
1、设计目标   
2、概念解释   
3、TairStore 和 MapStore 的区别   
4、动态线程缓存ThreadCache   
5、一个典型的应用范例   
6、使用过程中的注意事项   
7、版本升级说明   
**1、Proxycache 的设计目标**  
1、taobao-pamirs-proxycache   的目的是产生一种和业务代码完全隔离的缓存工具.开发只需要编写业务代码.缓存的使用完全通过配置文件进行.   
2、缓存可以动态的增加和去除.  
3、缓存功能和存储机制分离. 可以选择不同的数据存储方式来获得数据.  
4、可以通过JMX控制台查看缓存状态,命中率,读取/写入平均耗时,修改缓存数据.  
5、可以设定缓存自动更新,格式为标准 quartz 格式 （请参照： http://dogstar.iteye.com/blog/116130）   
6、提供一种动态线程缓存,可以对一个流程内的数据自动缓存.   
**2、概念解释**  
**缓存配置文件**  
在2.1.0版本后，对Spring bean配置和缓存配置CacheModule彻底分开，并且可支持多种缓存配置的load，真正做到缓存配置和存储分离。  
**Spring Bean**  
```
Spring bean 定义的缓存启动. 包括 cacheManage , store
<bean id="cacheManager" class="com.taobao.pamirs.cache.load.impl.LocalConfigCacheManager" init-method="init"
	depends-on="tairManager">
	<property name="storeType" value="map" />
	<property name="storeMapCleanTime" value="0,10,20,30,40,50 * * * * ? *" />
	<property name="configFilePaths">
		<list>
			<value>bean/cache/cache-config-article.xml</value>
		</list>
	</property>
</bean>
<bean class="com.taobao.pamirs.cache.framework.aop.handle.CacheManagerHandle">
	<property name="cacheManager" ref="cacheManager" />
</bean>
```
**CacheModule**  
```
<cacheModule>
	<!-- 缓存Bean配置 -->
	<cacheBeans>
		<cacheBean>
			<beanName>promotionReadService</beanName>
			<cacheMethods>
				<methodConfig>
					<methodName>getPromotionByCode</methodName>
					<parameterTypes>
						<java-class>java.lang.String</java-class>
					</parameterTypes>
					<expiredTime></expiredTime>
				</methodConfig>
			</cacheMethods>
		</cacheBean>
	</cacheBeans>
	<!-- 缓存清理Bean配置 -->
	<cacheCleanBeans>
		<cacheCleanBean>
			<beanName>promotionReadService</beanName>
			<methods>
				<cacheCleanMethod>
					<methodName>cleanCacheById</methodName>
					<parameterTypes>
						<java-class>java.lang.Long</java-class>
					</parameterTypes>
					<cleanMethods>
						<methodConfig>
							<methodName>getPromotionById</methodName>
						</methodConfig>
					</cleanMethods>
				</cacheCleanMethod>
			</methods>
		</cacheCleanBean>
	</cacheCleanBeans>
</cacheModule>
```
**缓存执行原理**  
1.cacheManage 被Spring装载.   
2.通过指定的Load方式加载指定的cacheModule 配置列表.   
3.合并多个cacheModule配置,增加autofill默认配置、配置validate功能,最终合并成cacheConfig.   
4.p按照cacheConfig配置,通过 cglib 对配置bean建立动态代理对象 proxyBean.   
5.按照cacheConfig配置,CacheManage 建立缓存代理 CacheProxy.   
6.proxyBean 的方法被调用时,参数作为 CacheKey ,方法返回结果作为 CacheValue,存入Cache.   
7.后续再有同样的调用,根据参数返回 CacheValue, 实现缓存功能.   
8.Cache底层实现多种存储方式,各有优点,适合不同业务场景.   

**3.TairStore 和 MapStore 的区别**  
```
TairStore
/**
 * TairStore 采用淘宝 Tair 的统一缓存存储方案.
 * <p>
 * 
 * <pre>
 * 通过 Key-Value 的形式将序列化后的对象存入 Tair 服务器中.
 * 
 * 只能采用 PUT , PUT_EXPIRETIME , GET , REMOVE 这四种 Key 操作. 
 * 不能使用 CLEAR , CLEAN 这种范围清除操作.
 * 
 * 使用该 Store . 数据量可以比较大. 5G ~ 10G 
 * 适用于数据量大但变化较少的场合.
 * 
 * 例如：商品数据、订单数据. 
 */
MapStore
/**
 * MapStore 使用本地ConcurrentLRUCacheMap作为 CacheManage 的缓存存储方案.
 * <p>
 * 
 * <pre>
 * 通过 Key-Value 的形式将对象存入 本地内存中.
 * 
 * 可以采用 PUT , PUT_EXPIRETIME , GET , REMOVE 这三种 Key 操作. 
 * 可以采用 CLEAR , CLEAN 这种范围清除操作.
 * 
 * 使用该 Store . 数据量较小. 0 ~ 1G 访问耗时极低. 
 * 适用于数据量小但变化较多的场合.
 * 
 * 例如基础型数据.
 * </pre>
*/
```
**4、动态线程缓存ThreadCache**  
动态线程缓存：一种区别与 分布式缓存 和 本地缓存 的另一种线程缓存。  
**一、使用场景**  
在流程中（单线程），涉及到对多个method重复调用，并且结果相同（流程中不会DML结果）   即使单个接口性能很高，也会导致整个流程性能降低。  
（如：
交易流程需要调用商品接口100次以上，虽然每次调用只需要3ms（已走tair），但总的RT也需要300ms了
）  
为提升性能，但又不想对原有的代码过多的侵入
（如：
对重复调用的method提前调用，然后把结果传递给每一个调用的地方
单纯采用threadlocal保存结果缓存，然后每个方法内部修改逻辑，可以优先取缓存
）  
**二、目标**  
对原有代码基本无侵入性  
和spring无缝结合  
支持method粒度  
支持bean：method = 1：n配置  
lazy load策略加载缓存  
命中率打印  
JMX动态开关  
**三、使用方法**  
注入spring bean：  
```
	<bean class="com.taobao.pamirs.cache.store.threadcache.ThreadCacheHandle">
		<property name="beansMap">
		<!-- the void method not support， will ignore cache -->
			<map>
				<entry key="articleReadClient" value="getArticleById,getPromotionIds,getMarketSaleConditions" />
				<entry key="itemReadClient" value="getItemById,getMutexItemIds,getPromotionIds,getSaleConditions" />
				<entry key="prodSubscriptionService" value="countProdSubNumByProductId" />
				<entry key="bizVerifyService" value="existAppstoreSubParam" />
			</map>		
		</property>
	</bean>
流程开始时启动线程缓存
	public void process() {
		// 线程缓存启动
		ThreadContext.startLocalCache();
		try {
			...
		} finally {
			ThreadContext.remove();	
		}
	}
```
**5、一个典型的应用范例**  
**服务平台商品中心数据缓存配置**  
**base-cache.xml**  
```
<?xml version="1.0" encoding="GBK"?>
<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN//EN" "http://www.springframework.org/dtd/spring-beans.dtd">
<beans default-autowire="byName">

	<bean id="tairManager" class="com.taobao.tair.impl.DefaultTairManager" init-method="init">
		<property name="configServerList">
			<list>
				<value>${tair.config.server1}</value>
				<value>${tair.config.server2}</value>
			</list>
		</property>
		<property name="groupName">
			<value>${tair.group.name}</value>
		</property>
	</bean>
	
	<bean id="cacheManager" class="com.taobao.pamirs.cache.load.impl.LocalConfigCacheManager" init-method="init"
		depends-on="tairManager">
		<property name="storeType" value="tair" />
		<property name="tairNameSpace" value="318" />
		<!-- <property name="storeRegion" value="${store.tair.region}" /> -->
		<property name="storeRegion" value="fwgoods.daily" />
		<property name="configFilePaths">
			<list>
				<value>bean/cache/cache-config-article.xml</value>
				<value>bean/cache/cache-config-item.xml</value>
				<value>bean/cache/cache-config-pack.xml</value>
				<value>bean/cache/cache-config-activity.xml</value>
				<value>bean/cache/cache-config-promotion.xml</value>
			</list>
		</property>
		<property name="tairManager" ref="tairManager" />
	</bean>
	<bean class="com.taobao.pamirs.cache.framework.aop.handle.CacheManagerHandle">
		<property name="cacheManager" ref="cacheManager" />
	</bean>
</beans>
```
**cache-config-article.xml**  
```
<?xml version="1.0" encoding="GBK"?>
<!-- ===================================================================== -->
<!-- 商品加载缓存管理配置 -->
<!-- ===================================================================== -->
<cacheModule>
	<cacheBeans>
		<cacheBean>
			<beanName>articleReadService</beanName>
			<cacheMethods>
				<methodConfig>
					<methodName>getArticleById</methodName>
				</methodConfig>
				<methodConfig>
					<methodName>getArticleByCode</methodName>
				</methodConfig>
				<methodConfig>
					<methodName>getArticleByAppkey</methodName>
				</methodConfig>
				<methodConfig>
					<methodName>getSaleConditions</methodName>
				</methodConfig>
				<methodConfig>
					<methodName>getMarketSaleConditions</methodName>
				</methodConfig>
				<methodConfig>
					<methodName>getPromotions</methodName>
				</methodConfig>
				<methodConfig>
					<methodName>getDependArticles</methodName>
				</methodConfig>
				<methodConfig>
					<methodName>getMutexArticles</methodName>
				</methodConfig>
				<methodConfig>
					<methodName>getPayServiceCodes</methodName>
				</methodConfig>
			</cacheMethods>
		</cacheBean>
	</cacheBeans>
	<cacheCleanBeans>
		<cacheCleanBean>
			<beanName>articleReadService</beanName>
			<methods>
				<cacheCleanMethod>
					<methodName>cleanCacheById</methodName>
					<cleanMethods>
						<methodConfig>
							<methodName>getArticleById</methodName>
						</methodConfig>
						<methodConfig>
							<methodName>getSaleConditions</methodName>
						</methodConfig>
						<methodConfig>
							<methodName>getPromotions</methodName>
						</methodConfig>
						<methodConfig>
							<methodName>getDependArticles</methodName>
						</methodConfig>
						<methodConfig>
							<methodName>getMutexArticles</methodName>
						</methodConfig>
					</cleanMethods>
				</cacheCleanMethod>
				<cacheCleanMethod>
					<methodName>cleanCacheByCode</methodName>
					<cleanMethods>
						<methodConfig>
							<methodName>getArticleByCode</methodName>
						</methodConfig>
					</cleanMethods>
				</cacheCleanMethod>
				<cacheCleanMethod>
					<methodName>cleanCacheByAppkey</methodName>
					<cleanMethods>
						<methodConfig>
							<methodName>getArticleByAppkey</methodName>
						</methodConfig>
					</cleanMethods>
				</cacheCleanMethod>
				<cacheCleanMethod>
					<methodName>cleanCacheByMarkeType</methodName>
					<cleanMethods>
						<methodConfig>
							<methodName>getMarketSaleConditions</methodName>
						</methodConfig>
					</cleanMethods>
				</cacheCleanMethod>
				<cacheCleanMethod>
					<methodName>cleanCacheByPayService</methodName>
					<cleanMethods>
						<methodConfig>
							<methodName>getPayServiceCodes</methodName>
						</methodConfig>
					</cleanMethods>
				</cacheCleanMethod>
			</methods>
		</cacheCleanBean>
	</cacheCleanBeans>
</cacheModule>
```
**jmx-mbean.xml**  
```
<?xml version="1.0" encoding="GBK"?>
<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN//EN" "http://www.springframework.org/dtd/spring-beans.dtd">
<beans default-autowire="byName">

	<bean id="exporter" class="org.springframework.jmx.export.MBeanExporter" lazy-init="false">
		<property name="beans">
			<map>
				<entry key="bean:name=htmlAdaptor" value-ref="htmlAdaptor" />
			</map>
		</property>
	</bean>

	<bean id="htmlAdaptor" class="com.sun.jdmk.comm.HtmlAdaptorServer" init-method="start">
		<property name="port" value="5168" />
	</bean>

	<!-- pamirs-cache 提供 -->
	<bean class="com.taobao.pamirs.cache.extend.jmx.mbean.ConfigurableMBeanInfoAssembler" />

</beans>
```
**log4j.xml**  
```
	<appender name="pamirsCache" class="org.apache.log4j.DailyRollingFileAppender">
		<param name="file" value="${goodscenter.loggingRoot}/pamirs_cache.log" />
		<param name="append" value="false" />
		<param name="encoding" value="GBK" />
		<layout class="org.apache.log4j.PatternLayout">
			<param name="ConversionPattern" value="%d %p - %m%n" />
		</layout>
	</appender>

	<logger name="com.taobao.pamirs.cache" additivity="false">
		<level value="warn" />
		<appender-ref ref="pamirsCache" />
	</logger>
```
**6、使用过程中的注意事项**  
**缓存支持的method参数限定**  
本缓存框架目前支持的method参数为了能够区分每次请求的key的唯一性，所以参数类型有约束。 当前支持的ParameterSupportType有如下18种：  
```
	/**
	 * 当前支持的参数类型
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isSupportParameterTypes(Class<?> type) {
		if (type.isPrimitive() || Byte.class.isAssignableFrom(type)
				|| Short.class.isAssignableFrom(type)
				|| Integer.class.isAssignableFrom(type)
				|| Long.class.isAssignableFrom(type)
				|| Float.class.isAssignableFrom(type)
				|| Double.class.isAssignableFrom(type)
				|| Character.class.isAssignableFrom(type)
				|| String.class.isAssignableFrom(type)
				|| Date.class.isAssignableFrom(type)
				|| Boolean.class.isAssignableFrom(type))
			return true;

		return false;
	}
```
**配置示例**  
完整的配置示例，请见src/test/resources/ designmodel 目录下：  
配置示例-springCache.xml   
配置示例-beanCache.xml   
**6、版本升级说明**  
**2012-08-31：升级为1.2.3**  
修改内容：支持多个基本类型的请求参数组合一个key，进行缓存操作。当请求参数为空时，默认为null。（tbyunshu）   
**2012-11-22：升级为2.0**  
（注意：本次升级修改比较大，在结构设计、配置上有大的重构，配置不向后兼容）（xiaocheng）   
**修改内容**  
AOP\CacheManager\CacheProxy\Store\Load\Extend 各自功能域更加清晰，解耦，易扩展.  
支持Tair的原生ExpireTime.  
MapStore采用高性能的ConcurrentLRU算法，并且使用SoftReference来避免OOM.  
集成动态线程缓存.  
配置源可扩展化（local、zk等）.  
配置简易化（约定大于配置）、增加启动校验功能.  
自带缓存命中日志，采用异步log打印。并且日志与xray集合，提供缓存命中率报表、监控等.  
**目前不支持的有**  
Loader继承CacheManager，1:1关系，所以CacheManager不支持多种Loader.   
一个Loader只能支持可支持多个Resource文件，但只能用一种storeType.   

  [1]: http://code.taobao.org/p/taobao-pamirs-proxycache/wiki/index/
