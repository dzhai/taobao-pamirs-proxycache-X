package com.taobao.pamirs.cache.utils.redis.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import com.taobao.pamirs.cache.utils.redis.IRedisUtils;

public class RedisUtilsImpl implements IRedisUtils {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private ShardedJedisPool shardedJedisPool;

	public void setShardedJedisPool(ShardedJedisPool shardedJedisPool) {
		this.shardedJedisPool = shardedJedisPool;
	}

	@SuppressWarnings("unused")
	private ShardedJedis getShardedJedis(String key) {
		return shardedJedisPool.getResource();
	}

	private ShardedJedis getShardedJedis() {
		return shardedJedisPool.getResource();
	}

	@SuppressWarnings("deprecation")
	private void returnResource(ShardedJedis shardedJedis) {
		shardedJedisPool.returnResource(shardedJedis);
	}

	@SuppressWarnings("deprecation")
	private void returnBrokenResource(ShardedJedis shardedJedis) {
		shardedJedisPool.returnBrokenResource(shardedJedis);
	}

	private static Jedis getJedis(ShardedJedis shardedJedis) {
		Jedis jedis = null;
		Collection<Jedis> js = shardedJedis.getAllShards();
		Iterator<Jedis> it = js.iterator();
		while (it.hasNext()) {
			jedis = it.next();
		}
		return jedis;
	}

	@Override
	public Object get(byte[] key, int db) {
		ShardedJedis sardedjedis = null;
		try {
			sardedjedis = getShardedJedis();
			Jedis j = getJedis(sardedjedis);
			j.select(db);// 切换数据库
			if (key != null && key.length > 0) {
				return j.get(key);
			} else {
				return null;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			returnBrokenResource(sardedjedis);
		} finally {
			returnResource(sardedjedis);
		}
		return null;
	}

	@Override
	public boolean set(byte[] key, byte[] value, int expiresTime, int db) {
		String judge = null;
		ShardedJedis sardedjedis = null;
		try {
			sardedjedis = getShardedJedis();
			Jedis j = getJedis(sardedjedis);
			j.select(db);// 切换数据库
			judge = j.set(key, value);
			if (expiresTime > 0) {
				j.expire(key, Integer.parseInt(String.valueOf(expiresTime)));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			returnBrokenResource(sardedjedis);
		} finally {
			returnResource(sardedjedis);
		}
		return judge != null ? true : false;
	}

	@Override
	public boolean del(String key, int db) {
		long judge = 0;
		ShardedJedis sardedjedis = null;
		try {
			sardedjedis = getShardedJedis();
			Jedis j = getJedis(sardedjedis);
			j.select(db);// 切换数据库
			if (key != null && !key.equals("")) {
				if (exists(key, db))
					judge = j.del(key);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			returnBrokenResource(sardedjedis);
		} finally {
			returnResource(sardedjedis);
		}

		return judge > 0;
	}

	@Override
	public boolean del(Set<String> keys, int db) {
		ShardedJedis shardedJedis = getShardedJedis();
		long result = 0;
		try {
			Jedis j = getJedis(shardedJedis);
			j.select(db);
			result = j.del(keys.toArray(new String[keys.size()]));
		} catch (Exception e) {
			returnBrokenResource(shardedJedis);
			e.printStackTrace();
		} finally {
			returnResource(shardedJedis);
		}
		return result == keys.size();
	}

	@Override
	public Set<String> keys(String pattern, int db) {
		Set<String> f = null;
		ShardedJedis sardedjedis = null;
		try {
			sardedjedis = getShardedJedis();
			Jedis j = getJedis(sardedjedis);
			j.select(db);// 切换数据库
			if (pattern != null && !pattern.equals("")) {
				f = j.keys(pattern);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			returnBrokenResource(sardedjedis);
		} finally {
			returnResource(sardedjedis);
		}
		return f;
	}
	
	@Override
	public boolean exists(String key, int db) {
		boolean verify = false;
		ShardedJedis sardedjedis = null;
		try {
			sardedjedis = getShardedJedis();
			Jedis j = getJedis(sardedjedis);
			j.select(db);// 切换数据库
			if (key != null && !key.equals("")) {
				verify = j.exists(key);
			} else {
				verify = false;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			returnBrokenResource(sardedjedis);
		} finally {
			returnResource(sardedjedis);
		}

		return verify;
	}

}
