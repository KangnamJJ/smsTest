package com.spier.common.http;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.spier.common.bean.net.CPIOfferBeans;
import com.spier.common.http.XZAnnotations.KVSerialize;

/**
 * CPI Offer请求。向上游请求CPI Offer
 * @author GHB
 * @version 1.0
 * @date 2019.2.13
 */
public class HttpCPIOfferRequest extends KVSerializerHTTP implements Serializable{

	private static final long serialVersionUID = 836711422682460265L;

	private static final String M_URL = "http://a.cpi.spipermedia.com/cpi/list";
	
	/**
	 * 构造方法
	 * @param url
	 */
	public HttpCPIOfferRequest(CPIOfferBeans.Request requestData) {
		super(M_URL, "CPIOfferRequest", true);
		
		initRequestParams(requestData);
	}

	@KVSerialize("token")
	private String mToken;
	
	@KVSerialize("aid")
	private int mAffiliateId;
	
	@KVSerialize("id")
	private String mOfferId;
	
	// HK,CN,US,TH
	@KVSerialize("countries")
	private String mCountries;
	
	// IOS/ANDROID
	@KVSerialize("platforms")
	private String mPlatforms;
	
	@KVSerialize("pkg")
	private String mPKG;
	
	private void initRequestParams(CPIOfferBeans.Request requestData) {
		if(null == requestData) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "requestData为null，初始化失败！");
			return;
		}
		
		if(!requestData.isDataValid()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "requestData数据无效，初始化失败！");
			return;
		}
		
		// 补全初始化的代码
		mToken = requestData.mToken;
		mAffiliateId = requestData.mAffiliateId;
		mOfferId = requestData.mOfferId;
		mCountries = toContries(requestData.mCountires);
		mPlatforms = toPlatforms(requestData.mPlatforms);
		mPKG = requestData.mNeedPkg;
	}
	
	private String toContries(List<String> countriesList) {
		return combineFromList(countriesList, ",");
	}
	
	private String toPlatforms(List<String> platformsList) {
		return combineFromList(platformsList, "/");
	}
	
	private String combineFromList(List<String> list, String spliter) {
		if(null == list || list.isEmpty()) {
			return null;
		}
		
		String res = "";
		for(String unit : list) {
			if(StringUtils.isEmpty(unit)) {
				continue;
			}
			
			if(!StringUtils.isEmpty(res)) {
				res += spliter;
			}
			
			res += unit;
		}
		
		return res;
	}
}
