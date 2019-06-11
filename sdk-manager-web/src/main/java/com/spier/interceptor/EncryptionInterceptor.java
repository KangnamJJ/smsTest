package com.spier.interceptor;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 加解密拦截器
 * @author GHB
 * @version 1.0
 * @date 2019.1.24
 */
public class EncryptionInterceptor implements HandlerInterceptor {

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handle, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		Logger.getAnonymousLogger().log(Level.INFO, "afterCompletion……");
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handle, ModelAndView modelAndView)
			throws Exception {
		// TODO Auto-generated method stub
		Logger.getAnonymousLogger().log(Level.INFO, "postHandle……");
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) throws Exception {
		// TODO Auto-generated method stub
		Logger.getAnonymousLogger().log(Level.INFO, "preHandle……");
		
		return true;
	}

}
