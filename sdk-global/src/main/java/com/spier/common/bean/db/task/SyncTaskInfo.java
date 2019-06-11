package com.spier.common.bean.db.task;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.spier.common.bean.db.CPIOfferInfo;
import com.spier.common.config.GlobalConfig;

/**
 * 同步任务信息，区别于自建任务
 * @author GHB
 * @version 1.0
 * @date 2019.2.16
 */
public class SyncTaskInfo extends CPIOfferInfo implements Serializable{

	private static final long serialVersionUID = 7835981808082612564L;

	public SyncTaskInfo() {
		
	}
	
	public SyncTaskInfo(CPIOfferInfo other) {
		super(other);
	}
	
	// 主键是offerid
	private String mCampaign;
	private String mPkgName;
	private String mCampaignDesc;
	private String mIconsIds;
	private String mMaterialsIds;
	private int mFinishedCount;
	
	/**
	 * @see GlobalConfig#M_TASK_STATE_PAUSE
	 * @see GlobalConfig#M_TASK_STATE_RUNNING
	 */
	private int mSwitch;

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
	public String getCampaignDesc() {
		return mCampaignDesc;
	}
	
	/**
	 * @param mDesc the mDesc to set
	 */
	public void setCampaignDesc(String desc) {
		this.mCampaignDesc = desc;
	}
	
	/**
	 * @return the mIconsIds
	 */
	public String getIconsIds() {
		return mIconsIds;
	}
	/**
	 * @param mIconsIds the mIconsIds to set
	 */
	public void setIconsIds(String iconsIds) {
		this.mIconsIds = iconsIds;
	}
	
	/**
	 * @return the mMaterialsIds
	 */
	public String getMaterialsIds() {
		return mMaterialsIds;
	}
	
	/**
	 * @param mMaterialsIds the mMaterialsIds to set
	 */
	public void setMaterialsIds(String materialsIds) {
		this.mMaterialsIds = materialsIds;
	}
	
	public int getSwitch() {
		return mSwitch;
	}
	
	public void setSwitch(int s) {
		mSwitch = s;
	}
	
	public int getFinishedCount() {
		return mFinishedCount;
	}
	
	public void setFinishedCount(int c) {
		mFinishedCount = c;
	}

	/* (non-Javadoc)
	 * @see net.billing.sdk.beans.db.CPIOfferInfo#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(!super.equals(obj)) {
			return false;
		}
		
		if(!(obj instanceof SyncTaskInfo)) {
			return false;
		}
		
		SyncTaskInfo other = (SyncTaskInfo) obj;
		
		if(!StringUtils.equals(mCampaign, other.mCampaign)) {
			return false;
		}
		
		if(!StringUtils.equals(mPkgName, other.mPkgName)) {
			return false;
		}
		
		if(!StringUtils.equals(mDesc, other.mDesc)) {
			return false;
		}
		
		if(!StringUtils.equals(mIconsIds, other.mIconsIds)) {
			return false;
		}
		
		if(!StringUtils.equals(mMaterialsIds, other.mMaterialsIds)) {
			return false;
		}
		
		if(mSwitch != other.mSwitch) {
			return false;
		}
		
		if(mFinishedCount != other.mFinishedCount) {
			return false;
		}
		
		return true;
	}
}
