package com.spier.config.rpc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
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

	// dubbo服务暴露端口
	@Bean
	public ProtocolConfig protocolConfig() {
		ProtocolConfig protocolConfig = new ProtocolConfig();
		protocolConfig.setName(dubboProperties.getName());
		protocolConfig.setPort(dubboProperties.getPort());
		return protocolConfig;
	}

	@Bean
	public ProviderConfig providerConfig() {
		ProviderConfig providerConfig = new ProviderConfig();
		providerConfig.setTimeout(dubboProperties.getTimeout());
		providerConfig.setRetries(dubboProperties.getRetries());
		return providerConfig;
	}
	/*@Bean
	public ApplicationConfig applicationConfig() {
		// 提供方应用信息，用于计算依赖关系
		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName("sdkserver");
		return applicationConfig;
	}

	@Bean
	public RegistryConfig registryConfig() {
		// 使用zookeeper注册中心暴露服务地址
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setAddress("zookeeper://192.168.184.130:2181");
		// registryConfig.setAddress("zookeeper://34.214.107.112:2181?backup=34.214.107.112:2182,34.214.107.112:2183");
		registryConfig.setTimeout(30000);
		return registryConfig;
	}

	// dubbo服务暴露端口
	@Bean
	public ProtocolConfig protocolConfig() {
		ProtocolConfig protocolConfig = new ProtocolConfig();
		protocolConfig.setName("dubbo");
		protocolConfig.setPort(20881);
		return protocolConfig;
	}

	@Bean
	public ProviderConfig providerConfig() {
		ProviderConfig providerConfig = new ProviderConfig();
		providerConfig.setTimeout(30000);
		providerConfig.setRetries(2);
		return providerConfig;
	}*/
}
