package com.spier.common.bean.db;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author GHB
 * @version 1.0
 * @date 2019.2.14
 */
public class CPICampaignInfo implements Serializable{
	private static final long serialVersionUID = -6848531264728859425L;
	private String mCampaign;
	private String mPkgName;
	private String mDesc;
	// 用逗号分割
	private String mIconIds;
	// 用逗号分割
	private String mMaterialIds;
	// 用逗号分割，这个是主键，一定要先排序再序列化
	private String mOfferIds;
	private Timestamp mCreateTime;
	private Timestamp mChangeTime;
	
	/**
	 * @return the mCampaign
	 */
	public String getCampaign() {
		return mCampaign;
	}
	/**
	 * @param mCampaign the mCampaign to set
	 */
	public void setCampaign(String campaign) {
		this.mCampaign = campaign;
	}
	/**
	 * @return the mPkgName
	 */
	public String getPkgName() {
		return mPkgName;
	}
	/**
	 * @param mPkgName the mPkgName to set
	 */
	public void setPkgName(String pkgName) {
		this.mPkgName = pkgName;
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
	 * @return the mIconIds
	 */
	public String getIconIds() {
		return mIconIds;
	}
	/**
	 * @param mIconIds the mIconIds to set
	 */
	public void setIconIds(String iconIds) {
		this.mIconIds = iconIds;
	}
	/**
	 * @return the mMaterialIds
	 */
	public String getMaterialIds() {
		return mMaterialIds;
	}
	/**
	 * @param mMaterialIds the mMaterialIds to set
	 */
	public void setMaterialIds(String materialIds) {
		this.mMaterialIds = materialIds;
	}
	/**
	 * @return the mOfferIds
	 */
	public String getOfferIds() {
		return mOfferIds;
	}
	/**
	 * @param mOfferIds the mOfferIds to set
	 */
	public void setOfferIds(String offerIds) {
		this.mOfferIds = offerIds;
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
		
		if(!(obj instanceof CPICampaignInfo)) {
			return false;
		}
		
		CPICampaignInfo other = (CPICampaignInfo) obj;
		
		if(!StringUtils.equals(mCampaign, other.mCampaign)) {
			return false;
		}
		
		if(!StringUtils.equals(mPkgName, other.mPkgName)) {
			return false;
		}
		
		if(!StringUtils.equals(mDesc, other.mDesc)) {
			return false;
		}
		
		if(!StringUtils.equals(mIconIds, other.mIconIds)) {
			return false;
		}
		
		if(!StringUtils.equals(mMaterialIds, other.mMaterialIds)) {
			return false;
		}
		
		if(!StringUtils.equals(mOfferIds, other.mOfferIds)) {
			return false;
		}
		
		return true;
	}
	
}
