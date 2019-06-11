package com.spier.common.bean.net;

import java.io.Serializable;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 配置信息网络交互数据类
 * @author GHB
 * @version 1.0
 * @date 2019.2.20
 */
public class ConfigsEntity implements Serializable{

	private static final long serialVersionUID = 3380142725151384036L;

	public static class Request {
		@Expose
		@SerializedName("ch")
		private String mChanNo;
		
		@Expose
		@SerializedName("pkg")
		private String mPkgName;
		
		@Expose
		@SerializedName("uid")
		private String mUID;
		
		@Expose
		@SerializedName("sid")
		private String mSessionId;

		/**
		 * @return the mChanNo
		 */
		public String getChanNo() {
			return mChanNo;
		}

		/**
		 * @param mChanNo the mChanNo to set
		 */
		public void setChanNo(String chanNo) {
			this.mChanNo = chanNo;
		}

		/**
		 * @return the mPkgName
		 */
		public String getPkgName() {
			return mPkgName;
		}

		/**
		 * @param mPkgName the mPkgName to set
		 */
		public void setPkgName(String pkgName) {
			this.mPkgName = pkgName;
		}

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
	}
	
	public static class Response {
		@Expose
		@SerializedName("chcfg")
		private Map<String, Boolean> mChanCfgs;
		
		@Expose
		@SerializedName("pkgcfg")
		private Map<String, Boolean> mPkgCfgs;

		/**
		 * @return the mChanCfgs
		 */
		public Map<String, Boolean> getChanCfgs() {
			return mChanCfgs;
		}

		/**
		 * @param mChanCfgs the mChanCfgs to set
		 */
		public void setChanCfgs(Map<String, Boolean> chanCfgs) {
			this.mChanCfgs = chanCfgs;
		}

		/**
		 * @return the mPkgCfgs
		 */
		public Map<String, Boolean> getPkgCfgs() {
			return mPkgCfgs;
		}

		/**
		 * @param mPkgCfgs the mPkgCfgs to set
		 */
		public void setPkgCfgs(Map<String, Boolean> pkgCfgs) {
			this.mPkgCfgs = pkgCfgs;
		}
		
		
	}
}
