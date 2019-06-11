package com.spier.controller;

import java.util.HashMap;
import java.util.Map;
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
import com.spier.common.bean.ReturnEntity;
import com.spier.common.bean.db.UserInfo;
import com.spier.common.bean.net.ConfigsEntity;
import com.spier.common.config.ErrorCodes;
import com.spier.common.utils.AnalyseResult;
import com.spier.service.IPkgCfgsInfoService;
import com.spier.service.IUserInfoService;
import com.spier.service.channel.IChanCfgsInfoService;
import com.spier.service.saferequest.ISafeRequestParserService;
import com.spier.service.saferequest.ISessionManageService;

/**
 * 配置信息Controller
 * @author GHB
 * @version 1.0
 * @date 2019.2.20
 */
@Controller
public class ConfigsOperations {

	@Autowired
	private ISafeRequestParserService mParserService;
	
	@Reference
	private IUserInfoService mUserInfoService;
	
	@Reference
	private ISessionManageService mSessionManagerService;
	
	@Reference
	private IChanCfgsInfoService mChanCfgsInfoService;
	
	@Reference
	private IPkgCfgsInfoService mPkgCfgsInfoService;
	
	@RequestMapping(value = "config/request", method = RequestMethod.POST)
	@ResponseBody
	public String onConfigsRequest(HttpServletRequest request) {
		AnalyseResult<ConfigsEntity.Request, ConfigsEntity.Response> requestData = 
				mParserService.deserializeRequestFirstLayer(
						request, ConfigsEntity.Request.class, true);
		// 如果失败，直接发送响应
		if(!requestData.mIsSucceed) {
			AnalyseResult<Object, ConfigsEntity.Response> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 未解析到有效数据
		if(null == requestData.mRequestObj) {
			AnalyseResult<Object, ConfigsEntity.Response> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 查询用户是否已经注册
		UserInfo user = mUserInfoService.findRecordByKWs(
				requestData.mRequestObj.getUID(), requestData.mRequestObj.getPkgName(), requestData.mChanId);
		if(null == user) {
			AnalyseResult<Object, ConfigsEntity.Response> response = 
					mParserService.generateResponseStr(ErrorCodes.M_ERR_CODE_USER_UNREGISTER, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 查询用户是否已经登陆
		ReturnEntity retRst = mSessionManagerService.checkSessionEstablished(
				requestData.mRequestObj.getUID(), requestData.mRequestObj.getPkgName(), 
				requestData.mChanId, requestData.mRequestObj.getSessionId());
		if(!retRst.mIsSucceed) {
			AnalyseResult<Object, ConfigsEntity.Response> response = 
					mParserService.generateResponseStr(
							retRst.mErrorCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 从渠道配置库中查询信息
		Map<String, Boolean> chanCfgs = getChanCfgs(requestData.mChanId);
		
		// 从包配置库中查询信息
		Map<String, Boolean> pkgCfgs = getPackageCfgs(requestData.mRequestObj.getPkgName());
		
		// 组装响应数据
		ConfigsEntity.Response responseData = new ConfigsEntity.Response();
		responseData.setChanCfgs(chanCfgs);
		responseData.setPkgCfgs(pkgCfgs);
		
		// 发送响应数据
		AnalyseResult<Object, ConfigsEntity.Response> response = 
				mParserService.generateResponseStr(
						ErrorCodes.M_ERR_CODE_SUCCEED, requestData.mChanId, responseData);
		return response.mResult;
	}
	
	
	private Map<String, Boolean> getChanCfgs(String chanNo) {
		Map<String, Boolean> res = new HashMap<String, Boolean>();
		
		if(StringUtils.isEmpty(chanNo)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "渠道号为空，无法查询渠道配置信息！");
			return res;
		}
		
		return mChanCfgsInfoService.getCfgsByChanNo(chanNo);
	}
	
	private Map<String, Boolean> getPackageCfgs(String pkgName) {
		Map<String, Boolean> res = new HashMap<String, Boolean>();
		
		if(StringUtils.isEmpty(pkgName)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "包名为空，无法查询包配置信息！");
			return res;
		}
		
		return mPkgCfgsInfoService.getCfgsByPackageName(pkgName);
	}
}
