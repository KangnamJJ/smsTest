package com.spier.controller.referrer;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.common.bean.db.referrer.ReferrerBeans;
import com.spier.common.config.ErrorCodes;
import com.spier.common.utils.AnalyseResult;
import com.spier.service.referrer.IReferrerService;
import com.spier.service.saferequest.ISafeRequestParserService;

/**
 * 广告推广数据相关操作
 * @author GHB
 * @version 1.0
 * @date 2019.3.7
 */
@Controller
public class ReferrerOperations {

	@Autowired
	private ISafeRequestParserService mParserService;
	
	@Reference
	private IReferrerService mReferrerService;
	
	@RequestMapping(value = "user/referrer", method = {RequestMethod.POST})
	@ResponseBody
	public String onReferrerReport(HttpServletRequest request) {
		AnalyseResult<ReferrerBeans.ReferrerRequest, Object> requestData = 
				mParserService.deserializeRequestFirstLayer(request, ReferrerBeans.ReferrerRequest.class, true);
		
		Logger.getAnonymousLogger().log(Level.INFO, "上报渠道信息！");
		
		// 如果失败，直接发送响应
		if(!requestData.mIsSucceed) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "解密失败！");
			AnalyseResult<Object, Object> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 未解析到有效数据
		if(null == requestData.mRequestObj) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "无渠道数据");
			AnalyseResult<Object, Object> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 应用名称不合法
		if(StringUtils.isEmpty(requestData.mRequestObj.getAppName())) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "应用名称为空！");
			
			AnalyseResult<Object, Object> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_APP_NAME_INVALID, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 用户名为空
		if(StringUtils.isEmpty(requestData.mRequestObj.mUID)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "uid为空！");
			AnalyseResult<Object, Object> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_USER_UNREGISTER, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 推广数据为空
		if(StringUtils.isEmpty(requestData.mRequestObj.mReferrer)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "推广数据为空！");
			AnalyseResult<Object, Object> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_NO_REFERRER, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 记录referrer
		mReferrerService.addReferrer(request.getRemoteAddr(), requestData.mChanId, requestData.mRequestObj);
		
		AnalyseResult<Object, Object> response = 
				mParserService.generateResponseStr(
						ErrorCodes.M_ERR_CODE_SUCCEED, requestData.mChanId, null);
		
		Logger.getAnonymousLogger().log(Level.INFO, "渠道信息记录完成！");
		
		return response.mResult;
	}
}
