package com.spier.common.bean.net.file;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 普通文件信息查询数据单元
 * @author GHB
 * @version 1.0
 * @date 2019.1.3
 */
public class NormalFileInfoQueryBeans implements Serializable{

	private static final long serialVersionUID = -2824226449325051106L;

	/**
	 * 请求参数
	 * @author GHB
	 * @version 1.0
	 * @date 2019.1.3
	 */
	public static class RequestData {
		@Expose
		@SerializedName("uid")
		private String mUserName;
		
		@Expose
		@SerializedName("app_name")
		private String mAppName;
		
		@Expose
		@SerializedName("fid")
		private String mSessionId;
		
		@Expose
		@SerializedName("file_id")
		private String mFileId;
		
		/**
		 * @return the mUserName
		 */
		public String getUserName() {
			return mUserName;
		}
		
		/**
		 * @param mUserName the mUserName to set
		 */
		public void setUserName(String userName) {
			this.mUserName = userName;
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
		public void setmAppName(String appName) {
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
		public void setmSessionId(String sessionId) {
			this.mSessionId = sessionId;
		}
		
		/**
		 * @return the mFileId
		 */
		public String getFileId() {
			return mFileId;
		}
		/**
		 * @param mFileId the mFileId to set
		 */
		public void setFileId(String fileId) {
			this.mFileId = fileId;
		}
		
		
	}
	
	/**
	 * 响应参数
	 * @author GHB
	 * @version 1.0
	 * @date 2019.1.3
	 */
	public static class ResponseData {
		@Expose
		@SerializedName("hash")
		private String mHash;
		
		@Expose
		@SerializedName("ver")
		private int mVer;

		/**
		 * @return the mHash
		 */
		public String getHash() {
			return mHash;
		}

		/**
		 * @param mHash the mHash to set
		 */
		public void setHash(String hash) {
			this.mHash = hash;
		}

		/**
		 * @return the mVer
		 */
		public int getVer() {
			return mVer;
		}

		/**
		 * @param mVer the mVer to set
		 */
		public void setVer(int ver) {
			this.mVer = ver;
		}
		
		
	}
}
