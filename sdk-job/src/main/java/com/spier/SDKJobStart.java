package com.spier;


import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

@EnableDubbo
@SpringBootApplication
@EnableScheduling
public class SDKJobStart extends SpringBootServletInitializer{  
	 @Override
	 protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		 return builder.sources(SDKJobStart.class);
	 }
	 
	 public static void main(String[] args) {  
		 SpringApplication.run(SDKJobStart.class, args);  
	  }  
	 
	// jul -slf4j日志
	static {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}
} 
