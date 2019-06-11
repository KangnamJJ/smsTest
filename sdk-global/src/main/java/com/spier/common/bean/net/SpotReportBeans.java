package com.spier.common.bean.net;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SpotReportBeans implements Serializable{

	private static final long serialVersionUID = 6734448628656855513L;

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
		@SerializedName("type")
		private int mType;
		
		@Expose
		@SerializedName("tag")
		private String mTag;
		
		@Expose
		@SerializedName("info")
		private String mInfo;

		/**
		 * @return the mUID
		 */
		public String getUID() {
			return mUID;
		}

		/**
		 * @param mUID the mUID to set
		 */
		public void setUID(String uID) {
			this.mUID = uID;
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
		
		public int getType() {
			return mType;
		}
		
		public void setType(int type) {
			mType = type;
		}

		/**
		 * @return the mTag
		 */
		public String getTag() {
			return mTag;
		}

		/**
		 * @param mTag the mTag to set
		 */
		public void setTag(String tag) {
			this.mTag = tag;
		}

		/**
		 * @return the mInfo
		 */
		public String getInfo() {
			return mInfo;
		}

		/**
		 * @param mInfo the mInfo to set
		 */
		public void setInfo(String info) {
			this.mInfo = info;
		}
	}
	
	public static class Response {
		
	}
}
