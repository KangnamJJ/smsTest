package com.spier.common.bean.db;

import java.io.Serializable;

/**
 * 运营商信息单元
 * @author GHB
 * @version 1.0
 * @date 2019.1.15
 */
public class OperatorInfo implements Serializable{

	private static final long serialVersionUID = -805108083412664621L;
	private String mCountryAbb;
	private String mOpName;
	private String mMCC;
	private String mMNC;
	
	private int mIndex;
	
	/**
	 * 获取国家简称
	 * @return
	 */
	public String getCountryAbb() {
		return mCountryAbb;
	}
	
	/**
	 * 设置国家简称
	 * @param ca
	 */
	public void setCountryAbb(String ca) {
		mCountryAbb = ca;
	}
	
	/**
	 * 获取运营商名称
	 * @return
	 */
	public String getOperatorName() {
		return mOpName;
	}
	
	/**
	 * 设置运营商名称
	 * @param name
	 */
	public void setOperatorName(String name) {
		mOpName = name;
	}
	
	public int getIndex() {
		return mIndex;
	}
	
	public void setIndex(int ind) {
		mIndex = ind;
	}
	
	public String getMCC() {
		return mMCC;
	}
	
	public void setMCC(String mcc) {
		mMCC = mcc;
	}
	
	public String getMNC() {
		return mMNC;
	}
	
	public void setMNC(String mnc) {
		mMNC = mnc;
	}
}
