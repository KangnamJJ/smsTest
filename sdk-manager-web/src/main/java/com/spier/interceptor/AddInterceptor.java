package com.spier.interceptor;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
@Configuration
public class AddInterceptor extends WebMvcConfigurerAdapter {
	
	//关键，将拦截器作为bean写入配置中
	@Bean
	EncryptionInterceptor encryptionInterceptor(){
        return new EncryptionInterceptor();
    }
	
	//配置拦截器拦截路径
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
         registry.addInterceptor(encryptionInterceptor()).addPathPatterns("/user/register");
         super.addInterceptors(registry);
    }
    
    @Bean  
    public MultipartConfigElement multipartConfigElement() {  
        MultipartConfigFactory factory = new MultipartConfigFactory();  
        //单个文件最大  
        factory.setMaxFileSize("10MB"); 
        // 设置总上传数据总大小  
        //factory.setMaxRequestSize("204800KB");  
        return factory.createMultipartConfig();  
    }  


	/**
	 * 配置静态访问资源
	 *  .excludePathPatterns("/static/**")
	 * @param registry
	 */
	/*@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**").addResourceLocations("/static/");
		super.addResourceHandlers(registry);
	}
	 //设置默认首页，当输入域名是可以自动跳转到默认指定的网页
	 @Override
     public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/WEB-INF/views/index.jsp");
        super.addViewControllers(registry);
     } */
}
