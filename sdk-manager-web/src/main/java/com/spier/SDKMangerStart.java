package com.spier;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

  
@EnableDubbo
@SpringBootApplication
public class SDKMangerStart extends SpringBootServletInitializer{  
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SDKMangerStart.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(SDKMangerStart.class, args);
	}

	// jul -slf4j日志
	static {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}
}  