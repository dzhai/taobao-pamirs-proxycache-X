====== 动态线程缓存 ======
一、使用场景
    在流程中（单线程），涉及到对多个method重复调用，并且结果相同（流程中不会DML结果）
    即使单个接口性能很高，也会导致整个流程性能降低。
    	（如：
    		交易流程需要调用商品接口100次以上，虽然每次调用只需要3ms（已走tair），但总的RT也需要300ms了
    		）
    为提升性能，但又不想对原有的代码过多的侵入
    	（如：
    		1. 对重复调用的method提前调用，然后把结果传递给每一个调用的地方
    		2. 单纯采用threadlocal保存结果缓存，然后每个方法内部修改逻辑，可以优先取缓存
    		）

二、目标
1. 对原有代码基本无侵入性
2. 和spring无缝结合
3. 支持method粒度
4. 支持bean：method = 1：n配置
5. lazy load策略加载缓存
6. 命中率打印
7. JMX动态开关


三、使用方法
1. 注入spring bean：

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
	
2. 流程开始时启动线程缓存
	public void process() {
		// 线程缓存启动
		ThreadContext.startLocalCache();
		try {
			...
		} finally {
			ThreadContext.remove();	
		}
	}

2. 打印cache命中日志（可选） for log4j

     <appender name="threadcache" class="org.apache.log4j.DailyRollingFileAppender">
        <param name="file" value="${loggingRoot}/threadcache.log"/>
        <param name="append" value="false"/>
        <param name="encoding" value="GBK"/>  
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d [%X{requestURIWithQueryString}] %-5p %c{2} - %m%n"/>
        </layout>
    </appender>
    
    <logger name="com.taobao.pamirs.cache.store.threadcache" additivity="false">
        <level value="warn"/>
        <appender-ref ref="threadcache"/>
    </logger>