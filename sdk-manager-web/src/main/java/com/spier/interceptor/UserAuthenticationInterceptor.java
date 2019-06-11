package com.spier.interceptor;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 用户鉴权拦截器
 * @author GHB
 * @version 1.0
 * @date 2019.1.24
 */
public class UserAuthenticationInterceptor implements HandlerInterceptor {

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		Logger.getAnonymousLogger().log(Level.INFO, "afterCompletion……");
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub
		Logger.getAnonymousLogger().log(Level.INFO, "postHandle……");
	}

	@Override
	public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2) throws Exception {
		// TODO Auto-generated method stub
		Logger.getAnonymousLogger().log(Level.INFO, "preHandle……");
		return true;
	}

}
