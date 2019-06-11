package com.spier.common.bean.db;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;

/**
 * CPI素材信息
 * @author GHB
 * @version 1.0
 * @date 2019.2.14
 */
public class CPIMaterialInfo implements Serializable{
	private static final long serialVersionUID = -1890201679777196282L;
	private int mIndex;
	private String mPixel;
	private String mUrl;
	private Timestamp mCreateTime;
	private Timestamp mChangeTime;
	
	/**
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
	 * @return the mPixel
	 */
	public String getPixel() {
		return mPixel;
	}
	/**
	 * @param mPixel the mPixel to set
	 */
	public void setPixel(String pixel) {
		this.mPixel = pixel;
	}
	/**
	 * @return the mUrl
	 */
	public String getUrl() {
		return mUrl;
	}
	/**
	 * @param mUrl the mUrl to set
	 */
	public void setUrl(String url) {
		this.mUrl = url;
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
		
		if(!(obj instanceof CPIMaterialInfo)) {
			return false;
		}
		
		CPIMaterialInfo other = (CPIMaterialInfo) obj;
		
		if(mIndex != other.mIndex) {
			return false;
		}
		
		if(!StringUtils.equals(mUrl, other.mUrl)) {
			return false;
		}
		
		if(!StringUtils.equals(mPixel, other.mPixel)) {
			return false;
		}
		
		return true;
	}
	
	
}
