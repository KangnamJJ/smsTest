package com.spier.common.bean.db;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;

/**
 * 手机卡信息单元
 * @author GHB
 * @version 1.0
 * @date 2018.12.30
 */
public class SimInfo implements Serializable{

	private static final long serialVersionUID = 7243545989683077723L;
	private int mId = -1;
	private String mNumber;
	private String mCountry;
	private String mOperator;
	private Timestamp mCreateTime;
	
	/**
	 * 获取索引号
	 * @return the mId
	 */
	public int getId() {
		return mId;
	}
	
	public void setId(int id) {
		mId = id;
	}
	
	/**
	 * 获取手机号
	 * @return the mNumber
	 */
	public String getNumber() {
		return mNumber;
	}
	
	/**
	 * 设置手机号
	 * @param mNumber the mNumber to set
	 */
	public void setNumber(String num) {
		this.mNumber = num;
	}
	
	/**
	 * 获取国家
	 * @return the mCountry
	 */
	public String getCountry() {
		return mCountry;
	}
	
	/**
	 * 设置国家
	 * @param mCountry the mCountry to set
	 */
	public void setCountry(String country) {
		this.mCountry = country;
	}
	
	/**
	 * 获取运营商
	 * @return the mOperator
	 */
	public String getOperator() {
		return mOperator;
	}
	
	/**
	 * 设置运营商
	 * @param mOperator the mOperator to set
	 */
	public void setOperator(String op) {
		this.mOperator = op;
	}
	
	/**
	 * 获取入库时间
	 * @return 可能为null
	 */
	public Timestamp getCreateTime() {
		return mCreateTime;
	}
	
	/**
	 * 设置入库时间
	 * @param time
	 */
	public void setCreateTime(Timestamp time) {
		mCreateTime = time;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(null == obj) {
			return false;
		}
		
		if(!(obj instanceof SimInfo)) {
			return false;
		}
		
		SimInfo other = (SimInfo) obj;
		
		if(mId != other.mId) {
			return false;
		}
		
		if(!StringUtils.equals(mNumber, other.mNumber)) {
			return false;
		}
		
		if(!StringUtils.equals(mCountry, other.mCountry)) {
			return false;
		}
		
		if(!StringUtils.equals(mOperator, other.mOperator)) {
			return false;
		}
		
		return true;
	}
	
}
