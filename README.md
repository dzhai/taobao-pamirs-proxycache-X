
#**淘宝proxycache修改**

### methodConfig配置
1. 添加prefix  
2. 添加cache
3. 添加parameterIndex
4. 添加parameterIndexs  

```
<methodConfig>
	<methodName>getUser</methodName>
		<parameterTypes>
		<java-class>com.XX.XX.Query</java-class>
		</parameterTypes>
		<parameterIndex index="0" name="phone"/> //name 表示参数是对象获取对象里面属性名称对应的值
		<prefix>com.xxx.user</prefix> //缓存前缀
		<cache>User</cache> //缓存cache名称
</methodConfig>
```	

### key生成规则
1. 把参数类型去掉
2. 添加prefix和cache配置	 			
默认生成的key=region@prefix#cache#beanName#methodName#abc@@123
					
### 存储方式
1. 以前是在CacheManage自动创建多个缓存对象，现在修改为只有一个缓存对象，并且需要注入到CacheManage对象中(xml配置文件)

### 清除方式
1. 清除cleanMethods中的methodConfig必须配置prefix
2. 清除使用前缀清除方式 xxx####*的方式（主要用于redis）
3. 清除生成的key=region@prefix#cache#*#abc@@123*

### link
**[taobao-pamirs-proxycache][1]**  
**[taobao-pamirs-proxycache源码分析学习与修改][2]**
[1]: http://code.taobao.org/p/taobao-pamirs-proxycache/wiki/index/
[2]: http://my.oschina.net/u/1164681/blog/502391?fromerr=7eCbgFAg
