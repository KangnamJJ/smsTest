package com.spier.common.bean.db.referrer;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;

import com.spier.common.config.ReferrerConfig;

public class ReferrerInfo implements Serializable{

	private static final long serialVersionUID = -1840451497391229060L;
	public int mInd;
	public String mChan;
	public String mApp;
	public String mUID;
	public String mReferrer;
	public String mIP;
	public int mNotified;
	public Timestamp mCreateTime;
	
	/**
	 * @return the mInd
	 */
	public int getInd() {
		return mInd;
	}
	
	/**
	 * @param mInd the mInd to set
	 */
	public void setInd(int mInd) {
		this.mInd = mInd;
	}
	
	/**
	 * @return the mChan
	 */
	public String getChan() {
		return mChan;
	}
	
	/**
	 * @param mChan the mChan to set
	 */
	public void setChan(String mChan) {
		this.mChan = mChan;
	}
	
	/**
	 * @return the mApp
	 */
	public String getApp() {
		return mApp;
	}
	/**
	 * @param mApp the mApp to set
	 */
	public void setApp(String mApp) {
		this.mApp = mApp;
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
	public void setUID(String mUID) {
		this.mUID = mUID;
	}
	/**
	 * @return the mReferrer
	 */
	public String getReferrer() {
		return mReferrer;
	}
	/**
	 * @param mReferrer the mReferrer to set
	 */
	public void setReferrer(String mReferrer) {
		this.mReferrer = mReferrer;
	}
	/**
	 * @return the mCreateTime
	 */
	public Timestamp getCreateTime() {
		return mCreateTime;
	}
	/**
	 * @param mCreateTime the mCreateTime to set
	 */
	public void setCreateTime(Timestamp mCreateTime) {
		this.mCreateTime = mCreateTime;
	}
	
	public String getIP() {
		return mIP;
	}
	
	public void setIP(String ip) {
		mIP = ip;
	}
	
	public boolean hasNotified() {
		return mNotified != ReferrerConfig.M_UNNOTIFIED_VALUE;
	}
	
	public void setNotified(int state) {
		mNotified = state;
	}
	
	public int getNotifiedState() {
		return mNotified;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(null == obj) {
			return false;
		}
		
		if(!(obj instanceof ReferrerInfo)) {
			return false;
		}
		
		ReferrerInfo other = (ReferrerInfo) obj;
		
		if(mInd != other.mInd) {
			return false;
		}
		
		if(!StringUtils.equals(mChan, other.mChan)) {
			return false;
		}
		
		if(!StringUtils.equals(mApp, other.mApp)) {
			return false;
		}
		
		if(!StringUtils.equals(mUID, other.mUID)) {
			return false;
		}
		
		if(mNotified != other.mNotified) {
			return false;
		}
		
		return true;
	}
	
	public void clone(ReferrerInfo other) {
		if(null == other) {
			return;
		}
		
		other.mApp = mApp;
		other.mChan = mChan;
		other.mCreateTime = mCreateTime;
		other.mInd = mInd;
		other.mIP = mIP;
		other.mNotified = mNotified;
		other.mReferrer = mReferrer;
		other.mUID = mUID;
	}

	public static ReferrerInfo fromRequest(String ip, String chan, ReferrerBeans.ReferrerRequest request) {
		if(null == request) {
			return null;
		}
		
		ReferrerInfo res = new ReferrerInfo();
		res.setApp(request.mAppName);
		res.setChan(chan);
		res.setReferrer(request.mReferrer);
		res.setUID(request.mUID);
		res.setIP(ip);
		
		return res;
	}
}
