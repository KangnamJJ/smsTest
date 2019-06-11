package com.spier.common.bean.net.user;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 用户权限上报数据封装
 * @author GHB
 * @version 1.0
 * @date 2019.1.17
 */
public class UserPrivilegesReportBeans implements Serializable{

	private static final long serialVersionUID = -4919202146029410945L;

	public static class Request {
		@Expose
		@SerializedName("uid")
		private String mUserId;
		
		@Expose
		@SerializedName("app_name")
		private String mAppName;
		
		@Expose
		@SerializedName("sid")
		private String mSessionId;
		
		@Expose
		@SerializedName("privs")
		private List<String> mPrivileges;

		/**
		 * @return the mUserId
		 */
		public String getUserId() {
			return mUserId;
		}

		/**
		 * @param mUserId the mUserId to set
		 */
		public void setUserId(String userId) {
			this.mUserId = userId;
		}

		/**
		 * @return the mAppName
		 */
		public String getAppName() {
			return mAppName;
		}

		/**
		 * @param mAppName the mAppName to set
		 */
		public void setAppName(String appName) {
			this.mAppName = appName;
		}

		/**
		 * @return the mSessionId
		 */
		public String getSessionId() {
			return mSessionId;
		}

		/**
		 * @param mSessionId the mSessionId to set
		 */
		public void setSessionId(String sessionId) {
			this.mSessionId = sessionId;
		}

		/**
		 * @return the mPrivileges
		 */
		public List<String> getPrivileges() {
			return mPrivileges;
		}

		/**
		 * @param mPrivileges the mPrivileges to set
		 */
		public void setPrivileges(List<String> privileges) {
			this.mPrivileges = privileges;
		}
		
		
	}
	
	public static class Response {
		
	}
}
