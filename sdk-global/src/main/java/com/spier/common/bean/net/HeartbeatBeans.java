package com.spier.common.bean.net;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 心跳包
 * @author GHB
 * @version 1.0
 * @date 2019.3.10
 */
public class HeartbeatBeans implements Serializable{

	private static final long serialVersionUID = 3962430990866239620L;

	@Expose
	@SerializedName("pkg")
	private String mPkg;
	
	@Expose
	@SerializedName("uid")
	private String mUID;
	
	@Expose
	@SerializedName("vd")
	private String mShellVersion;

	/**
	 * @return the mPkg
	 */
	public String getPkg() {
		return mPkg;
	}

	/**
	 * @param mPkg the mPkg to set
	 */
	public void setPkg(String pkg) {
		this.mPkg = pkg;
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
	 * @return the mShellVersion
	 */
	public String getShellVersion() {
		return mShellVersion;
	}

	/**
	 * @param mShellVersion the mShellVersion to set
	 */
	public void setShellVersion(String shellVersion) {
		this.mShellVersion = shellVersion;
	}
	
	
}
