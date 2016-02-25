package com.taobao.pamirs.cache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.taobao.pamirs.cache.extend.log.LoggerListener;
import com.taobao.pamirs.cache.framework.CacheProxy;
import com.taobao.pamirs.cache.framework.ICache;
import com.taobao.pamirs.cache.framework.config.CacheBean;
import com.taobao.pamirs.cache.framework.config.CacheCleanBean;
import com.taobao.pamirs.cache.framework.config.CacheCleanMethod;
import com.taobao.pamirs.cache.framework.config.CacheConfig;
import com.taobao.pamirs.cache.framework.config.MethodConfig;
import com.taobao.pamirs.cache.load.ICacheConfigService;
import com.taobao.pamirs.cache.load.verify.CacheConfigVerify;
import com.taobao.pamirs.cache.store.StoreType;
import com.taobao.pamirs.cache.util.CacheCodeUtil;
import com.taobao.pamirs.cache.util.ConfigUtil;

/**
 * 缓存框架入口类
 * 
 * @author xuanyu
 * @author xiaocheng 2012-11-2
 */
@SuppressWarnings("rawtypes")
public abstract class CacheManager implements ApplicationContextAware,
		ApplicationListener, ICacheConfigService {

	private static final Log log = LogFactory.getLog(CacheManager.class);

	private CacheConfig cacheConfig;

	/**
	 * 每一个method对应一个adapter实例
	 */
	private final Map<String, CacheProxy<Serializable, Serializable>> cacheProxys = new ConcurrentHashMap<String, CacheProxy<Serializable, Serializable>>();

	protected ApplicationContext applicationContext;
		
	protected ICache<Serializable, Serializable> cache =null;

	private boolean useCache = true;
	
	/** 打印缓存命中日志 **/
	private boolean openCacheLog = false;

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
			// 注册cacheBean
			for (CacheBean bean : cacheBeans) {

				List<MethodConfig> cacheMethods = bean.getCacheMethods();
				for (MethodConfig method : cacheMethods) {
					initCacheAdapters(cacheConfig.getStoreRegion(),bean.getBeanName(), method);
				}
			}
		}
		List<CacheCleanBean>  cacheCleanBeans=cacheConfig.getCacheCleanBeans();
		if (cacheCleanBeans != null) {
			// 注册cacheCleanBeans
			for (CacheCleanBean bean : cacheCleanBeans) {

				List<CacheCleanMethod> cacheMethods = bean.getMethods();
				for (MethodConfig method : cacheMethods) {
					initCacheAdapters(cacheConfig.getStoreRegion(),bean.getBeanName(), method);
				}
			}
		}
	}

	/**
	 * 初始化Bean/Method对应的缓存，包括： <br>
	 * 1. CacheProxy <br>
	 * 2. 定时清理任务：storeMapCleanTime <br>
	 * 
	 * @param region
	 * @param cacheBean
	 */
	private void initCacheAdapters(String region, String beanName,MethodConfig cacheMethod) {
		String key =CacheCodeUtil.getCacheAdapterKey(region,beanName,cacheMethod);		
		
		StoreType storeType = StoreType.toEnum(cacheConfig.getStoreType());
		
		if (cache != null) {
			// 1. CacheProxy
			CacheProxy<Serializable, Serializable> cacheProxy = new CacheProxy<Serializable, Serializable>(
					storeType, cacheConfig.getStoreRegion(), key, cache,
					beanName, cacheMethod);

			cacheProxys.put(key, cacheProxy);
			
			
			// 4. 注册Xray log
			if (openCacheLog){
				cacheProxy.addListener(new LoggerListener(beanName,cacheMethod.getMethodName(), cacheMethod.getParameterTypes()));				
			}
		}else{
			throw new RuntimeException("缓存存储方式cache没有注入");
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

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public void setCacheConfig(CacheConfig cacheConfig) {
		this.cacheConfig = cacheConfig;
	}

	public CacheConfig getCacheConfig() {
		return cacheConfig;
	}

	public void setCache(ICache<Serializable, Serializable> cache) {
		this.cache = cache;
	}

	public void setOpenCacheLog(boolean openCacheLog) {
		this.openCacheLog = openCacheLog;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

}
