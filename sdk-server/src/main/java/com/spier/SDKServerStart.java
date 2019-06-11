package com.spier;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;


@EnableTransactionManagement  //事务管理注解
@EnableDubbo
@DubboComponentScan(basePackages ="com.spier.service.impl")
@MapperScan("com.spier.mapper")
@SpringBootApplication 
public class SDKServerStart extends SpringBootServletInitializer{
	 @Override
	 protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
	       return builder.sources(SDKServerStart.class);
	 }
	 
	public static void main(String[] args) {
		SpringApplication.run(SDKServerStart.class, args);
	}

	// jul -slf4j日志
	static {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}
}
