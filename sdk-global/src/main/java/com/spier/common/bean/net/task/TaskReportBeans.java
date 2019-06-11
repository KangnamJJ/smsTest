package com.spier.common.bean.net.task;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 任务执行结果上报类
 * @author GHB
 * @version 1.0
 * @date 2019.1.16
 */
public class TaskReportBeans implements Serializable{

	private static final long serialVersionUID = -2350354085337462562L;

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
		@SerializedName("tid")
		private String mTaskId;
		
		@Expose
		@SerializedName("tfid")
		private String mTaskFlowId;
		
		@Expose
		@SerializedName("pnum")
		private String mPhoneNumber;
		
		@Expose
		@SerializedName("country")
		private String mCountry;
		
		@Expose
		@SerializedName("op")
		private String mOp;
		
		@Expose
		@SerializedName("curl")
		private String mClickURL;
		
		@Expose
		@SerializedName("succeed")
		private int mSucceed;

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
		 * @return the mTaskId
		 */
		public String getTaskId() {
			return mTaskId;
		}

		/**
		 * @param mTaskId the mTaskId to set
		 */
		public void setTaskId(String taskId) {
			this.mTaskId = taskId;
		}
		
		public String getTaskFlowId() {
			return mTaskFlowId;
		}
		
		public void setTaskFlowId(String id) {
			mTaskFlowId = id;
		}

		/**
		 * @return the mPhoneNumber
		 */
		public String getPhoneNumber() {
			return mPhoneNumber;
		}

		/**
		 * @param mPhoneNumber the mPhoneNumber to set
		 */
		public void setPhoneNumber(String phoneNumber) {
			this.mPhoneNumber = phoneNumber;
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
		 * @return the mSucceed
		 */
		public int getSucceed() {
			return mSucceed;
		}

		/**
		 * @param mSucceed the mSucceed to set
		 */
		public void setSucceed(int succeed) {
			this.mSucceed = succeed;
		}
		
		public String getClickURL() {
			return mClickURL;
		}
		
		public void setClickURL(String curl) {
			mClickURL = curl;
		}
	}
	
	public static class Response {
		
	}
	
}
