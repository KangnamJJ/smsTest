package com.spier.common.bean.net.user;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.spier.common.bean.net.RequestPhoneInfo;

/**
 * 手机信息补报接口数据
 * @author GHB
 * @version 1.0
 * @date 2019.1.24
 */
public class PhoneReportBeans implements Serializable{

	private static final long serialVersionUID = -1758315177876274387L;

	public static class Request {
		@Expose
		@SerializedName("uid")
		private String mUID;
		
		@Expose
		@SerializedName("app_name")
		private String mAppName;
		
		@Expose
		@SerializedName("sid")
		private String mSessionId;
		
		@Expose
		@SerializedName("phone_info")
		private RequestPhoneInfo mPhone;
		
		public String getUID() {
			return mUID;
		}
		
		public void setUID(String uid) {
			mUID = uid;
		}
		
		public String getAppName() {
			return mAppName;
		}
		
		public void setAppName(String name) {
			mAppName = name;
		}
		
		public String getSessionId() {
			return mSessionId;
		}
		
		public void setSessionId(String id) {
			mSessionId = id;
		}
		
		public RequestPhoneInfo getPhoneInfo() {
			return mPhone;
		}
		
		public void setPhoneInfo(RequestPhoneInfo info) {
			mPhone = info;
		}
	}
	
	public static class Response {
		
	}
}
