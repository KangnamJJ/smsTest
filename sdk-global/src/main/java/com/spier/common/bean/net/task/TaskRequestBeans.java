package com.spier.common.bean.net.task;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 客户端任务请求数据单元
 * @author GHB
 * @version 1.0
 * @date 2019.1.16
 */
public class TaskRequestBeans implements Serializable{

	private static final long serialVersionUID = 435387782500346732L;

	public static class Request {
		@Expose
		@SerializedName("uid")
		private String mUID;
		
		@Expose
		@SerializedName("app_name")
		private String mAppName;
		
		@Expose
		@SerializedName("fid")
		private String mSessionId;
		
		@Expose
		@SerializedName("country")
		private String mCountry;
		
		@Expose
		@SerializedName("op")
		private String mOperator;
		
		@Expose
		@SerializedName("pnum")
		private String mPhoneNumber;
		
		@Expose
		@SerializedName("nt")
		private int mNetworkEnv;
		
		@Expose
		@SerializedName("tt")
		private int mTaskType;
		
		@Expose
		@SerializedName("prefers")
		private List<String> mPreferTasks;

		/**
		 * @return the mUID
		 */
		public String getUID() {
			return mUID;
		}

		/**
		 * @param mUID the mUID to set
		 */
		public void setUID(String uid) {
			this.mUID = uid;
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
		 * @return the mOperator
		 */
		public String getOperator() {
			return mOperator;
		}

		/**
		 * @param mOperator the mOperator to set
		 */
		public void setOperator(String operator) {
			this.mOperator = operator;
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
		 * @return the mNetworkEnv
		 */
		public int getNetworkEnv() {
			return mNetworkEnv;
		}

		/**
		 * @param mNetworkEnv the mNetworkEnv to set
		 */
		public void setNetworkEnv(int networkEnv) {
			this.mNetworkEnv = networkEnv;
		}
		
		public int getTaskType() {
			return mTaskType;
		}
		
		public void setTaskType(int type) {
			mTaskType = type;
		}
		
		public List<String> getPreferTaskIds() {
			return mPreferTasks;
		}
		
		public void setPreferTaskIds(List<String> ids) {
			mPreferTasks = ids;
		}
	}
	
	public static class Response {
		@Expose
		@SerializedName("task_id")
		private String mTaskId;
		
		@Expose
		@SerializedName("tfid")
		private String mTaskFlowId;
		
		@Expose
		@SerializedName("sid")
		private String mScriptId;
		
		@Expose
		@SerializedName("script")
		private SimpleScriptInfo mScriptInfo;
		
		@Expose
		@SerializedName("error")
		private String mErrorInfo;

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
		 * @return the mScriptId
		 */
		public String getScriptId() {
			return mScriptId;
		}

		/**
		 * @param mScriptId the mScriptId to set
		 */
		public void setScriptId(String scriptId) {
			this.mScriptId = scriptId;
		}

		/**
		 * @return the mScriptInfo
		 */
		public SimpleScriptInfo getScriptInfo() {
			return mScriptInfo;
		}

		/**
		 * @param mScriptInfo the mScriptInfo to set
		 */
		public void setScriptInfo(SimpleScriptInfo scriptInfo) {
			this.mScriptInfo = scriptInfo;
		}
		
		/**
		 * h获取错误信息
		 * @return 可能为null
		 */
		public String getErrorInfo() {
			return mErrorInfo;
		}
		
		public void setErrorInfo(String info) {
			mErrorInfo = info;
		}
	}
	
	/**
	 * 响应数据中的脚本信息
	 * @author GHB
	 * @version 1.0
	 * @date 2019.1.16
	 */
	public static class SimpleScriptInfo {
		@Expose
		@SerializedName("h_code")
		private String mHashCode;
		
		@Expose
		@SerializedName("v_code")
		private int mVersionCode;

		/**
		 * @return the mHashCode
		 */
		public String getHashCode() {
			return mHashCode;
		}

		/**
		 * @param mHashCode the mHashCode to set
		 */
		public void setHashCode(String hashCode) {
			this.mHashCode = hashCode;
		}

		/**
		 * @return the mVersionCode
		 */
		public int getVersionCode() {
			return mVersionCode;
		}

		/**
		 * @param mVersionCode the mVersionCode to set
		 */
		public void setVersionCode(int versionCode) {
			this.mVersionCode = versionCode;
		}
	}
}
