package com.spier.common.bean.db;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.spier.common.utils.PhoneInfoUtil;

/**
 * 手机信息单元
 * @author GHB
 * @version 1.0
 */
public class PhoneInfo implements Serializable{
	private static final long serialVersionUID = -1065371962687702053L;
	private int mId;
	private String mMac;
	private String mIMEI;
	private String mIMSI;
	private String mSerialNo;
	private String mAndroidId;
	private int mScreenWidth;
	private int mScreenHeight;
	private String mBrand;
	private String mModel;
	private int mApiLevel;
	private String mCountryAbb;
	private String mOperator;
	private String mFingerPrint;
	private String mUserAgent;
	
	// 根据以上信息生成此手机的唯一标识
	private String mIdentifyStr = PhoneInfoUtil.M_ERR_IDENTIFY_STR;
	
	/**
	 * 获取表中的索引值
	 * @return the mId
	 */
	public int getId() {
		return mId;
	}
	
	public void setId(int id) {
		mId = id;
	}
	
	/**
	 * 获取mac地址
	 * @return the mMac
	 */
	public String getMac() {
		return mMac;
	}
	
	/**
	 * 设置mac地址
	 * @param mMac the mMac to set
	 */
	public void setMac(String mac) {
		this.mMac = mac;
	}
	
	/**
	 * 获取IMEI值
	 * @return the mIMEI
	 */
	public String getIMEI() {
		return mIMEI;
	}
	
	/**
	 * 设置imei
	 * @param mIMEI the mIMEI to set
	 */
	public void setIMEI(String imei) {
		this.mIMEI = imei;
	}
	
	/**
	 * 获取imsi
	 * @return the mIMSI
	 */
	public String getIMSI() {
		return mIMSI;
	}
	
	/**
	 * 设置imsi
	 * @param mIMSI the mIMSI to set
	 */
	public void setIMSI(String imsi) {
		this.mIMSI = imsi;
	}
	
	/**
	 * 获取sn
	 * @return the mSerialNo
	 */
	public String getSerialNo() {
		return mSerialNo;
	}
	
	/**
	 * 设置sn
	 * @param mSerialNo the mSerialNo to set
	 */
	public void setSerialNo(String sn) {
		this.mSerialNo = sn;
	}
	
	public String getAndroidId() {
		return mAndroidId;
	}
	
	public void setAndroidId(String aid) {
		mAndroidId = aid;
	}
	
	/**
	 * 获取屏幕宽度pixel
	 * @return the mScreenWidth
	 */
	public int getScreenWidth() {
		return mScreenWidth;
	}
	
	/**
	 * 设置屏幕宽度
	 * @param pixel
	 */
	public void setScreenWidth(int width) {
		this.mScreenWidth = width;
	}
	
	/**
	 * 获取屏幕高度
	 * @return pixel
	 */
	public int getScreenHeight() {
		return mScreenHeight;
	}
	
	/**
	 * 设置屏幕高度
	 * @param mScreenHeight the mScreenHeight to set
	 */
	public void setScreenHeight(int height) {
		this.mScreenHeight = height;
	}
	
	/**
	 * 获取品牌
	 * @return the mBrand
	 */
	public String getBrand() {
		return mBrand;
	}
	
	/**
	 * 设置品牌
	 * @param mBrand the mBrand to set
	 */
	public void setBrand(String brand) {
		this.mBrand = brand;
	}
	
	/**
	 * 获取型号
	 * @return the mModel
	 */
	public String getModel() {
		return mModel;
	}
	
	/**
	 * 设置型号
	 * @param mModel the mModel to set
	 */
	public void setModel(String model) {
		this.mModel = model;
	}
	
	/**
	 * 获取apilevel
	 * @return the mApiLevel
	 */
	public int getApiLevel() {
		return mApiLevel;
	}
	
	/**
	 * 设置apilevel
	 * @param mApiLevel the mApiLevel to set
	 */
	public void setApiLevel(int level) {
		this.mApiLevel = level;
	}
	
	public String getCountryAbb() {
		return mCountryAbb;
	}
	
	public void setCountryAbb(String abb) {
		mCountryAbb = abb;
	}
	
	public String getOperator() {
		return mOperator;
	}
	
	public void setOperator(String op) {
		mOperator = op;
	}
	
	/**
	 * 获取指纹
	 * @return
	 */
	public String getFingerPrint() {
		return mFingerPrint;
	}
	
	/**
	 * 设置指纹
	 * @param print
	 */
	public void setFingerPrint(String print) {
		mFingerPrint = print;
	}
	
	/**
	 * 获取ua
	 * @return
	 */
	public String getUserAgent() {
		return mUserAgent;
	}
	
	/**
	 * 设置ua
	 * @param ua
	 */
	public void setUserAgent(String ua) {
		mUserAgent = ua;
	}
	
	/**
	 * 获取身份唯一标识
	 * @return 不为null，错误为Error
	 */
	public String getIdentifyStr() {
		return mIdentifyStr;
	}
	
	/**
	 * 设置手机身份的唯一标识
	 * @param str
	 */
	public void setIdentifyStr(String str) {
		if(null != str) {
			mIdentifyStr = str;
		}
	}
	
	/**
	 * 生成手机信息唯一标识
	 */
	public void generateIdentifyStr() {
		mIdentifyStr = PhoneInfoUtil.generatePhoneIdentifyStr(this);
	}
	
	public boolean isIdentifyValid() {
		if(StringUtils.isEmpty(getIdentifyStr())) {
			return false;
		}
		
		if(StringUtils.equals(getIdentifyStr(), PhoneInfoUtil.M_ERR_IDENTIFY_STR)) {
			return false;
		}
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(null == obj) {
			return false;
		}
		
		if(!(obj instanceof PhoneInfo)) {
			return false;
		}
		
		PhoneInfo other = (PhoneInfo) obj;
		
		if(getId() != other.getId()) {
			return false;
		}
		
		if(!StringUtils.equals(getMac(), other.getMac())) {
			return false;
		}
		
		if(!StringUtils.equals(getIMEI(), other.getIMEI())) {
			return false;
		}
		
		if(!StringUtils.equals(getIMSI(), other.getIMSI())) {
			return false;
		}
		
		if(!StringUtils.equals(getSerialNo(), other.getSerialNo())) {
			return false;
		}
		
		if(getScreenWidth() != other.getScreenWidth()) {
			return false;
		}
		
		if(getScreenHeight() != other.getScreenHeight()) {
			return false;
		}
		
		if(!StringUtils.equals(getBrand(), other.getBrand())) {
			return false;
		}
		
		if(!StringUtils.equals(getModel(), other.getModel())) {
			return false;
		}
		
		if(getApiLevel() != other.getApiLevel()) {
			return false;
		}
		
		if(!StringUtils.equals(getFingerPrint(), other.getFingerPrint())) {
			return false;
		}
		
		if(!StringUtils.equals(getUserAgent(), other.getUserAgent())) {
			return false;
		}
		
		if(!StringUtils.equals(getIdentifyStr(), other.getIdentifyStr())) {
			return false;
		}
		
		if(!StringUtils.equals(getAndroidId(), other.getAndroidId())) {
			return false;
		}
		
		if(!StringUtils.equals(mCountryAbb, other.mCountryAbb)) {
			return false;
		}
		
		if(!StringUtils.equals(mOperator, other.mOperator)) {
			return false;
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		PhoneInfo res = new PhoneInfo();
		res.mId = mId;
		res.mApiLevel = mApiLevel;
		res.mBrand = mBrand;
		res.mFingerPrint = mFingerPrint;
		res.mIMEI = mIMEI;
		res.mIMSI = mIMSI;
		res.mMac = mMac;
		res.mAndroidId = mAndroidId;
		res.mModel = mModel;
		res.mScreenHeight = mScreenHeight;
		res.mScreenWidth = mScreenWidth;
		res.mSerialNo = mSerialNo;
		res.mUserAgent = mUserAgent;
		res.mIdentifyStr = mIdentifyStr;
		res.mCountryAbb = mCountryAbb;
		res.mOperator = mOperator;
		
		return res;
	}
	
	/**
	 * 由参数所示对象生成一个新对象
	 * @param info
	 * @return 可能为null
	 */
	public static PhoneInfo clone(PhoneInfo info) {
		if(null == info) {
			return null;
		}
		
		PhoneInfo res = null;
		try {
			res = (PhoneInfo) info.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return res;
	}
}
