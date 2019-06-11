package com.spier.common.bean.db;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;

/**
 * CPI Offer信息
 * @author GHB
 * @version 1.0
 * @date 2019.2.13
 */
public class CPIOfferInfo implements Serializable{
//	public int mIndex;
	
	private static final long serialVersionUID = -3825085288178800274L;
	public Timestamp mCreateTime;
	public Timestamp mChangeTime;
	
	/**
	 * Offer的唯一标识id，主键
	 */
	public int mOfferId;
	
	/**
	 * Offer的名字
	 */
	public String mOfferName;
	
	/**
	 * Offer的分类，逗号分割
	 */
	public String mCategories;
	
	/**
	 * Offer的描述
	 */
	public String mDesc;
	
	/**
	 * apk版本要求
	 */
	public String mVersion;
	
	/**
	 * 是否激励，0——激励，1-非激励
	 */
	public String mIncent;
	
	/**
	 * 点击跳转链接
	 */
	public String mTrackLink;
	
	/**
	 * 预览链接
	 */
	public String mPreviewLink;
	
	/**
	 * Offer转化流程描述
	 */
	public String mFlow;
	
	/**
	 * 推广市场
	 */
	public String mMarket;
	
	/**
	 * 是否需要设备信息
	 */
	public String mNeedDeviceInfo;
	
	/**
	 * 货币，一般是USD
	 */
	public String mCurrency;
	
	/**
	 * 操作系统列表,IOS或ANDROID，逗号分割
	 */
	public String mPlatforms;
	
	/**
	 * 支持的国家列表，逗号分割
	 */
	public String mCountries;
	
	/**
	 * 价格
	 */
	public double mPayout;
	
	/**
	 * CPI/CPA/CPM/CPC/CPE/CPS/SmartLink
	 */
	public String mPayoutType;
	
	/**
	 * 每日限制的转化数
	 */
	public int mCap;
	
	/**
	 * 允许的网络类型，逗号分割
	 */
	public String mAllowNetworkTypes;
	
	/**
	 * 拒绝的网络类型，逗号分割
	 */
	public String mForbiddenNetworkTypes;
	
	/**
	 * 子渠道黑名单列表，逗号分割
	 */
	public String mSubAffBlacklist;
	
	
//	/**
//	 * @return the mIndex
//	 */
//	public int getIndex() {
//		return mIndex;
//	}
//
//
//	/**
//	 * @param mIndex the mIndex to set
//	 */
//	public void setIndex(int index) {
//		this.mIndex = index;
//	}
	
	public CPIOfferInfo() {
		
	}

	public CPIOfferInfo(CPIOfferInfo other) {
		if(null != other) {
			mCreateTime = other.mCreateTime;
			mChangeTime = other.mChangeTime;
			mOfferId = other.mOfferId;
			mOfferName = other.mOfferName;
			mCategories = other.mCategories;
			mDesc = other.mDesc;
			mVersion = other.mVersion;
			mIncent = other.mIncent;
			mTrackLink = other.mTrackLink;
			mPreviewLink = other.mPreviewLink;
			mFlow = other.mFlow;
			mMarket = other.mMarket;
			mNeedDeviceInfo = other.mNeedDeviceInfo;
			mCurrency = other.mCurrency;
			mPlatforms = other.mPlatforms;
			mCountries = other.mCountries;
			mPayout = other.mPayout;
			mPayoutType = other.mPayoutType;
			mCap = other.mCap;
			mAllowNetworkTypes = other.mAllowNetworkTypes;
			mForbiddenNetworkTypes = other.mForbiddenNetworkTypes;
			mSubAffBlacklist = other.mSubAffBlacklist;
		}
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


	/**
	 * @return the mOfferId
	 */
	public int getOfferId() {
		return mOfferId;
	}


	/**
	 * @param mOfferId the mOfferId to set
	 */
	public void setOfferId(int offerId) {
		this.mOfferId = offerId;
	}


	/**
	 * @return the mOfferName
	 */
	public String getOfferName() {
		return mOfferName;
	}


	/**
	 * @param mOfferName the mOfferName to set
	 */
	public void setOfferName(String offerName) {
		this.mOfferName = offerName;
	}


	/**
	 * @return the mCategories
	 */
	public String getCategories() {
		return mCategories;
	}


	/**
	 * @param mCategories the mCategories to set
	 */
	public void setCategories(String categories) {
		this.mCategories = categories;
	}


	/**
	 * @return the mDesc
	 */
	public String getDesc() {
		return mDesc;
	}


	/**
	 * @param mDesc the mDesc to set
	 */
	public void setDesc(String desc) {
		this.mDesc = desc;
	}


	/**
	 * @return the mVersion
	 */
	public String getVersion() {
		return mVersion;
	}


	/**
	 * @param mVersion the mVersion to set
	 */
	public void setVersion(String version) {
		this.mVersion = version;
	}


	/**
	 * @return the mIncent
	 */
	public String getIncent() {
		return mIncent;
	}


	/**
	 * @param mIncent the mIncent to set
	 */
	public void setIncent(String incent) {
		this.mIncent = incent;
	}


	/**
	 * @return the mTrackLink
	 */
	public String getTrackLink() {
		return mTrackLink;
	}


	/**
	 * @param mTrackLink the mTrackLink to set
	 */
	public void setTrackLink(String trackLink) {
		this.mTrackLink = trackLink;
	}


	/**
	 * @return the mPreviewLink
	 */
	public String getPreviewLink() {
		return mPreviewLink;
	}


	/**
	 * @param mPreviewLink the mPreviewLink to set
	 */
	public void setPreviewLink(String previewLink) {
		this.mPreviewLink = previewLink;
	}


	/**
	 * @return the mFlow
	 */
	public String getFlow() {
		return mFlow;
	}


	/**
	 * @param mFlow the mFlow to set
	 */
	public void setFlow(String flow) {
		this.mFlow = flow;
	}


	/**
	 * @return the mMarket
	 */
	public String getMarket() {
		return mMarket;
	}


	/**
	 * @param mMarket the mMarket to set
	 */
	public void setMarket(String market) {
		this.mMarket = market;
	}


	/**
	 * @return the mNeedDeviceInfo
	 */
	public String getNeedDeviceInfo() {
		return mNeedDeviceInfo;
	}


	/**
	 * @param mNeedDeviceInfo the mNeedDeviceInfo to set
	 */
	public void setNeedDeviceInfo(String needDeviceInfo) {
		this.mNeedDeviceInfo = needDeviceInfo;
	}


	/**
	 * @return the mCurrency
	 */
	public String getCurrency() {
		return mCurrency;
	}


	/**
	 * @param mCurrency the mCurrency to set
	 */
	public void setCurrency(String currency) {
		this.mCurrency = currency;
	}


	/**
	 * @return the mPlatforms
	 */
	public String getPlatforms() {
		return mPlatforms;
	}


	/**
	 * @param mPlatforms the mPlatforms to set
	 */
	public void setPlatforms(String platforms) {
		this.mPlatforms = platforms;
	}


	/**
	 * @return the mCountries
	 */
	public String getCountries() {
		return mCountries;
	}


	/**
	 * @param mCountries the mCountries to set
	 */
	public void setCountries(String countries) {
		this.mCountries = countries;
	}


	/**
	 * @return the mPayout
	 */
	public double getPayout() {
		return mPayout;
	}


	/**
	 * @param mPayout the mPayout to set
	 */
	public void setPayout(double payout) {
		this.mPayout = payout;
	}


	/**
	 * @return the mPayoutType
	 */
	public String getPayoutType() {
		return mPayoutType;
	}


	/**
	 * @param mPayoutType the mPayoutType to set
	 */
	public void setPayoutType(String payoutType) {
		this.mPayoutType = payoutType;
	}


	/**
	 * @return the mCap
	 */
	public int getCap() {
		return mCap;
	}


	/**
	 * @param mCap the mCap to set
	 */
	public void setCap(int cap) {
		this.mCap = cap;
	}


	/**
	 * @return the mAllowNetworkTypes
	 */
	public String getAllowNetworkTypes() {
		return mAllowNetworkTypes;
	}


	/**
	 * @param mAllowNetworkTypes the mAllowNetworkTypes to set
	 */
	public void setAllowNetworkTypes(String allowNetworkTypes) {
		this.mAllowNetworkTypes = allowNetworkTypes;
	}


	/**
	 * @return the mForbiddenNetworkTypes
	 */
	public String getForbiddenNetworkTypes() {
		return mForbiddenNetworkTypes;
	}


	/**
	 * @param mForbiddenNetworkTypes the mForbiddenNetworkTypes to set
	 */
	public void setForbiddenNetworkTypes(String forbiddenNetworkTypes) {
		this.mForbiddenNetworkTypes = forbiddenNetworkTypes;
	}


	/**
	 * @return the mSubAffBlacklist
	 */
	public String getSubAffBlacklist() {
		return mSubAffBlacklist;
	}


	/**
	 * @param mSubAffBlacklist the mSubAffBlacklist to set
	 */
	public void setSubAffBlacklist(String subAffBlacklist) {
		this.mSubAffBlacklist = subAffBlacklist;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(null == obj) {
			return false;
		}
		
		if(!(obj instanceof CPIOfferInfo)) {
			return false;
		}
		
		CPIOfferInfo other = (CPIOfferInfo) obj;
		
		if(mOfferId != other.mOfferId) {
			return false;
		}
		
		if(!StringUtils.equals(mOfferName, other.mOfferName)) {
			return false;
		}
		
		if(!StringUtils.equals(mCategories, other.mCategories)) {
			return false;
		}
		
		if(!StringUtils.equals(mDesc, other.mDesc)) {
			return false;
		}
		
		if(!StringUtils.equals(mVersion, other.mVersion)) {
			return false;
		}
		
		if(!StringUtils.equals(mIncent, other.mIncent)) {
			return false;
		}
		
		if(!StringUtils.equals(mTrackLink, other.mTrackLink)) {
			return false;
		}
		
		if(!StringUtils.equals(mPreviewLink, other.mPreviewLink)) {
			return false;
		}
		
		if(!StringUtils.equals(mFlow, other.mFlow)) {
			return false;
		}
		
		if(!StringUtils.equals(mMarket, other.mMarket)) {
			return false;
		}
		
		if(!StringUtils.equals(mNeedDeviceInfo, other.mNeedDeviceInfo)) {
			return false;
		}
		
		if(!StringUtils.equals(mCurrency, other.mCurrency)) {
			return false;
		}
		
		if(!StringUtils.equals(mPlatforms, other.mPlatforms)) {
			return false;
		}
		
		if(!StringUtils.equals(mCountries, other.mCountries)) {
			return false;
		}
		
		if(mPayout != other.mPayout) {
			return false;
		}
		
		if(!StringUtils.equals(mPayoutType, other.mPayoutType)) {
			return false;
		}
		
		if(mCap != other.mCap) {
			return false;
		}
		
		if(!StringUtils.equals(mAllowNetworkTypes, other.mAllowNetworkTypes)) {
			return false;
		}
		
		if(!StringUtils.equals(mForbiddenNetworkTypes, other.mForbiddenNetworkTypes)) {
			return false;
		}
		
		if(!StringUtils.equals(mSubAffBlacklist, other.mSubAffBlacklist)) {
			return false;
		}
		
		return true;
	}
}
