package com.taobao.pamirs.cache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.InstanceAlreadyExistsException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.taobao.pamirs.cache.extend.jmx.CacheMbean;
import com.taobao.pamirs.cache.extend.jmx.CacheMbeanListener;
import com.taobao.pamirs.cache.extend.jmx.annotation.JmxMethod;
import com.taobao.pamirs.cache.extend.jmx.mbean.MBeanManagerFactory;
import com.taobao.pamirs.cache.extend.log.xray.XrayLogListener;
import com.taobao.pamirs.cache.framework.CacheProxy;
import com.taobao.pamirs.cache.framework.ICache;
import com.taobao.pamirs.cache.framework.config.CacheBean;
import com.taobao.pamirs.cache.framework.config.CacheConfig;
import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.framework.timer.CleanCacheTimerManager;
import com.taobao.pamirs.cache.load.ICacheConfigService;
import com.taobao.pamirs.cache.load.verify.CacheConfigVerify;
import com.taobao.pamirs.cache.store.StoreType;
import com.taobao.pamirs.cache.store.map.MapStore;
import com.taobao.pamirs.cache.util.CacheCodeUtil;
import com.taobao.pamirs.cache.util.ConfigUtil;
import com.taobao.pamirs.cache.util.lru.ConcurrentLRUCacheMap;

/**
 * 缓存框架入口类
 * 
 * @author xuanyu
 * @author xiaocheng 2012-11-2
 */
public abstract class CacheManager implements ApplicationContextAware,
		ApplicationListener, ICacheConfigService {

	private static final Log log = LogFactory.getLog(CacheManager.class);

	private CacheConfig cacheConfig;

	/**
	 * 每一个method对应一个adapter实例
	 */
	private final Map<String, CacheProxy<Serializable, Serializable>> cacheProxys = new ConcurrentHashMap<String, CacheProxy<Serializable, Serializable>>();

	protected ApplicationContext applicationContext;

	private CleanCacheTimerManager timeTask = new CleanCacheTimerManager();

	private boolean useCache = true;

	/** 打印缓存命中日志 **/
	private boolean openCacheLog = false;

	/**
	 * 指定本地缓存时LruMap的大小，默认是1024
	 * 
	 * @see StoreType.MAP
	 * @see ConcurrentLRUCacheMap
	 */
	private int localMapSize = ConcurrentLRUCacheMap.DEFAULT_INITIAL_CAPACITY;
	/**
	 * 指定本地缓存分段的大小，默认是16
	 * 
	 * @see StoreType.MAP
	 * @see ConcurrentLRUCacheMap
	 */
	private int localMapSegmentSize = ConcurrentLRUCacheMap.DEFAULT_CONCURRENCY_LEVEL;;

	public void init() throws Exception {
		// 1. 加载/校验config
		cacheConfig = loadConfig();

		// 后面两个，见onApplicationEvent方法
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		// 放在onApplicationEvent里，原因是解决CacheManagerHandle里先执行代理，再applicationContext.getBean，否则代理不了

		if (event instanceof ContextRefreshedEvent) {
			// 2. 自动填充默认的配置
			autoFillCacheConfig(cacheConfig);

			// 3. 缓存配置合法性校验
			verifyCacheConfig(cacheConfig);

			// 4. 初始化缓存
			initCache();
		}
	}

	@Override
	public abstract CacheConfig loadConfig();

	@Override
	public void autoFillCacheConfig(CacheConfig cacheConfig) {
		ConfigUtil.autoFillCacheConfig(cacheConfig, applicationContext);
	}

	@Override
	public void verifyCacheConfig(CacheConfig cacheConfig) {
		CacheConfigVerify.checkCacheConfig(cacheConfig, applicationContext);
	}

	/**
	 * 初始化缓存
	 */
	private void initCache() {
		List<CacheBean> cacheBeans = cacheConfig.getCacheBeans();
		if (cacheBeans != null) {
			// 只需注册cacheBean,目前cacheCleanBeans必须是它的子集
			for (CacheBean bean : cacheBeans) {

				List<MethodConfig> cacheMethods = bean.getCacheMethods();
				for (MethodConfig method : cacheMethods) {
					initCacheAdapters(cacheConfig.getStoreRegion(),
							bean.getBeanName(), method,
							cacheConfig.getStoreMapCleanTime());
				}
			}
		}
	}

	/**
	 * 初始化Bean/Method对应的缓存，包括： <br>
	 * 1. CacheProxy <br>
	 * 2. 定时清理任务：storeMapCleanTime <br>
	 * 3. 注册JMX <br>
	 * 4. 注册Xray log <br>
	 * 
	 * @param region
	 * @param cacheBean
	 * @param storeMapCleanTime
	 */
	private void initCacheAdapters(String region, String beanName,
			MethodConfig cacheMethod, String storeMapCleanTime) {
		String key = CacheCodeUtil.getCacheAdapterKey(region, beanName,
				cacheMethod);
		StoreType storeType = StoreType.toEnum(cacheConfig.getStoreType());
		ICache<Serializable, Serializable> cache = null;

		if (StoreType.MAP == storeType) {
			cache = new MapStore<Serializable, Serializable>(localMapSize,
					localMapSegmentSize);
		}

		if (cache != null) {
			// 1. CacheProxy
			CacheProxy<Serializable, Serializable> cacheProxy = new CacheProxy<Serializable, Serializable>(
					storeType, cacheConfig.getStoreRegion(), key, cache,
					beanName, cacheMethod);

			cacheProxys.put(key, cacheProxy);

			// 2. 定时清理任务：storeMapCleanTime
			if (StoreType.MAP == storeType
					&& StringUtils.isNotBlank(storeMapCleanTime)) {
				try {
					timeTask.createCleanCacheTask(cacheProxy, storeMapCleanTime);
				} catch (Exception e) {
					log.error("[严重]设置Map定时清理任务失败!", e);
				}
			}

			// 3. 注册JMX
			registerCacheMbean(key, cacheProxy, storeMapCleanTime,
					cacheMethod.getExpiredTime());

			// 4. 注册Xray log
			if (openCacheLog)
				cacheProxy.addListener(new XrayLogListener(beanName,
						cacheMethod.getMethodName(), cacheMethod
								.getParameterTypes()));
		}
	}

	/**
	 * 注册JMX
	 * 
	 * @param key
	 * @param cacheProxy
	 */
	private void registerCacheMbean(String key,
			CacheProxy<Serializable, Serializable> cacheProxy,
			String storeMapCleanTime, Integer expiredTime) {
		try {
			String mbeanName = CacheMbean.MBEAN_NAME + ":name=" + key;
			CacheMbeanListener listener = new CacheMbeanListener();
			cacheProxy.addListener(listener);
			CacheMbean<Serializable, Serializable> cacheMbean = new CacheMbean<Serializable, Serializable>(
					cacheProxy, listener, applicationContext,
					storeMapCleanTime, expiredTime);
			MBeanManagerFactory.registerMBean(mbeanName, cacheMbean);
		} catch (InstanceAlreadyExistsException e) {
			log.debug("重复注册JMX", e);
		} catch (Exception e) {
			log.warn("注册JMX失败", e);
		}
	}

	public CacheProxy<Serializable, Serializable> getCacheProxy(String key) {
		if (key == null || cacheProxys == null)
			return null;

		return cacheProxys.get(key);
	}

	public boolean isUseCache() {
		return useCache;
	}

	@JmxMethod
	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public void setCacheConfig(CacheConfig cacheConfig) {
		this.cacheConfig = cacheConfig;
	}

	public CacheConfig getCacheConfig() {
		return cacheConfig;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setLocalMapSize(int localMapSize) {
		this.localMapSize = localMapSize;
	}

	public void setLocalMapSegmentSize(int localMapSegmentSize) {
		this.localMapSegmentSize = localMapSegmentSize;
	}

	public void setOpenCacheLog(boolean openCacheLog) {
		this.openCacheLog = openCacheLog;
	}

}
