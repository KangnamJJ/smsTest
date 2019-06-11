package com.spier.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.dubbo")
public class DubboProperties {
	private String applicationName;  //应用名称
	private String address;    //注册地址
	private String name;       //端口服务名称
	private Integer port;          //端口
	private Integer timeout;       //超时时间
	private Integer retries;       //重试次数
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public Integer getTimeout() {
		return timeout;
	}
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	public Integer getRetries() {
		return retries;
	}
	public void setRetries(Integer retries) {
		this.retries = retries;
	}
}
