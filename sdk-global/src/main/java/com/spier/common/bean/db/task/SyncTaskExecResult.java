package com.spier.common.bean.db.task;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;

/**
 * 同步任务的执行结果，区别于自建CPA任务
 * @author GHB
 * @version 1.0
 * @date 2019.2.16
 */
public class SyncTaskExecResult implements Serializable{

	private static final long serialVersionUID = -6016621461577280473L;
	// 主键
	private int mId;
	private String mOfferId;
	private String mUserId;
	private String mChanNo;
	private String mAppName;		// 包名，SDK推广的app的包名
	private int mTaskState;
	private Timestamp mCreateTime;
	private Timestamp mChangeTime;
	
	/**
	 * @return the mId
	 */
	public int getId() {
		return mId;
	}
	
	/**
	 * @param mId the mId to set
	 */
	public void setId(int id) {
		this.mId = id;
	}
	
	/**
	 * @return the mOfferId
	 */
	public String getOfferId() {
		return mOfferId;
	}
	
	/**
	 * @param mOfferId the mOfferId to set
	 */
	public void setOfferId(String offerId) {
		this.mOfferId = offerId;
	}
	
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
	 * @return the mTaskState
	 */
	public int getTaskState() {
		return mTaskState;
	}
	
	/**
	 * @param mTaskState the mTaskState to set
	 */
	public void setTaskState(int taskState) {
		this.mTaskState = taskState;
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
	public void setCreateTime(Timestamp createTime) {
		this.mCreateTime = createTime;
	}
	
	/**
	 * @return the mChangeTime
	 */
	public Timestamp getChangeTime() {
		return mChangeTime;
	}
	
	/**
	 * @param mChangeTime the mChangeTime to set
	 */
	public void setChangeTime(Timestamp changeTime) {
		this.mChangeTime = changeTime;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(null == obj) {
			return false;
		}
		
		if(!(obj instanceof SyncTaskExecResult)) {
			return false;
		}
		
		SyncTaskExecResult other = (SyncTaskExecResult) obj;
		
		if(!StringUtils.equals(mOfferId, other.mOfferId)) {
			return false;
		}
		
		if(!StringUtils.equals(mUserId, other.mUserId)) {
			return false;
		}
		
		if(!StringUtils.equals(mAppName, other.mAppName)) {
			return false;
		}
		
		if(!StringUtils.equals(mChanNo, other.mChanNo)) {
			return false;
		}
		
		if(mTaskState != other.mTaskState) {
			return false;
		}
		
		return true;
	}
	
	
}
