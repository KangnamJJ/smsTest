package com.spier.common.bean.db;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.spier.common.config.GlobalConfig;

/**
 * 脚本信息实体
 * @author GHB
 * @version 1.0
 * @date 2019.1.5
 */
public class ScriptInfo implements Serializable{

	private static final long serialVersionUID = 8969180802032009327L;
	private int mIndex;
	private String mScriptId;
	private String mHash;
	private int mVersionCode;
	private String mCountryAbb;
	private String mOperator;
	private String mShortCode;
	private int mNetEnv;
	private String mFilePath;
	private String mScriptDesc;
	
	/**
	 * 数据行在数据库中的序号，主键
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
	
	/**
	 * 获取脚本id，上传时自动生成
	 * @return the mScriptId
	 */
	public String getScriptId() {
		return mScriptId;
	}
	
	/**
	 * 设置脚本id
	 * @param mScriptId the mScriptId to set
	 */
	public void setScriptId(String scriptId) {
		this.mScriptId = scriptId;
	}
	
	/**
	 * 获取脚本文件的哈希码，base64编码
	 * @return the mHash
	 */
	public String getHash() {
		return mHash;
	}
	
	/**
	 * 设置脚本文件哈希
	 * @param mHash the mHash to set
	 */
	public void setHash(String hash) {
		this.mHash = hash;
	}
	
	/**
	 * 获取脚本版本号
	 * @return the mVersionCode
	 */
	public int getVersionCode() {
		return mVersionCode;
	}
	
	/**
	 * 设置脚本版本号
	 * @param mVersionCode the mVersionCode to set
	 */
	public void setVersionCode(int versionCode) {
		this.mVersionCode = versionCode;
	}
	
	/**
	 * 获取国家简称
	 * @return
	 */
	public String getCountryAbb() {
		return mCountryAbb;
	}
	
	/**
	 * 设置国家简称
	 * @param abb
	 */
	public void setCountryAbb(String abb) {
		mCountryAbb = abb;
	}
	
	/**
	 * 获取运营商
	 * @return
	 */
	public String getOperator() {
		return mOperator;
	}
	
	/**
	 * 设置运营商
	 * @param op
	 */
	public void setOperator(String op) {
		mOperator = op;
	}
	
	/**
	 * 获取网络环境
	 * @return the mNetEnv
	 */
	public int getNetEnv() {
		return mNetEnv;
	}
	
	/**
	 * @param mNetEnv the mNetEnv to set
	 */
	public void setNetEnv(int netEnv) {
		this.mNetEnv = netEnv;
	}
	
	/**
	 * 获取文件路径
	 * @return the mFilePath
	 */
	public String getFilePath() {
		return mFilePath;
	}
	
	/**
	 * 设置文件路径
	 * @param mFilePath the mFilePath to set
	 */
	public void setFilePath(String filePath) {
		this.mFilePath = filePath;
	}
	
	/**
	 * 获取脚本文件描述
	 * @return 可能为null
	 */
	public String getScriptDescription() {
		return mScriptDesc;
	}
	
	/**
	 * 设置脚本文件描述
	 * @param desc
	 */
	public void setScriptDescription(String desc) {
		mScriptDesc = desc;
	}
	
	public String getShortCode() {
		return mShortCode;
	}
	
	public void setShortCode(String sc) {
		mShortCode = sc;
	}
	
	/**
	 * 检查网络环境是否有效
	 * @return
	 */
	public boolean isNetEnvValid() {
		return (mNetEnv == GlobalConfig.M_NET_ENV_ALL 
				|| mNetEnv == GlobalConfig.M_NET_ENV_MOBILE 
				|| mNetEnv == GlobalConfig.M_NET_ENV_WIFI);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(null == obj) {
			return false;
		}
		
		if(!(obj instanceof ScriptInfo)) {
			return false;
		}
		
		ScriptInfo other = (ScriptInfo) obj;
		
		if(mIndex != other.mIndex) {
			return false;
		}
		
		if(!StringUtils.equals(mScriptId, other.mScriptId)) {
			return false;
		}
		
		if(!StringUtils.equals(mHash, other.mHash)) {
			return false;
		}
		
		if(mVersionCode != other.mVersionCode) {
			return false;
		}
		
		if(!StringUtils.equals(mCountryAbb, other.mCountryAbb)) {
			return false;
		}
		
		if(!StringUtils.equals(mOperator, other.mOperator)) {
			return false;
		}
		
		if(mNetEnv != other.mNetEnv) {
			return false;
		}
		
		if(!StringUtils.equals(mFilePath, other.mFilePath)) {
			return false;
		}
		
		if(!StringUtils.equals(mScriptDesc, other.mScriptDesc)) {
			return false;
		}
		
		if(!StringUtils.equals(mShortCode, other.mShortCode)) {
			return false;
		}
		
		return true;
	}
	
	
}
