package com.spier.common.bean.db;

import java.io.Serializable;

/**
 * 国家信息
 * @author GHB
 * @version 1.0
 * @date 2019.1.10
 */
public class CountryInfo implements Serializable{
	private static final long serialVersionUID = 653486041952507700L;
	
	private int mIndex;
	private String mAbbrevation;
	private String mChiness;
	private String mEnglish;
	
	/**
	 * @return the mIndex
	 */
	public int getIndex() {
		return mIndex;
	}
	
	/**
	 * @param mIndex the mIndex to set
	 */
	public void setIndex(int ind) {
		this.mIndex = ind;
	}
	
	/**
	 * @return the mAbbrevation
	 */
	public String getAbbrevation() {
		return mAbbrevation;
	}
	
	/**
	 * @param mAbbrevation the mAbbrevation to set
	 */
	public void setAbbrevation(String abbrevation) {
		this.mAbbrevation = abbrevation;
	}
	
	/**
	 * @return the mChiness
	 */
	public String getChiness() {
		return mChiness;
	}
	
	/**
	 * @param mChiness the mChiness to set
	 */
	public void setChiness(String chiness) {
		this.mChiness = chiness;
	}
	
	/**
	 * @return the mEnglish
	 */
	public String getEnglish() {
		return mEnglish;
	}
	
	/**
	 * @param mEnglish the mEnglish to set
	 */
	public void setEnglish(String english) {
		this.mEnglish = english;
	}
	
	
}
