package com.spier.config.cache;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class JedisClusterConfig {
	private static final Logger LOGGER=LoggerFactory.getLogger(JedisClusterConfig.class);
	private static final String FIELDNAME_CLUSTERCONFIG="clusterConfig";
	
	@Bean
	public JedisCluster getJedisCluster(JedisConnectionFactory jconnectionFactory){
		JedisCluster jedisCluster = null;
		try {
			JedisPoolConfig jedisPoolConfig=jconnectionFactory.getPoolConfig();
			Field f=JedisConnectionFactory.class.getDeclaredField(FIELDNAME_CLUSTERCONFIG);
			f.setAccessible(true);
			RedisClusterConfiguration redisClusterConfiguration=(RedisClusterConfiguration) f.get(jconnectionFactory);
			Set<HostAndPort> nodes=new HashSet<HostAndPort>();
			if(redisClusterConfiguration.getClusterNodes()!=null && !redisClusterConfiguration.getClusterNodes().isEmpty()){
				for(RedisNode clusterNode:redisClusterConfiguration.getClusterNodes()){
					nodes.add(new HostAndPort(clusterNode.getHost(),clusterNode.getPort()));
				}
			}else{
				nodes.add(new HostAndPort(jconnectionFactory.getHostName(),jconnectionFactory.getPort()));
			}
			int redirects = redisClusterConfiguration.getMaxRedirects() != null ? redisClusterConfiguration.getMaxRedirects(): 3;
			jedisCluster= new JedisCluster(nodes,jconnectionFactory.getTimeout(),jconnectionFactory.getTimeout(), redirects,jconnectionFactory.getPassword(),jedisPoolConfig);
			return jedisCluster;
		} catch (Exception e) {
			LOGGER.error("初始化jediscluster异常",e);
		}
		return jedisCluster;
	}
	
}
