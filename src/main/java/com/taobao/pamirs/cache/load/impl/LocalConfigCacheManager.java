package com.taobao.pamirs.cache.load.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.taobao.pamirs.cache.extend.jmx.annotation.JmxClass;
import com.taobao.pamirs.cache.framework.config.CacheConfig;
import com.taobao.pamirs.cache.framework.config.CacheModule;
import com.taobao.pamirs.cache.load.AbstractCacheConfigService;
import com.taobao.pamirs.cache.load.LoadConfigException;
import com.taobao.pamirs.cache.util.ConfigUtil;

/**
 * 本地加载缓存配置服务
 * 
 * @author poxiao.gj
 * @date 2012-11-13
 */
@JmxClass
public class LocalConfigCacheManager extends AbstractCacheConfigService {

	private List<String> configFilePaths;

	public void setConfigFilePaths(List<String> configFilePaths) {
		this.configFilePaths = configFilePaths;
	}

	/**
	 * 加载加载缓存配置
	 * 
	 * @return
	 */
	public CacheConfig loadConfig() {
		List<CacheModule> cacheModules = getCacheModules();
		if (cacheModules.size() <= 0) {
			throw new LoadConfigException("非法的缓存配置，CacheModule列表为空");
		}

		CacheConfig cacheConfig = new CacheConfig();
		cacheConfig.setStoreType(getStoreType());
		cacheConfig.setStoreMapCleanTime(getMapCleanTime());
		cacheConfig.setStoreRegion(getStoreRegion());
		cacheConfig.setStoreTairNameSpace(getTairNameSpace());
		for (CacheModule cacheModule : cacheModules) {
			cacheConfig.getCacheBeans().addAll(cacheModule.getCacheBeans());
			cacheConfig.getCacheCleanBeans().addAll(
					cacheModule.getCacheCleanBeans());
		}

		return cacheConfig;
	}

	/**
	 * 从文件中获取配置文件信息
	 * 
	 * @return
	 * @throws Exception
	 */
	private List<CacheModule> getCacheModules() {
		if (configFilePaths == null || configFilePaths.size() <= 0) {
			throw new IllegalArgumentException("非法配置文件路径的参数，配置文件列表不能为空");
		}

		InputStream input = null;
		try {
			ClassLoader classLoader = Thread.class.getClassLoader();
			if (classLoader == null) {
				classLoader = LocalConfigCacheManager.class.getClassLoader();
			}
			List<CacheModule> cacheModuleList = new ArrayList<CacheModule>();
			for (String configFilePath : configFilePaths) {
				input = classLoader.getResourceAsStream(configFilePath);
				if (input != null) {
					CacheModule cacheModule = ConfigUtil
							.getCacheConfigModule(input);
					cacheModuleList.add(cacheModule);
				}
			}
			return cacheModuleList;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
