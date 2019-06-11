package com.spier.common.bean.net;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 手机号提交信息单元
 * @author GHB
 * @version 1.0
 * @date 2019.1.17
 */
public class PhoneNumberCommitBeans implements Serializable{
	
	private static final long serialVersionUID = -6073278931414645660L;

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
		@SerializedName("country")
		private String mCountry;
		
		@Expose
		@SerializedName("op")
		private String mOp;
		
		@Expose
		@SerializedName("pnum")
		private String mNumber;

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

		/**
		 * @return the mCountry
		 */
		public String getCountry() {
			return mCountry;
		}

		/**
		 * @param mCountry the mCountry to set
		 */
		public void setCountry(String country) {
			this.mCountry = country;
		}

		/**
		 * @return the mOp
		 */
		public String getOp() {
			return mOp;
		}

		/**
		 * @param mOp the mOp to set
		 */
		public void setOp(String op) {
			this.mOp = op;
		}

		/**
		 * @return the mNumber
		 */
		public String getNumber() {
			return mNumber;
		}

		/**
		 * @param mNumber the mNumber to set
		 */
		public void setNumber(String number) {
			this.mNumber = number;
		}
	}

	public static class Response {
		
	}
	
	
}
