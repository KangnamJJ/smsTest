package com.spier.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.RegistryConfig;

@Configuration
@EnableConfigurationProperties(DubboProperties.class)
public class DubboConfig {
	@Autowired
	private DubboProperties dubboProperties;

	@Bean
	public ApplicationConfig applicationConfig() {
		// 提供方应用信息，用于计算依赖关系
		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName(dubboProperties.getApplicationName());
		return applicationConfig;
	}

	@Bean
	public RegistryConfig registryConfig() {
		// 使用zookeeper注册中心暴露服务地址
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setAddress(dubboProperties.getAddress());
		registryConfig.setTimeout(dubboProperties.getTimeout());
		return registryConfig;
	}

	@Bean
	public ConsumerConfig consumerConfig() {
		ConsumerConfig consumerConfig = new ConsumerConfig();
		consumerConfig.setCheck(false);
		consumerConfig.setTimeout(dubboProperties.getTimeout());
		consumerConfig.setRetries(dubboProperties.getRetries());
		return consumerConfig;
	}
	
	/*  @Bean public ProtocolConfig protocolConfig() {
	  ProtocolConfig protocolConfig = new ProtocolConfig(); 
	  protocolConfig.setName("dubbo");
	  protocolConfig.setPort(20880); return protocolConfig; 
	  }*/
	 
}
