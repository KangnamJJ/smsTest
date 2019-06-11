package com.spier.common.bean.net;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 网络请求的手机信息数据单元
 * @author GHB
 * @version 1.0
 * @date 2019.1.1
 */
public class RequestPhoneInfo implements Serializable{

	private static final long serialVersionUID = -111479358111001769L;

	@Expose
	@SerializedName("MAC")
	private String mMac;
	
	@Expose
	@SerializedName("IMEI")
	private String mIMEI;
	
	@Expose
	@SerializedName("IMSI")
	private String mIMSI;
	
	@Expose
	@SerializedName("SN")
	private String mSerialNo;
	
	@Expose
	@SerializedName("SW")
	private int mScreenWidth;
	
	@Expose
	@SerializedName("SH")
	private int mScreenHeight;
	
	@Expose
	@SerializedName("BRAND")
	private String mBrand;
	
	@Expose
	@SerializedName("MODEL")
	private String mModel;
	
	@Expose
	@SerializedName("API")
	private int mApiLevel;
	
	@Expose
	@SerializedName("FP")
	private String mFingerPrint;
	
	@Expose
	@SerializedName("UA")
	private String mUserAgent;
	
	@Expose
	@SerializedName("ANDROID_ID")
	private String mAndroidId;
	
	/**
	 * @return the mMac
	 */
	public String getMac() {
		return mMac;
	}
	
	/**
	 * @param mMac the mMac to set
	 */
	public void setMac(String mac) {
		this.mMac = mac;
	}
	
	/**
	 * @return the mIMEI
	 */
	public String getIMEI() {
		return mIMEI;
	}
	
	/**
	 * @param mIMEI the mIMEI to set
	 */
	public void setIMEI(String imei) {
		this.mIMEI = imei;
	}
	
	/**
	 * @return the mIMSI
	 */
	public String getIMSI() {
		return mIMSI;
	}
	
	/**
	 * @param mIMSI the mIMSI to set
	 */
	public void setIMSI(String imsi) {
		this.mIMSI = imsi;
	}
	
	/**
	 * @return the mSerialNo
	 */
	public String getSerialNo() {
		return mSerialNo;
	}
	
	/**
	 * @param mSerialNo the mSerialNo to set
	 */
	public void setSerialNo(String serialNo) {
		this.mSerialNo = serialNo;
	}
	
	/**
	 * @return the mScreenWidth
	 */
	public int getScreenWidth() {
		return mScreenWidth;
	}
	
	/**
	 * @param mScreenWidth the mScreenWidth to set
	 */
	public void setScreenWidth(int screenWidth) {
		this.mScreenWidth = screenWidth;
	}
	
	/**
	 * @return the mScreenHeight
	 */
	public int getScreenHeight() {
		return mScreenHeight;
	}
	
	/**
	 * @param mScreenHeight the mScreenHeight to set
	 */
	public void setScreenHeight(int screenHeight) {
		this.mScreenHeight = screenHeight;
	}
	
	/**
	 * @return the mBrand
	 */
	public String getBrand() {
		return mBrand;
	}
	
	/**
	 * @param mBrand the mBrand to set
	 */
	public void setBrand(String brand) {
		this.mBrand = brand;
	}
	
	/**
	 * @return the mModel
	 */
	public String getModel() {
		return mModel;
	}
	
	/**
	 * @param mModel the mModel to set
	 */
	public void setModel(String model) {
		this.mModel = model;
	}
	
	/**
	 * @return the mApiLevel
	 */
	public int getApiLevel() {
		return mApiLevel;
	}
	
	/**
	 * @param mApiLevel the mApiLevel to set
	 */
	public void setApiLevel(int apiLevel) {
		this.mApiLevel = apiLevel;
	}
	
	/**
	 * @return the mFingerPrint
	 */
	public String getFingerPrint() {
		return mFingerPrint;
	}
	
	/**
	 * @param mFingerPrint the mFingerPrint to set
	 */
	public void setFingerPrint(String fingerPrint) {
		this.mFingerPrint = fingerPrint;
	}
	
	/**
	 * @return the mUserAgent
	 */
	public String getUserAgent() {
		return mUserAgent;
	}
	
	/**
	 * @param mUserAgent the mUserAgent to set
	 */
	public void setUserAgent(String userAgent) {
		this.mUserAgent = userAgent;
	}
	
	public String getAndroidId() {
		return mAndroidId;
	}
	
	public void setAndroidId(String aid) {
		mAndroidId = aid;
	}
	
	/**
	 * 检查信息是否合法
	 * @return
	 */
	public boolean isInfoValid() {
		if(!StringUtils.isEmpty(getIMEI())) {
			return true;
		}
		
		if(!StringUtils.isEmpty(getMac())) {
			return true;
		}
		
		if(!StringUtils.isEmpty(getSerialNo())) {
			return true;
		}
		
		if(!StringUtils.isEmpty(getAndroidId())) {
			return true;
		}
		
		return false;
	}
}
