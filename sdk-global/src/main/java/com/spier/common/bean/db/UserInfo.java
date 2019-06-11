package com.spier.common.bean.db;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;

import com.spier.common.utils.PhoneInfoUtil;

/**
 * 用户信息单元
 * @author GHB
 * @version
 * @date 2018.12.30
 */
public class UserInfo implements Serializable{

	private static final long serialVersionUID = -7900157225987816773L;

	public static final String M_INVALID_NAME = PhoneInfoUtil.M_ERR_IDENTIFY_STR;
	
	/**
	 * 用户状态，未注册：{@value}
	 */
	public static final int M_STATE_UNREGSITER = 0;
	/**
	 * 用户状态，已注册未登陆：{@value}
	 */
	public static final int M_STATE_UNLOGIN = 1;
	/**
	 * 用户状态，已注册已登陆：{@value}
	 */
	public static final int M_STATE_LOGIN = 2;
	/**
	 * 用户状态，忠实用户：{@value}
	 */
	public static final int M_STATE_LOYALTY = 3;
	
	public static final int M_LOYALTY_USER = 1;
	
	public static final int M_NORMAL_USER = 0;
	
	private int mId;
	private String mName;
	private String mAppName;
	private String mChNo;
	private int mState;
	private String mSessionId;
	private String mSessionEstablishTime;
	// 权限集合，用逗号分割
	private String mPrivileges;
//	private Timestamp mCreateTime;
	private int mIsLoyalty;
	
	private boolean mPhoneInfoValid;
	private String mPhoneInfoInvalidReason;
	
	// 下面两个是关联另外两个数据库的
	private String mPhoneInfoId;
	private String mPhoneNumber;
	
	private String mShellVersion;
	private Timestamp mCreateTime;
	private Timestamp mRecentHandshake;
	private String mIp;
	private String mCountry;
	
	private Timestamp mRecentTaskTime;
	// 定义同通用
	private int mRecentNetType;
	// 是否出现过手机流量环境，0：没有-1：有
	private int mMobileTypeAppear;

	public String getIp() {
		return mIp;
	}

	public void setIp(String mIp) {
		this.mIp = mIp;
	}

	public String getCountry() {
		return mCountry;
	}

	public void setCountry(String mCountry) {
		this.mCountry = mCountry;
	}

	/**
	 * @return the mId
	 */
	public int getId() {
		return mId;
	}
	
	public void setId(int id) {
		mId = id;
	}
	
	/**
	 * 用户名后台根据手机信息自动生成，然后下发到客户端
	 * @return the mName
	 */
	public String getName() {
		return mName;
	}
	
	/**
	 * @param mName the mName to set
	 */
	public void setName(String name) {
		this.mName = name;
	}
	
	/**
	 * 获取应用名称
	 * @return
	 */
	public String getAppName() {
		return mAppName;
	}
	
	/**
	 * 设置应用名称
	 * @param appName
	 */
	public void setAppName(String appName) {
		mAppName = appName;
	}
	
	public String getChannelNo() {
		return mChNo;
	}
	
	public void setChannelNo(String chNo) {
		mChNo = chNo;
	}
	
	/**
	 * 用户状态<br>
	 * {@link #M_STATE_UNREGSITER}<br>
	 * {@link #M_STATE_UNLOGIN}<br>
	 * {@link #M_STATE_LOGIN}
	 * @return the mState
	 */
	public int getState() {
		return mState;
	}
	
	/**
	 * 设置用户状态
	 * @param mState 用户状态 {@link #M_STATE_UNREGSITER} {@link #M_STATE_UNLOGIN} {@link #M_STATE_LOGIN}
	 */
	public void setState(int state) {
		this.mState = state;
	}
	
	/**
	 * 获取会话id，每次登陆时由后台生成，下发给客户端。每次会话建立后12小时要重新登陆
	 * @return the mSessionId
	 */
	public String getSessionId() {
		return mSessionId;
	}
	
	/**
	 * 设置会话id
	 * @param mSessionId the mSessionId to set
	 */
	public void setSessionId(String sid) {
		this.mSessionId = sid;
	}
	
	/**
	 * @return the mIsLoyalty
	 */
	public int getIsLoyalty() {
		return mIsLoyalty;
	}

	/**
	 * @param mIsLoyalty the mIsLoyalty to set
	 */
	public void setIsLoyalty(int isLoyalty) {
		this.mIsLoyalty = isLoyalty;
	}

	/**
	 * 获取会话建立时间，格式为：yyyy-MM-dd HH:mm:ss
	 * @return 可能为null
	 */
	public String getSessionEstablishTime() {
		return mSessionEstablishTime;
	}
	
	/**
	 * 设置会话建立时间 格式为：yyyy-MM-dd HH:mm:ss
	 * @param mSessionEstablishTime the mSessionEstablishTime to set
	 */
	public void setSessionEstablishTime(String sessionEstablishTime) {
		this.mSessionEstablishTime = sessionEstablishTime;
	}
	
//	/**
//	 * 获取入库时间
//	 * @return 不为null
//	 */
//	public Timestamp getCreateTime() {
//		return mCreateTime;
//	}
//	
//	/**
//	 * 设置入库时间
//	 * @param mCreateTime the mCreateTime to set
//	 */
//	public void setCreateTime(Timestamp createTime) {
//		this.mCreateTime = createTime;
//	}
	
	/**
	 * 获取此用户的手机信息的id
	 * @return 
	 */
	public String getPhoneInfoId() {
		return mPhoneInfoId;
	}
	
	/**
	 * 设置用户手机信息的主键
	 * @param mPhone the mPhone to set
	 */
	public void setPhoneInfoId(String phoneId) {
		this.mPhoneInfoId = phoneId;
	}
	
	/**
	 * 获取手机号码
	 * @return
	 */
	public String getPhoneNumber() {
		return mPhoneNumber;
	}
	
	/**
	 * 设置手机号码
	 * @param mSimcard the mSimcard to set
	 */
	public void setPhoneNumber(String number) {
		this.mPhoneNumber = number;
	}
	
	/**
	 * 此用户的手机信息是否有效
	 * @return
	 */
	public boolean isPhoneInfoValid() {
		return mPhoneInfoValid;
	}
	
	public void setPhoneInfoValid(boolean valid) {
		mPhoneInfoValid = valid;
	}
	
	/**
	 * 获取此用户手机信息无效的原因
	 * @return 可能为null
	 */
	public String getPhoneInfoInvalidReason() {
		return mPhoneInfoInvalidReason;
	}
	
	public void setPhoneInfoInvalidReason(String reason) {
		mPhoneInfoInvalidReason = reason;
	}
	
	public String getShellVersion() {
		return mShellVersion;
	}
	
	public void setShellVersion(String shellVersion) {
		mShellVersion = shellVersion;
	}
	
	public Timestamp getRecentHandshake() {
		return mRecentHandshake;
	}
	
	public void setRecentHandshake(Timestamp handshake) {
		mRecentHandshake = handshake;
	}
	
	public Timestamp getCreateTime() {
		return mCreateTime;
	}
	
	public void setCreateTime(Timestamp time) {
		mCreateTime = time;
	}
	
	public String getPrivileges() {
		return mPrivileges;
	}
	
	public void setPrivileges(String privs) {
		mPrivileges = privs;
	}
	
	/**
	 * 用户名称是否有效
	 * @return
	 */
	public boolean isUserNameValid() {
		if(StringUtils.isEmpty(getName())) {
			return false;
		}
		
		if(StringUtils.equals(getName(), M_INVALID_NAME)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * @return the mRecentTaskTime
	 */
	public Timestamp getRecentTaskTime() {
		return mRecentTaskTime;
	}

	/**
	 * @param mRecentTaskTime the mRecentTaskTime to set
	 */
	public void setRecentTaskTime(Timestamp recentTaskTime) {
		this.mRecentTaskTime = recentTaskTime;
	}

	/**
	 * @return the mCrtNetType
	 */
	public int getCrtNetType() {
		return mRecentNetType;
	}

	/**
	 * @param mRecentNetType the mCrtNetType to set
	 */
	public void setCrtNetType(int crtNetType) {
		this.mRecentNetType = crtNetType;
	}

	/**
	 * @return the mMobileTypeAppear
	 */
	public int getMobileTypeAppear() {
		return mMobileTypeAppear;
	}

	/**
	 * @param mMobileTypeAppear the mMobileTypeAppear to set
	 */
	public void setMobileTypeAppear(int mobileTypeAppear) {
		this.mMobileTypeAppear = mobileTypeAppear;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(null == obj) {
			return false;
		}
		
		if(!(obj instanceof UserInfo)) {
			return false;
		}
		
		UserInfo other = (UserInfo) obj;
		
		if(mId != other.mId) {
			return false;
		}
		
		if(!StringUtils.equals(mName, other.mName)) {
			return false;
		}
		
		if(!StringUtils.equals(mAppName, other.mAppName)) {
			return false;
		}
		
		if(mState != other.mState) {
			return false;
		}
		
		if(mIsLoyalty != other.mIsLoyalty) {
			return false;
		}
		
		if(!StringUtils.equals(mSessionId, other.mSessionId)) {
			return false;
		}
		
		if(!StringUtils.equals(mSessionEstablishTime, other.mSessionEstablishTime)) {
			return false;
		}
		
		if(!StringUtils.equals(mPhoneInfoId, other.mPhoneInfoId)) {
			return false;
		}
		
		if(!StringUtils.equals(mPhoneNumber, other.mPhoneNumber)) {
			return false;
		}
		
		if(!StringUtils.equals(mChNo, other.mChNo)) {
			return false;
		}
		
		if(!StringUtils.equals(mShellVersion, other.mShellVersion)) {
			return false;
		}
		
		if(!StringUtils.equals(mPrivileges, other.mPrivileges)) {
			return false;
		}

		if(!StringUtils.equals(mCountry, other.mCountry)) {
			return false;
		}

		if(!StringUtils.equals(mIp, other.mIp)) {
			return false;
		}
		
		if(mRecentTaskTime != other.mRecentTaskTime) {
			return false;
		}
		
		if(mRecentNetType != other.mRecentNetType) {
			return false;
		}
		
		if(mMobileTypeAppear != other.mMobileTypeAppear) {
			return false;
		}
		
		return true;
	}
	
}
