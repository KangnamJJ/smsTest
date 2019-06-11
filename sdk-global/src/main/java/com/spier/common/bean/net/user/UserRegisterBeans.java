package com.spier.common.bean.net.user;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.spier.common.bean.net.RequestPhoneInfo;


/**
 * 用户注册请求数据单元定义类
 * @author GHB
 * @version 1.0
 * @date 2019.1.1
 */
public class UserRegisterBeans implements Serializable{

	private static final long serialVersionUID = -1184218416033556621L;

	/**
	 * 请求数据单元
	 * @author GHB
	 * @version 1.0
	 * @date 2019.1.1
	 */
	public static class RequestBean {
		@Expose
		@SerializedName("shv")
		private int mShellVerCode;
		
		@Expose
		@SerializedName("app_name")
		private String mAppName;
		
		@Expose
		@SerializedName("phone_info")
		private RequestPhoneInfo mPhoneInfo;

		/**
		 * 获取壳版本号
		 * @return the mShellVerCode
		 */
		public int getShellVerCode() {
			return mShellVerCode;
		}

		/**
		 * 设置壳版本号
		 * @param mShellVerCode the mShellVerCode to set
		 */
		public void setShellVerCode(int shellVerCode) {
			this.mShellVerCode = shellVerCode;
		}
		
		/**
		 * 获取app名称
		 * @return the mAppName
		 */
		public String getAppName() {
			return mAppName;
		}

		/**
		 * 设置app名称
		 * @param mAppName the mAppName to set
		 */
		public void setmAppName(String appName) {
			this.mAppName = appName;
		}

		/**
		 * 获取手机信息
		 * @return the mPhoneInfo
		 */
		public RequestPhoneInfo getPhoneInfo() {
			return mPhoneInfo;
		}

		/**
		 * 设置手机信息
		 * @param mPhoneInfo the mPhoneInfo to set
		 */
		public void setPhoneInfo(RequestPhoneInfo phoneInfo) {
			this.mPhoneInfo = phoneInfo;
		}
	}
	
	/**
	 * 响应数据单元
	 * @author GHB
	 * @version 1.0
	 * @date 2019.1.1
	 */
	public static class ResponseBean {
		
		@Expose
		@SerializedName("uid")
		private String mUID;
		
		@Expose
		@SerializedName("fid")
		private String mFID;

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
		 * @return the mFID
		 */
		public String getFID() {
			return mFID;
		}

		/**
		 * @param mFID the mFID to set
		 */
		public void setFID(String fid) {
			this.mFID = fid;
		}
		
	}
	
	
}
