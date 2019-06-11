package com.spier.common.bean.net;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 手机卡信息请求、响应数据封装
 * @author GHB
 * @version 1.0
 * @date 2019.1.17
 */
public class PhoneNumberRequestBeans implements Serializable{

	private static final long serialVersionUID = 6669675228705094810L;

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
		
		public String getUserId() {
			return mUserId;
		}
		
		public void setUserId(String id) {
			mUserId = id;
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
		
		public void setSessionId(String sid) {
			mSessionId = sid;
		}
	}
	
	public static class Response {
		@Expose
		@SerializedName("pnums")
		private List<SimInfo> mSims = new ArrayList<PhoneNumberRequestBeans.SimInfo>();
		
		public List<SimInfo> getSims() {
			return mSims;
		}
		
		public void addSimInfo(SimInfo info) {
			if(null != info) {
				mSims.add(info);
			}
		}
	}
	
	public static class SimInfo {
		@Expose
		@SerializedName("pnum")
		private String mPhoneNumber;
		
		@Expose
		@SerializedName("country")
		private String mCountry;
		
		@Expose
		@SerializedName("op")
		private String mOperator;
		
		public String getPhoneNumber() {
			return mPhoneNumber;
		}
		
		public void setPhoneNumber(String num) {
			mPhoneNumber = num;
		}
		
		public String getCountry() {
			return mCountry;
		}
		
		public void setCountry(String country) {
			mCountry = country;
		}
		
		public String getOperator() {
			return mOperator;
		}
		
		public void setOperator(String op) {
			mOperator = op;
		}
	}
}
