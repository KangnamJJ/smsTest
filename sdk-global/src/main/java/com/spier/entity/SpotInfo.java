package com.spier.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

/**
 * 埋点数据
 * @author GHB
 * @version 1.0
 * @date 2019.1.18
 */
public class SpotInfo implements Serializable{

	private static final long serialVersionUID = -6418143796579355849L;
	private int mIndex;
	private String mChanNo;
	private String mAppName;
	private String mUid;
	private String mFlowId;
	private String mTag;
	private int mType;
	private String mInfo;
	private Timestamp mChangeTime;
	
	/**
	 * @return the mIndex
	 */
	public int getIndex() {
		return mIndex;
	}
	/**
	 * @param mIndex the mIndex to set
	 */
	public void setIndex(int index) {
		this.mIndex = index;
	}
	
	public String getUID() {
		return mUid;
	}
	
	public void setUID(String uid) {
		mUid = uid;
	}
	
	public String getChanNo() {
		return mChanNo;
	}
	
	public void setChanNo(String no) {
		mChanNo = no;
	}
	
	public String getAppName() {
		return mAppName;
	}
	
	public void setAppName(String name) {
		mAppName = name;
	}
	
	public String getFlowId() {
		return mFlowId;
	}
	
	public void setFlowId(String fid) {
		mFlowId = fid;
	}
	
	public int getType() {
		return mType;
	}
	
	public void setType(int type) {
		mType = type;
	}
	
	/**
	 * @return the mTag
	 */
	public String getTag() {
		return mTag;
	}
	
	/**
	 * @param mTag the mTag to set
	 */
	public void setTag(String tag) {
		this.mTag = tag;
	}
	
	/**
	 * @return the mInfo
	 */
	public String getInfo() {
		return mInfo;
	}
	
	/**
	 * @param mInfo the mInfo to set
	 */
	public void setInfo(String info) {
		this.mInfo = info;
	}
	
	public Timestamp getChangeTime() {
		return mChangeTime;
	}
	
	public void setChangeTime(Timestamp time) {
		mChangeTime = time;
	}
	
	public void copyFrom(SpotInfo info) {
		if(null == info) {
			return;
		}
		
		setChangeTime(info.getChangeTime());
		setAppName(info.getAppName());
		setChanNo(info.getChanNo());
		setFlowId(info.getFlowId());
		setIndex(info.getIndex());
		setInfo(info.getInfo());
		setTag(info.getTag());
		setType(info.getType());
		setUID(info.getUID());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(null == obj) {
			return false;
		}
		
		if(!(obj instanceof SpotInfo)) {
			return false;
		}
		
		SpotInfo other = (SpotInfo) obj;
		
		if(mIndex != other.mIndex) {
			return false;
		}
		
		if(!StringUtils.equals(mChanNo, other.mChanNo)) {
			return false;
		}
		
		if(!StringUtils.equals(mAppName, other.mAppName)) {
			return false;
		}
		
		if(!StringUtils.equals(mUid, other.mUid)) {
			return false;
		}
		
		if(!StringUtils.equals(mFlowId, other.mFlowId)) {
			return false;
		}
		
		if(!StringUtils.equals(mTag, other.mTag)) {
			return false;
		}
		
		if(mType != other.mType) {
			return false;
		}
		
		if(!StringUtils.equals(mInfo, other.mInfo)) {
			return false;
		}
		
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
	
}
