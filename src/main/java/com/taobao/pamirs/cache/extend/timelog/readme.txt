====== 方法时间日志程序 ======
一、使用场景
      调试、打印指定bean的方法调用时间，包括多method轨迹展示

二、功能
1.	目标代码无倾入性
2.	方法调用响应时间打印
3.	方法调用轨迹展示
4.	支持方法参数打印（开关）
5.  支持Annotation和配置两种方式并存
6.	支持脚本统计接口调用次数（sort）
	cat 9403.txt |awk '{FS=":"; print $1}'|sort -n |uniq -c|sort -n > 9403.z.txt


三、使用方法
1. 只用注解
	@TimeLog

2. 注入spring bean（可选）
	对一些要打印的bean是第三方包的，不能加注解，可以选择此配置方式
	否则可以采用scan方式启动：
		<context:component-scan base-package="com.taobao.pamirs.cache.extend.timelog" />

	<bean class="com.taobao.pamirs.cache.extend.timelog.TimeHandle">
		<property name="beanList">
			<list>
				<value>articleReadClient</value>
				<value>itemReadClient</value>
				<value>packReadClient</value>
				<value>prodSubscriptionService</value>
				<value>bizOrderCommonService</value>
				<value>servReadService</value>
				<value>accumulationQueryUtil</value>
				<value>productReadService</value>
				<value>resourceLimitUtil</value>
				<value>alipayInfoQueryUtil</value>
				<value>bizVerifyService</value>
				<value>subParameterService</value>
			</list>
		</property>
	</bean>

3. 打印方法时间日志（for log4j）

     <appender name="timelog" class="org.apache.log4j.DailyRollingFileAppender">
        <param name="file" value="${loggingRoot}/timelog.log"/>
        <param name="append" value="false"/>
        <param name="encoding" value="GBK"/>  
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d [%X{requestURIWithQueryString}] %-5p %c{2} - %m%n"/>
        </layout>
    </appender>
    
    <logger name="com.taobao.pamirs.cache.extend.timelog" additivity="false">
        <level value="warn"/>
        <appender-ref ref="timelog"/>
    </logger>