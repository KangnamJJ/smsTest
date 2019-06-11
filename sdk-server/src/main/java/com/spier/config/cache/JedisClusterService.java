package com.spier.config.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.JedisCluster;

@Service
public  class JedisClusterService{
	@Autowired
	private JedisCluster jedisCluster;
	
	public JedisClusterService() {
    }
	private int timeOut = 43200;//默认超时时间 60*60*12 半天
	
	public void set(String key, String value) {
		jedisCluster.set(key, value);
	}
	public void saveOrUpdate(String key, int timeout, String value) {
		jedisCluster.setex(key, timeout, value);
	}
	public void saveOrUpdate(String key, Object obj) {
		jedisCluster.setex(key, this.timeOut, JSON.toJSONString(obj));
	}
	public void saveOrUpdate(String key, int timeout, Object obj) {
		jedisCluster.setex(key, timeout, JSON.toJSONString(obj));
	}
	public void saveOrUpdate(String key, String value) {
		jedisCluster.setex(key, this.timeOut, value);
	}
	public void setnx(String key, String value) {
		jedisCluster.setnx(key, value);
	}
	/**
	 * 取值返回相对应的类型
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T> T getValue(String key ,Class<T> clazz){
		return (T) JSON.parseObject(get(key), clazz);
	}
	public String get(String key) {
		return jedisCluster.get(key);
	}
	public Long del(String key) {
		return jedisCluster.del(key);
	}
	public Long incr(String key) {
		return jedisCluster.incr(key);
	}
	public Long expire(String key, int timeout) {
		return jedisCluster.expire(key, timeout);
	}
}
