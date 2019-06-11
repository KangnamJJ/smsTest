package com.spier.referrerdispatchers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.spier.common.bean.db.referrer.ReferrerInfo;
import com.spier.common.http.Http;
import com.spier.common.http.HttpCtx;
import com.spier.common.http.HttpMode;

/**
 * 转化分发器抽象基类
 * @author GHB
 * @version 1.0
 * @date 2019.3.15
 */
public abstract class AbsConversionDispatcher implements IConversionDispatcher {

	private String mChan;
	
	@Override
	public String getChan() {
		return mChan;
	}

	@Override
	public void setChan(String chanNo) {
		mChan = chanNo;
	}

	private String mPostback;
	
	@Override
	public void setPostback(String postback) {
		mPostback = postback;
	}

	@Override
	public String getPostback() {
		return mPostback;
	}
	
	private Map<String, String> mReplacement = new HashMap<String, String>();
	
	/* (non-Javadoc)
	 * @see net.billing.sdk.tasks.referrerdispatchers.IConversionDispatcher#getReplacement()
	 */
	@Override
	public Map<String, String> getReplacement() {
		return mReplacement;
	}
	
	/* (non-Javadoc)
	 * @see net.billing.sdk.tasks.referrerdispatchers.IConversionDispatcher#addReplacement(java.lang.String, java.lang.String)
	 */
	@Override
	public void addReplacement(String replacePart, String replacement) {
		if(StringUtils.isEmpty(replacePart)) {
			return;
		}
		
		mReplacement.put(replacePart, replacement);
	}

	/* (non-Javadoc)
	 * @see net.billing.sdk.tasks.referrerdispatchers.IConversionDispatcher#dispatch(net.billing.sdk.beans.db.ReferrerInfo)
	 */
	@Override
	public boolean dispatch(ReferrerInfo conv) {
		if(null == conv) {
			return false;
		}
		
		ReferrerInfo ref = new ReferrerInfo();
		conv.clone(ref);
		
		if(StringUtils.containsIgnoreCase(ref.getReferrer(), "%3D")) {
			try {
				ref.setReferrer(URLDecoder.decode(ref.getReferrer(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		if(!beforeDispatching(ref)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"【{0}】：before处理失败，不再继续分发！", getChan()));
			return false;
		}
		
		// 封装回调url
		String requestUrl = sealPostback();
		if(StringUtils.isEmpty(requestUrl)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"【{0}】：封装postback出错，无法下发转化！", getChan()));
			return false;
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("将要下发转化的URL：【{0}】", requestUrl));
		
		// 发送请求
		HttpCtx ctx = HttpCtx.getDefault();
		ctx.setHttpMode(HttpMode.Get);
		Http http = new Http(requestUrl);
		http.access(ctx);
		if(!http.isSucceed()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"【{0}】：调用接口下发转化失败：【{1}】", "DispatcherHexMobile", http.getErrorStr()));
			return false;
		}
		
		return true;
	}
	
	protected String absortConvIdFromReferrer(String referrer) {
		if(StringUtils.isEmpty(referrer)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "referrer为空，无法提取转化数据！");
			return null;
		}
		
		String chanNo = null;
		String clickId = null;
		
		String[] ss = referrer.split("&");
		for(String s : ss) {
			if(StringUtils.isEmpty(s)) {
				continue;
			}
			
			if(s.contains(ReferrerConfig.M_PARAM_NAME_CHAN)) {
				chanNo = absorbValue(s);
			}
			
			if(s.contains(ReferrerConfig.M_PARAM_NAME_CONVERSION_ID)) {
				clickId = absorbValue(s);
			}
		}
		
		if(!StringUtils.equals(chanNo, getChan())) {
			Logger.getAnonymousLogger().log(Level.WARNING, MessageFormat.format(
					"转化数据中渠道号不符！应该是【{0}】，实际是【{1}】", 
					getChan(), chanNo));
		}
		
		return clickId;
	}
	
	private String absorbValue(String expr) {
		if(StringUtils.isEmpty(expr)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "expr为空，无法提取参数值！");
			return null;
		}
		
		String[] ss = expr.split("=");
		if(ss.length < 2) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"expr【{0}】用=切割，格式有误，无法提取参数值！", expr));
			return null;
		}
		
		if(ss.length > 2) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"expr【{0}】用=切割，格式有误！", expr));
		}
		
		return ss[1];
	}
	
	private String sealPostback() {
		if(StringUtils.isEmpty(getPostback())) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "postback为空，无法封装postback！");
			return null;
		}
		
		if(getReplacement().isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, "无replace数据，停止封装postback！");
			return null;
		}
		
		String res = getPostback();
		
		for(Entry<String, String> entry : getReplacement().entrySet()) {
			String replacePart = entry.getKey();
			String replacement = entry.getValue();
			
			if(StringUtils.isEmpty(replacePart)) {
				Logger.getAnonymousLogger().log(Level.INFO, "replacePart为空，忽略！");
				continue;
			}
			
			res = res.replace(replacePart, replacement);
		}
		
		return res;
	}
}
