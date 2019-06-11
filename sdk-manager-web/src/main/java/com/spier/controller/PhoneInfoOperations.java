package com.spier.controller;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
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
import com.spier.common.bean.db.SimInfo;
import com.spier.common.bean.db.UserInfo;
import com.spier.common.bean.net.PhoneNumberCommitBeans;
import com.spier.common.bean.net.PhoneNumberRequestBeans;
import com.spier.common.config.ErrorCodes;
import com.spier.common.utils.AnalyseResult;
import com.spier.service.BussinessCommonService;
import com.spier.service.ISimInfoService;
import com.spier.service.IUserInfoService;
import com.spier.service.saferequest.ISafeRequestParserService;
import com.spier.service.saferequest.ISessionManageService;

/**
 * 手机信息、手机卡信息等请求Controller
 * @author GHB
 * @version 1.0
 * @date 2019.1.17
 */
@Controller
public class PhoneInfoOperations {

	@Autowired
	private ISafeRequestParserService mParserService;
	
	@Reference	
	private IUserInfoService mUserInfoService;
	
	@Reference	
	private ISessionManageService mSessionManageService;
	
	@Reference	
	private ISimInfoService mSimInfoService;
	
	@Reference	
	private BussinessCommonService bussinessCommonService;
	
	@RequestMapping(value="phone/requestnum", method={RequestMethod.POST})
	@ResponseBody
	public String onPhoneNumberRequest(HttpServletRequest request) {
		AnalyseResult<PhoneNumberRequestBeans.Request, PhoneNumberRequestBeans.Response> requestData = 
				mParserService.deserializeRequestFirstLayer(
						request, PhoneNumberRequestBeans.Request.class, true);
		// 如果失败，直接发送响应
		if(!requestData.mIsSucceed) {
			AnalyseResult<Object, PhoneNumberRequestBeans.Response> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 未解析到有效数据
		if(null == requestData.mRequestObj) {
			AnalyseResult<Object, PhoneNumberRequestBeans.Response> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 查询用户是否已经注册
		UserInfo user = mUserInfoService.findRecordByKWs(
				requestData.mRequestObj.getUserId(), requestData.mRequestObj.getAppName(), requestData.mChanId);
		if(null == user) {
			AnalyseResult<Object, PhoneNumberRequestBeans.Response> response = 
					mParserService.generateResponseStr(ErrorCodes.M_ERR_CODE_USER_UNREGISTER, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 查询用户是否已经登陆
		ReturnEntity retRst = mSessionManageService.checkSessionEstablished(
				requestData.mRequestObj.getUserId(), requestData.mRequestObj.getAppName(), 
				requestData.mChanId, requestData.mRequestObj.getSessionId());
		if(!retRst.mIsSucceed) {
			AnalyseResult<Object, PhoneNumberRequestBeans.Response> response = 
					mParserService.generateResponseStr(
							retRst.mErrorCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 查询此用户id下的所有用户记录
		List<UserInfo> users = findUserWithPhoneNumber(user.getName());
		PhoneNumberRequestBeans.Response responseLst = packResponseList(users);
		
		// 下发数据
		AnalyseResult<Object, PhoneNumberRequestBeans.Response> response = 
				mParserService.generateResponseStr(
						ErrorCodes.M_ERR_CODE_SUCCEED, requestData.mChanId, responseLst);
		return response.mResult;
	}
	
	private List<UserInfo> findUserWithPhoneNumber(String userId) {
		List<UserInfo> res = new ArrayList<UserInfo>();
		
		if(StringUtils.isEmpty(userId)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "userid为空，无合适的用户信息！");
			return res;
		}
		
		List<UserInfo> list = mUserInfoService.findRecordsByUserName(userId);
		for(UserInfo info : list) {
			if(null == info) {
				continue;
			}
			
			if(StringUtils.isEmpty(info.getPhoneNumber())) {
				continue;
			}
			
			res.add(info);
		}
		
		return res;
	}
	
	private PhoneNumberRequestBeans.Response packResponseList(List<UserInfo> users) {
		PhoneNumberRequestBeans.Response res = new PhoneNumberRequestBeans.Response();
		if(null == users) {
			return res;
		}
		
		for(UserInfo user : users) {
			if(null == user) {
				continue;
			}
			
			PhoneNumberRequestBeans.SimInfo sim = new PhoneNumberRequestBeans.SimInfo();
			sim.setPhoneNumber(user.getPhoneNumber());
			
			SimInfo record = mSimInfoService.findSimInfoRecordByNumber(user.getPhoneNumber());
			if(null != record) {
				sim.setCountry(record.getCountry());
				sim.setOperator(record.getOperator());
			}
			
			res.addSimInfo(sim);
		}
		
		return res;
	}
	
	@RequestMapping(value="phone/commitnum", method={RequestMethod.POST})
	@ResponseBody
	public String onPhoneNumberCommit(HttpServletRequest request) {
		AnalyseResult<PhoneNumberCommitBeans.Request, PhoneNumberCommitBeans.Response> requestData = 
				mParserService.deserializeRequestFirstLayer(
						request, PhoneNumberCommitBeans.Request.class, true);
		// 如果失败，直接发送响应
		if(!requestData.mIsSucceed) {
			AnalyseResult<Object, PhoneNumberCommitBeans.Response> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 未解析到有效数据
		if(null == requestData.mRequestObj) {
			AnalyseResult<Object, PhoneNumberCommitBeans.Response> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 查询用户是否已经注册
		UserInfo user = mUserInfoService.findRecordByKWs(
				requestData.mRequestObj.getUID(), requestData.mRequestObj.getAppName(), requestData.mChanId);
		if(null == user) {
			AnalyseResult<Object, PhoneNumberCommitBeans.Response> response = 
					mParserService.generateResponseStr(ErrorCodes.M_ERR_CODE_USER_UNREGISTER, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 查询用户是否已经登陆
		ReturnEntity retRst = mSessionManageService.checkSessionEstablished(
				requestData.mRequestObj.getUID(), requestData.mRequestObj.getAppName(), 
				requestData.mChanId, requestData.mRequestObj.getSessionId());
		if(!retRst.mIsSucceed) {
			AnalyseResult<Object, PhoneNumberCommitBeans.Response> response = 
					mParserService.generateResponseStr(
							retRst.mErrorCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		if(StringUtils.isEmpty(requestData.mRequestObj.getNumber())) {
			AnalyseResult<Object, PhoneNumberCommitBeans.Response> response = 
					mParserService.generateResponseStr(
							retRst.mErrorCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 统一国家简称和运营商信息
		String countryAbb = bussinessCommonService.getCountryAbb(requestData.mRequestObj.getCountry());
		if(!StringUtils.equals(countryAbb, requestData.mRequestObj.getCountry())) {
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
					"国家简称【{0}】-->【{1}】", requestData.mRequestObj.getCountry(), countryAbb));
			requestData.mRequestObj.setCountry(countryAbb);
			
		}
		String opName = bussinessCommonService.getOperatorNameByCountryAbbAndOpText(
				countryAbb, requestData.mRequestObj.getOp());
		if(!StringUtils.equals(opName, requestData.mRequestObj.getOp())) {
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
					"运营商名称【{0}】-->【{1}】", requestData.mRequestObj.getOp(), opName));
			requestData.mRequestObj.setOp(opName);
		}
		
		// 更新用户数据中的手机号
		user.setPhoneNumber(requestData.mRequestObj.getNumber());
		if(!mUserInfoService.updateUserPhoneNumberByUID(user.getName(), user.getPhoneNumber())) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"用户【{0}】的手机号【{1}】更新失败！", user.getName(), user.getPhoneNumber()));
		}
		
		// 更新sim信息中的手机号
		boolean res = false;
		SimInfo exists = mSimInfoService.findSimInfoRecordByNumber(user.getPhoneNumber());
		if(exists == null) {
			SimInfo info = new SimInfo();
			info.setCountry(requestData.mRequestObj.getCountry());
			info.setNumber(user.getPhoneNumber());
			info.setOperator(requestData.mRequestObj.getOp());
			res = mSimInfoService.addSimInfoRecord(info);
		} else {
			if(!StringUtils.isEmpty(requestData.mRequestObj.getCountry())) {
				exists.setCountry(requestData.mRequestObj.getCountry());
			}
			if(!StringUtils.isEmpty(requestData.mRequestObj.getOp())) {
				exists.setOperator(requestData.mRequestObj.getOp());
			}
			res = mSimInfoService.updateInfoByInd(exists.getId(), exists);
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("sim信息更新结果：{0}", res ? "成功" : "失败"));
		
		// 下发结果
		AnalyseResult<Object, PhoneNumberCommitBeans.Response> response = 
				mParserService.generateResponseStr(
						ErrorCodes.M_ERR_CODE_SUCCEED, requestData.mChanId, null);
		return response.mResult;
	}
}
