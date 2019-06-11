package com.spier.common.bean.net.user;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 用户登陆接口用到的数据
 * @author GHB
 * @version 1.0
 * @date 2019.1.2
 */
public class UserLoginBeans implements Serializable{

	private static final long serialVersionUID = -817449191307849556L;

	/**
	 * 请求数据
	 * @author GHB
	 * @version 1.0
	 * @date 2019.1.2
	 */
	public static class Request{
		@Expose
		@SerializedName("uid")
		private String mUserName;
		
		@Expose
		@SerializedName("app_name")
		private String mAppName;
		
		public String getUserName() {
			return mUserName;
		}
		
		public void setUserName(String name) {
			mUserName = name;
		}
		
		public String getAppName() {
			return mAppName;
		}
		
		public void setAppName(String name) {
			mAppName = name;
		}
	}
	
	/**
	 * 响应数据
	 * @author GHB
	 * @version 1.0
	 * @date 2019.1.2
	 */
	public static class Response {
		@Expose
		@SerializedName("fid")
		private String mSessionId;
		
		public String getSessionId() {
			return mSessionId;
		}
		
		public void setSessionId(String id) {
			mSessionId = id;
		}
	}
}
