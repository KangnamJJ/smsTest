package com.spier.common.bean.db;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

/**
 * 渠道信息
 * @author GHB
 * @version 1.0
 * @date 2018.12.20
 */
public class ChannelInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6208350151201081701L;
	/**
	 * 渠道任务开关打开
	 */
	public static final int M_TASK_SWITCH_ON = 1;
	/**
	 * 渠道任务开关关闭
	 */
	public static final int M_TASK_SWITCH_OFF = 2;
	
	private int mInd;
	private String mChanNo;
	private String mChanDesc;
	private String mRsaPubKB64;
	private String mRsaPrivKB64;
	private int mTaskSwitchState;
	private int mSingleUserMaxTasks;
	private String mChanPostback;
	
	/**
	 * 获取行序列号
	 * @return
	 */
	public int getIndex() {
		return mInd;
	}
	
	/**
	 * 获取渠道号
	 * @return 不为null
	 */
	public String getChannelNo() {
		return mChanNo;
	}
	
	public void setChannelNo(String no) {
		mChanNo = no;
	}
	
	/**
	 * 获取渠道描述信息
	 * @return 可能为null
	 */
	public String getChanDesc() {
		return mChanDesc;
	}
	
	public void setChanDesc(String desc) {
		mChanDesc = desc;
	}
	
	/**
	 * 获取RSA公钥经过Base64编码后的字符串
	 * @return 不为null
	 */
	public String getRsaPubKeyB64Str() {
		return mRsaPubKB64;
	}
	
	public void setRsaPubKB64Str(String str) {
		mRsaPubKB64 = str;
	}
	
	/**
	 * 获取RSA私钥经过Base64编码后的字符串
	 * @return 不为null
	 */
	public String getRsaPrivKeyB64Str() {
		return mRsaPrivKB64;
	}
	
	public void setRsaPrivKB64Str(String str) {
		mRsaPrivKB64 = str;
	}
	
	/**
	 * 获取渠道任务开关状态
	 * @return
	 */
	public int getChanTaskSwitchState() {
		return mTaskSwitchState;
	}
	
	/**
	 * 设置渠道任务开关状态
	 * @param state
	 */
	public void setChanTaskSwitchState(int state) {
		mTaskSwitchState = state;
	}
	
	public void setChanTaskSwitchState(String str) {
		try {
			mTaskSwitchState = Integer.parseInt(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取单个用户最大完成任务数
	 * @return
	 */
	public int getSingleUserMaxTasks() {
		return mSingleUserMaxTasks;
	}
	
	/**
	 * 设置单个用户最大完成任务数
	 * @param max
	 */
	public void setSingelUserMaxTasks(int max) {
		mSingleUserMaxTasks = max;
	}
	
	public void setSingelUserMaxTasksStr(String str) {
		try {
			mSingleUserMaxTasks = Integer.parseInt(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取取到的postback
	 * @return
	 */
	public String getPostback() {
		return mChanPostback;
	}
	
	public void setPostback(String postback) {
		mChanPostback = postback;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(null == obj) {
			return false;
		}
		
		if(!(obj instanceof ChannelInfo)) {
			return false;
		}
		
		ChannelInfo other = (ChannelInfo) obj;
		
		if(mInd != other.mInd) {
			return false;
		}
		
		if(!StringUtils.equals(mChanNo, other.mChanNo)) {
			return false;
		}
		
		if(!StringUtils.equals(mChanDesc, other.mChanDesc)) {
			return false;
		}
		
		if(!StringUtils.equals(mRsaPubKB64, other.mRsaPubKB64)) {
			return false;
		}
		
		if(!StringUtils.equals(mRsaPrivKB64, other.mRsaPrivKB64)) {
			return false;
		}
		
		if(mTaskSwitchState != other.mTaskSwitchState) {
			return false;
		}
		
		if(mSingleUserMaxTasks != other.mSingleUserMaxTasks) {
			return false;
		}
		
		if(!StringUtils.equals(mChanPostback, other.mChanPostback)) {
			return false;
		}
		
		return true;
	}
	
}
