package com.spier.common.bean.db.referrer;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReferrerBeans implements Serializable{

	private static final long serialVersionUID = -5356346491787343380L;

	public static class ReferrerRequest {
		@Expose
		@SerializedName("uid")
		public String mUID;
		
		@Expose
		@SerializedName("app_name")
		public String mAppName;
		
		public String getAppName() {
			return mAppName;
		}
		
		@Expose
		@SerializedName("referrer")
		public String mReferrer;
	}
}
