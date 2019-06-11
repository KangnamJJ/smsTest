package com.spier.common.bean.net;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.spier.common.utils.CompareUtil;

/**
 * 与上游对接的CPI Offer数据类
 * @author GHB
 * @version 1.0
 * @Date 2019.2.23
 */
public class CPIOfferBeans implements Serializable{

	private static final long serialVersionUID = 2544515703676279849L;

	/**
	 * 请求数据
	 * @author GHB
	 * @version 1.0
	 * @date 2019.2.13
	 */
	public static class Request {
		/**
		 * token，必填
		 */
		public String mToken;
		/**
		 * 子渠道Id，必填
		 */
		public int mAffiliateId;
		/**
		 * 选填
		 */
		public String mOfferId;
		/**
		 * 选填
		 */
		public List<String> mCountires;
		/**
		 * 选填
		 */
		public List<String> mPlatforms;
		/**
		 * 选填
		 */
		public String mNeedPkg;
		
		/**
		 * 请求数据是否有效
		 * @return
		 */
		public boolean isDataValid() {
			if(StringUtils.isEmpty(mToken)) {
				return false;
			}
			
			if(mAffiliateId <= 0) {
				return false;
			}
			
			return true;
		}
	}
	
	/**
	 * 响应数据
	 * @author GHB
	 * @version 1.0
	 * @date 2019.2.23
	 */
	public static class Response {
		public static Response getDefault() {
			return new Response();
		}
		
		private static final Gson mGson = new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.disableHtmlEscaping()
				.create();
		
		/**
		 * 反序列化得到对象
		 * @param json
		 * @return 可能为null
		 */
		public static Response fromJsonStr(String json) {
			if(StringUtils.isEmpty(json)) {
				Logger.getAnonymousLogger().log(Level.SEVERE, "json为null，无法反序列化成CPIOffer响应对象！");
				return null;
			}
			
			Response res = null;
			try {
				res = mGson.fromJson(json, Response.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return res;
		}
		
		/** 0表示成功，其他表示失败 */
		@Expose
		@SerializedName("status")
		public int mStatus;
		
		/** 返回的offer数量 */
		@Expose
		@SerializedName("count")
		public int mCount;
		
		/** 返回的所有apk的列表 */
		@Expose
		@SerializedName("campaigns")
		public List<Campaign> mCampaigns;

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if(null == obj) {
				return false;
			}
			
			if(!(obj instanceof Response)) {
				return false;
			}
			
			Response other = (Response) obj;
			
			if(mStatus != other.mStatus) {
				return false;
			}
			
			if(mCount != other.mCount) {
				return false;
			}
			
			if(!CompareUtil.areListsEqual(mCampaigns, other.mCampaigns, true)) {
				return false;
			}
			
			return true;
		}
		
		
	}
	
	/**
	 * Offer
	 * @author GHB
	 * @version 1.0
	 * @date 2019.2.13
	 */
	public static class Campaign {
		
		/**
		 * 构造方法
		 * @param other
		 */
		public Campaign(Campaign other) {
			if(null != other) {
				mApplicaionName = other.mApplicaionName;
				mPkgName = other.mPkgName;
				mDesc = other.mDesc;
				mIcons = other.mIcons;
				mMaterials = other.mMaterials;
				mOffers = other.mOffers;
			}
		}
		
		/**
		 * 应用名称
		 */
		@Expose
		@SerializedName("campaign")
		public String mApplicaionName;
		
		/**
		 * 包名
		 */
		@Expose
		@SerializedName("pkg")
		public String mPkgName;
		
		/**
		 * 应用描述
		 */
		@Expose
		@SerializedName("description")
		public String mDesc;
		
		/**
		 * apk推广icon列表
		 */
		@Expose
		@SerializedName("icons")
		public List<ImgMaterial> mIcons;
		
		/**
		 * Apk推广素材列表
		 */
		@Expose
		@SerializedName("materials")
		public List<ImgMaterial> mMaterials;
		
		/**
		 * Offer列表
		 */
		@Expose
		@SerializedName("offers")
		public List<OfferInfo> mOffers;

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if(null == obj) {
				return false;
			}
			
			if(!(obj instanceof Campaign)) {
				return false;
			}
			
			Campaign other = (Campaign) obj;
			
			if(!StringUtils.equals(mApplicaionName, other.mApplicaionName)) {
				return false;
			}
			
			if(!StringUtils.equals(mPkgName, other.mPkgName)) {
				return false;
			}
			
			if(!StringUtils.equals(mDesc, other.mDesc)) {
				return false;
			}
			
			if(!CompareUtil.areListsEqual(mIcons, other.mIcons, true)) {
				return false;
			}
			
			if(!CompareUtil.areListsEqual(mMaterials, other.mMaterials, true)) {
				return false;
			}
			
			if(!CompareUtil.areListsEqual(mOffers, other.mOffers, true)) {
				return false;
			}
			
			return true;
		}
	}
	
	public static class ImgMaterial {
		@Expose
		@SerializedName("pixel")
		public String mPixel;
		
		@Expose
		@SerializedName("url")
		public String mURL;

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if(null == obj) {
				return false;
			}
			
			if(!(obj instanceof ImgMaterial)) {
				return false;
			}
			
			ImgMaterial other = (ImgMaterial) obj;
			
			if(!StringUtils.equals(mURL, other.mURL)) {
				return false;
			}
			
			if(!StringUtils.equals(mPixel, other.mPixel)) {
				return false;
			}
			
			return true;
		}
		
		
	}
	
	public static class OfferInfo {
		/**
		 * Offer的唯一标识id
		 */
		@Expose
		@SerializedName("id")
		public int mOfferId;
		
		/**
		 * Offer的名字
		 */
		@Expose
		@SerializedName("name")
		public String mOfferName;
		
		/**
		 * Offer的分类
		 */
		@Expose
		@SerializedName("category")
		public List<String> mCategories;
		
		/**
		 * Offer的描述
		 */
		@Expose
		@SerializedName("description")
		public String mDesc;
		
		/**
		 * apk版本要求
		 */
		@Expose
		@SerializedName("version")
		public String mVersion;
		
		/**
		 * 是否激励，0——激励，1-非激励
		 */
		@Expose
		@SerializedName("incent")
		public String mIncent;
		
		/**
		 * 点击跳转链接
		 */
		@Expose
		@SerializedName("trackLink")
		public String mTrackLink;
		
		/**
		 * 预览链接
		 */
		@Expose
		@SerializedName("previewLink")
		public String mPreviewLink;
		
		/**
		 * Offer转化流程描述
		 */
		@Expose
		@SerializedName("flow")
		public String mFlow;
		
		/**
		 * 推广市场
		 */
		@Expose
		@SerializedName("marketSource")
		public String mMarket;
		
		/**
		 * 是否需要设备信息
		 */
		@Expose
		@SerializedName("deviceNeeds")
		public String mNeedDeviceInfo;
		
		/**
		 * 货币，一般是USD
		 */
		@Expose
		@SerializedName("currency")
		public String mCurrency;
		
		/**
		 * 操作系统列表,IOS或ANDROID
		 */
		@Expose
		@SerializedName("platforms")
		public List<String> mPlatforms;
		
		/**
		 * 支持的国家列表
		 */
		@Expose
		@SerializedName("countries")
		public List<String> mCountries;
		
		/**
		 * 价格
		 */
		@Expose
		@SerializedName("payout")
		public double mPayout;
		
		/**
		 * CPI/CPA/CPM/CPC/CPE/CPS/SmartLink
		 */
		@Expose
		@SerializedName("payoutType")
		public String mPayoutType;
		
		/**
		 * 每日限制的转化数
		 */
		@Expose
		@SerializedName("cap")
		public int mCap;
		
		/**
		 * 允许的网络类型
		 */
		@Expose
		@SerializedName("allowNetwork")
		public List<String> mAllowNetworkTypes;
		
		/**
		 * 拒绝的网络类型
		 */
		@Expose
		@SerializedName("forbiddenNetwork")
		public List<String> mForbiddenNetworkTypes;
		
		/**
		 * 子渠道黑名单列表
		 */
		@Expose
		@SerializedName("blackAffSubList")
		public List<String> mSubAffBlacklist;

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if(null == obj) {
				return false;
			}
			
			if(!(obj instanceof OfferInfo)) {
				return false;
			}
			
			OfferInfo other = (OfferInfo) obj;
			
			if(mOfferId != other.mOfferId) {
				return false;
			}
			
			if(!StringUtils.equals(mOfferName, other.mOfferName)) {
				return false;
			}
			
			if(!CompareUtil.areListsEqual(mCategories, other.mCategories, true)) {
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
			
			if(!CompareUtil.areListsEqual(mPlatforms, other.mPlatforms, true)) {
				return false;
			}
			
			if(!CompareUtil.areListsEqual(mCountries, other.mCountries, true)) {
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
			
			if(!CompareUtil.areListsEqual(mAllowNetworkTypes, other.mAllowNetworkTypes, true)) {
				return false;
			}
			
			if(!CompareUtil.areListsEqual(mForbiddenNetworkTypes, other.mForbiddenNetworkTypes, true)) {
				return false;
			}
			
			if(!CompareUtil.areListsEqual(mSubAffBlacklist, other.mSubAffBlacklist, true)) {
				return false;
			}
			
			return true;
		}
		
		
	}
}
