package com.spier.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.common.bean.ReturnEntity;
import com.spier.common.bean.db.PhoneInfo;
import com.spier.common.bean.db.UserInfo;
import com.spier.common.bean.net.HeartbeatBeans;
import com.spier.common.bean.net.RequestPhoneInfo;
import com.spier.common.bean.net.user.PhoneReportBeans;
import com.spier.common.bean.net.user.UserLoginBeans;
import com.spier.common.bean.net.user.UserPrivilegesReportBeans;
import com.spier.common.bean.net.user.UserRegisterBeans;
import com.spier.common.config.ErrorCodes;
import com.spier.common.utils.AnalyseResult;
import com.spier.common.utils.DateTimeUtil;
import com.spier.common.utils.GeoUtils;
import com.spier.common.utils.IpUtils;
import com.spier.common.utils.RandomUtil;
import com.spier.service.IPhoneInfoService;
import com.spier.service.IUserInfoService;
import com.spier.service.saferequest.ISafeRequestParserService;
import com.spier.service.saferequest.ISessionManageService;

/**
 * 客户端注册或登陆请求接口，此接口只需要安全通信，无需用户身份验证
 * @author GHB
 * @version 1.0
 * @date 2018.12.31
 */
@Controller
public class UserInfoOperations {

	@Reference
	private IUserInfoService mUserInfoService;
	
	@Reference
	private IPhoneInfoService mPhoneInfoService;
	
	@Autowired
	private ISafeRequestParserService mParserService;
	
	@Reference
	private ISessionManageService mSessionManagerService;
	
	@RequestMapping(value="user/register", method={RequestMethod.POST})
	@ResponseBody
	public String onRegisterRequest(HttpServletRequest request) {
		AnalyseResult<UserRegisterBeans.RequestBean, UserRegisterBeans.ResponseBean> requestData = 
				mParserService.deserializeRequestFirstLayer(
						request, UserRegisterBeans.RequestBean.class, true);
		
		// 如果失败，直接发送响应
		if(!requestData.mIsSucceed) {
			AnalyseResult<Object, UserRegisterBeans.ResponseBean> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 未解析到有效数据
		if(null == requestData.mRequestObj) {
			AnalyseResult<Object, UserRegisterBeans.ResponseBean> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 应用名称不合法
		if(StringUtils.isEmpty(requestData.mRequestObj.getAppName())) {
			AnalyseResult<Object, UserRegisterBeans.ResponseBean> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_APP_NAME_INVALID, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 没有手机信息
		if(null == requestData.mRequestObj.getPhoneInfo()) {
			AnalyseResult<Object, UserRegisterBeans.ResponseBean> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_REG_NO_PHONE_INFO, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 检查手机信息的合法性
		if(!requestData.mRequestObj.getPhoneInfo().isInfoValid()) {
			AnalyseResult<Object, UserRegisterBeans.ResponseBean> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_REG_PHONE_INFO_INV, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 组织手机信息数据单元
		PhoneInfo dbPhoneInfo = getDbPhoneInfoFromRequest(requestData.mRequestObj.getPhoneInfo());
		if(null == dbPhoneInfo) {
			AnalyseResult<Object, UserRegisterBeans.ResponseBean> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_REG_DB_PH_INFO_FAIL, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 从手机信息表中查询此用户是否已经存在
		boolean exists = false;
		// 这个方法有问题，如果mac地址为02:00:00:00:00:00或者前几个为空或其他默认无效值，则有可能取出多个。
//		List<PhoneInfo> existList = mPhoneInfoService.findRecordsByKWs(
//				dbPhoneInfo.getIMEI(), dbPhoneInfo.getMac(), dbPhoneInfo.getSerialNo(), dbPhoneInfo.getAndroidId());
		List<PhoneInfo> existList = mPhoneInfoService.findRecordsByAndroidId(dbPhoneInfo.getAndroidId());
		if(!existList.isEmpty()) {
			exists = true;
			if(existList.size() > 1) {
				// 手机信息存在于多条记录中，可能为刷量的
				AnalyseResult<Object, UserRegisterBeans.ResponseBean> response = 
						mParserService.generateResponseStr(
								ErrorCodes.M_ERR_CODE_REG_DB_PH_INFO_DUP, requestData.mChanId, null);
				return response.mResult;
			}
		}
		
		// 如果手机已经存在，则merge彼此的信息
		if(exists) {
			mergeInfoFromDb(dbPhoneInfo, existList.get(0));
			if(!mPhoneInfoService.updateRecordByInd(existList.get(0).getId(), dbPhoneInfo)) {
				Logger.getAnonymousLogger().log(Level.WARNING, MessageFormat.format(
						"手机信息idStr: 【{0}】更新失败！", dbPhoneInfo.getIdentifyStr()));
			}
		}
		
		// 生成用户信息数据单元
		UserInfo userInfo = getUserInfoFromRequest(requestData.mRequestObj, dbPhoneInfo.getIdentifyStr());
		if(null == userInfo) {
			AnalyseResult<Object, UserRegisterBeans.ResponseBean> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_REG_USR_GEN_FAIL, requestData.mChanId, null);
			return response.mResult;
		}
		userInfo.setChannelNo(requestData.mChanId);
		
		// 与数据库中的数据融合
		UserInfo existUserInfo = mUserInfoService.findRecordByKWs(
				userInfo.getName(), userInfo.getAppName(), userInfo.getChannelNo());
		if(null != existUserInfo) {
			mergeUserInfo(userInfo, existUserInfo);
		}
		
		// 数据入库
		userInfo.setState(UserInfo.M_STATE_LOGIN);
		userInfo.setSessionEstablishTime(DateTimeUtil.getCurrentDateTimeStr("yyyy-MM-dd HH:mm:ss"));
		userInfo.setSessionId(generateSessionId());
		try {
			String ip = IpUtils.getIpAddr(request);
			userInfo.setIp(ip);
			String country = GeoUtils.parseCountryCode(ip);
			userInfo.setCountry(country);
		} catch (Exception e) {
			AnalyseResult<Object, UserRegisterBeans.ResponseBean> response =
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_IP_PARSER_FAIL, requestData.mChanId, null);
			return response.mResult;
		}

		if(null == existUserInfo) {
			if(!mUserInfoService.addRecord(userInfo, dbPhoneInfo, null)) {
				AnalyseResult<Object, UserRegisterBeans.ResponseBean> response = 
						mParserService.generateResponseStr(
								ErrorCodes.M_ERR_CODE_REG_INS_FAIL, requestData.mChanId, null);
				return response.mResult;
			}
		} else {
			userInfo.setPhoneInfoId(dbPhoneInfo.getIdentifyStr());
			userInfo.setPhoneNumber(existUserInfo.getPhoneNumber());
			if(!mUserInfoService.updateInfoRecordByInd(existUserInfo.getId(), userInfo)) {
				AnalyseResult<Object, UserRegisterBeans.ResponseBean> response = 
						mParserService.generateResponseStr(
								ErrorCodes.M_ERR_CODE_REG_UPD_FAIL, requestData.mChanId, null);
				return response.mResult;
			}
		}
		
		// 生成响应数据
		UserRegisterBeans.ResponseBean responseData = new UserRegisterBeans.ResponseBean();
		responseData.setFID(userInfo.getSessionId());
		responseData.setUID(userInfo.getName());
		
		AnalyseResult<Object, UserRegisterBeans.ResponseBean> response = 
				mParserService.generateResponseStr(
						ErrorCodes.M_ERR_CODE_SUCCEED, requestData.mChanId, responseData);
		
		return response.mResult;
	}
	
	@RequestMapping(value="user/login", method={RequestMethod.POST})
	@ResponseBody
	public String onLoginRequest(HttpServletRequest request) {
		AnalyseResult<UserLoginBeans.Request, UserLoginBeans.Response> requestData = 
				mParserService.deserializeRequestFirstLayer(request, UserLoginBeans.Request.class, true);
		
		// 如果失败，直接发送响应
		if(!requestData.mIsSucceed) {
			AnalyseResult<Object, UserLoginBeans.Response> response = 
					mParserService.generateResponseStr(requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 未解析到有效数据
		if(requestData.mRequestObj == null) {
			AnalyseResult<Object, UserLoginBeans.Response> response = 
					mParserService.generateResponseStr(requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 查询用户是否已经注册
		UserInfo existRecord = mUserInfoService.findRecordByKWs(
				requestData.mRequestObj.getUserName(), requestData.mRequestObj.getAppName(), requestData.mChanId);
		if(null == existRecord) {
			AnalyseResult<Object, UserLoginBeans.Response> response = 
					mParserService.generateResponseStr(ErrorCodes.M_ERR_CODE_USER_UNREGISTER, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 建立会话数据
		existRecord.setSessionEstablishTime(DateTimeUtil.getCurrentDateTimeStr("yyyy-MM-dd HH:mm:ss"));
		existRecord.setSessionId(generateSessionId());
		existRecord.setState(UserInfo.M_STATE_LOGIN);
		
		// 入库
		boolean succeed = mUserInfoService.updateSessionByInd(existRecord.getId(), existRecord.getSessionId(), 
				existRecord.getSessionEstablishTime(), existRecord.getState());
		if(!succeed) {
			AnalyseResult<Object, UserLoginBeans.Response> response = 
					mParserService.generateResponseStr(ErrorCodes.M_ERR_CODE_LOGIN_STATE_SAVE_FAIL, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 组织响应数据
		UserLoginBeans.Response responseData = new UserLoginBeans.Response();
		responseData.setSessionId(existRecord.getSessionId());
		AnalyseResult<Object, UserLoginBeans.Response> response = 
				mParserService.generateResponseStr(ErrorCodes.M_ERR_CODE_SUCCEED, requestData.mChanId, responseData);
		
		return response.mResult;
	}
	
	@RequestMapping(value="user/report", method={RequestMethod.POST})
	@ResponseBody
	public String onPhoneReport(HttpServletRequest request) {
		AnalyseResult<PhoneReportBeans.Request, PhoneReportBeans.Response> requestData = 
				mParserService.deserializeRequestFirstLayer(
						request, PhoneReportBeans.Request.class, true);
		// 如果失败，直接发送响应
		if(!requestData.mIsSucceed) {
			AnalyseResult<Object, PhoneReportBeans.Response> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 未解析到有效数据
		if(null == requestData.mRequestObj) {
			AnalyseResult<Object, PhoneReportBeans.Response> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 无手机信息
		if(null == requestData.mRequestObj.getPhoneInfo()) {
			AnalyseResult<Object, PhoneReportBeans.Response> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_REPORT_LACK_PHONE, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 检查会话是否以已经过期
		ReturnEntity retRst = mSessionManagerService.checkSessionEstablished(
				requestData.mRequestObj.getUID(), requestData.mRequestObj.getAppName(), 
				requestData.mChanId, requestData.mRequestObj.getSessionId());
		if(!retRst.mIsSucceed) {
			AnalyseResult<Object, PhoneReportBeans.Response> response = 
					mParserService.generateResponseStr(
							retRst.mErrorCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 根据用户名、APP名称取出其手机信息
		UserInfo user = mUserInfoService.findRecordByKWs(
				requestData.mRequestObj.getUID(), requestData.mRequestObj.getAppName(), requestData.mChanId);
		if(null == user) {
			AnalyseResult<Object, PhoneReportBeans.Response> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_USER_UNREGISTER, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 组织手机信息数据单元
		PhoneInfo phoneFromRequest = getDbPhoneInfoFromRequest(requestData.mRequestObj.getPhoneInfo());
		if(null == phoneFromRequest) {
			AnalyseResult<Object, PhoneReportBeans.Response> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_REG_DB_PH_INFO_FAIL, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 查出使用的手机信息
		PhoneInfo existPhone = mPhoneInfoService.findRecordByIdentifyStr(user.getPhoneInfoId());
		if(null == existPhone) {
			// 模糊查询
			List<PhoneInfo> exists = mPhoneInfoService.findRecordsByKWs(
					requestData.mRequestObj.getPhoneInfo().getIMEI(), requestData.mRequestObj.getPhoneInfo().getMac(), 
					requestData.mRequestObj.getPhoneInfo().getSerialNo(), requestData.mRequestObj.getPhoneInfo().getAndroidId());
			if(!exists.isEmpty()) {
				existPhone = exists.get(0);
			}
		}
		
		// 根据新增手机和留存手机分别进行处理
		if(null == existPhone) {
			// 新增
			mPhoneInfoService.addRecord(phoneFromRequest);
		} else {
			// 留存
			mergeInfoFromDb(phoneFromRequest, existPhone);
			mPhoneInfoService.updateRecordByInd(existPhone.getId(), phoneFromRequest);
		}
		
		// 更新用户列表
		if(!StringUtils.equals(phoneFromRequest.getIdentifyStr(), user.getPhoneInfoId())) {
			user.setPhoneInfoId(phoneFromRequest.getIdentifyStr());
			if(!mUserInfoService.updateInfoRecordByInd(user.getId(), user)) {
				Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("用户【{0}】更新失败！", user.getName()));
			}
		}
		
		// 响应数据
		AnalyseResult<Object, PhoneReportBeans.Response> response = 
				mParserService.generateResponseStr(
						ErrorCodes.M_ERR_CODE_SUCCEED, requestData.mChanId, null);
		return response.mResult;
	}
	
	@RequestMapping(value="user/privs", method={RequestMethod.POST})
	@ResponseBody
	public String onUserPrivilegesReport(HttpServletRequest request) {
		AnalyseResult<UserPrivilegesReportBeans.Request, UserPrivilegesReportBeans.Response> requestData = 
				mParserService.deserializeRequestFirstLayer(
						request, UserPrivilegesReportBeans.Request.class, true);
		// 如果失败，直接发送响应
		if(!requestData.mIsSucceed) {
			AnalyseResult<Object, UserPrivilegesReportBeans.Response> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 未解析到有效数据
		if(null == requestData.mRequestObj) {
			AnalyseResult<Object, UserPrivilegesReportBeans.Response> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 检查会话是否以已经过期
		ReturnEntity retRst = mSessionManagerService.checkSessionEstablished(
				requestData.mRequestObj.getUserId(), requestData.mRequestObj.getAppName(), 
				requestData.mChanId, requestData.mRequestObj.getSessionId());
		if(!retRst.mIsSucceed) {
			AnalyseResult<Object, UserPrivilegesReportBeans.Response> response = 
					mParserService.generateResponseStr(
							retRst.mErrorCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 更新数据库
		mUserInfoService.updateUserPrivileges(requestData.mRequestObj.getUserId(), 
				requestData.mRequestObj.getPrivileges());
//		boolean succeed = mUserInfoService.updateUserPrivileges(requestData.mRequestObj.getUserId(), 
//				requestData.mRequestObj.getPrivileges());
//		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
//				"用户【{0}】更新权限是否成功：【{1}】", 
//				requestData.mRequestObj.getUserId(), succeed ? "成功" : "失败"));
		
		// 下发响应数据
		AnalyseResult<Object, UserPrivilegesReportBeans.Response> response = 
				mParserService.generateResponseStr(
						requestData.mErrCode, requestData.mChanId, null);
		return response.mResult;
	}
	
	private PhoneInfo getDbPhoneInfoFromRequest(RequestPhoneInfo rqPhoneInfo) {
		if(null == rqPhoneInfo) {
			return null;
		}
		
		PhoneInfo res = new PhoneInfo();
		res.setApiLevel(rqPhoneInfo.getApiLevel());
		res.setBrand(rqPhoneInfo.getBrand());
		res.setFingerPrint(rqPhoneInfo.getFingerPrint());
		res.setIMEI(rqPhoneInfo.getIMEI());
		res.setIMSI(rqPhoneInfo.getIMSI());
		res.setMac(rqPhoneInfo.getMac());
		res.setModel(rqPhoneInfo.getModel());
		res.setScreenHeight(rqPhoneInfo.getScreenHeight());
		res.setScreenWidth(rqPhoneInfo.getScreenWidth());
		res.setSerialNo(rqPhoneInfo.getSerialNo());
		res.setUserAgent(rqPhoneInfo.getUserAgent());
		res.setAndroidId(rqPhoneInfo.getAndroidId());
		
		res.generateIdentifyStr();
		
		return res;
	}
	
	private void mergeInfoFromDb(PhoneInfo infoFromRequest, PhoneInfo infoFromDb) {
		if(null == infoFromRequest || null == infoFromDb) {
			return;
		}
		
		if(StringUtils.isEmpty(infoFromRequest.getIMEI())) {
			infoFromRequest.setIMEI(infoFromDb.getIMEI());
		}
		
		if(StringUtils.isEmpty(infoFromRequest.getMac())) {
			infoFromRequest.setMac(infoFromDb.getMac());
		}
		
		if(StringUtils.isEmpty(infoFromRequest.getSerialNo())) {
			infoFromRequest.setSerialNo(infoFromDb.getSerialNo());
		}
		
		if(StringUtils.isEmpty(infoFromRequest.getAndroidId())) {
			infoFromRequest.setAndroidId(infoFromDb.getAndroidId());
		}
		
		infoFromRequest.setIdentifyStr(infoFromDb.getIdentifyStr());
		infoFromRequest.setId(infoFromDb.getId());
		
		if(infoFromRequest.getApiLevel() <= 0) {
			infoFromRequest.setApiLevel(infoFromDb.getApiLevel());
		}
		
		if(StringUtils.isEmpty(infoFromRequest.getBrand())) {
			infoFromRequest.setBrand(infoFromDb.getBrand());
		}
		
		if(StringUtils.isEmpty(infoFromRequest.getModel())) {
			infoFromRequest.setModel(infoFromDb.getModel());
		}
		
		if(StringUtils.isEmpty(infoFromRequest.getFingerPrint())) {
			infoFromRequest.setFingerPrint(infoFromDb.getFingerPrint());
		}
		
		if(StringUtils.isEmpty(infoFromRequest.getIMSI())) {
			infoFromRequest.setIMSI(infoFromDb.getIMSI());
		}
		
		if(infoFromRequest.getScreenHeight() <= 0) {
			infoFromRequest.setScreenHeight(infoFromDb.getScreenHeight());
		}
		
		if(infoFromRequest.getScreenWidth() <= 0) {
			infoFromRequest.setScreenWidth(infoFromDb.getScreenWidth());
		}
		
		if(StringUtils.isEmpty(infoFromRequest.getUserAgent())) {
			infoFromRequest.setUserAgent(infoFromDb.getUserAgent());
		}
		
	}
	
	private UserInfo getUserInfoFromRequest(UserRegisterBeans.RequestBean bean, String userName) {
		if(null == bean) {
			return null;
		}
		
		if(StringUtils.isEmpty(userName)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "用户名为空，无法查询用户是否存在");
			return null;
		}
		
		if(StringUtils.isEmpty(bean.getAppName())) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "app名称为空，无法查询用户是否存在");
			return null;
		}
		
		UserInfo res = new UserInfo();
		res.setAppName(bean.getAppName());
		res.setName(userName);
		
		return res;
	}
	
	private void mergeUserInfo(UserInfo requestInfo, UserInfo existInfo) {
		if(null == requestInfo || null == existInfo) {
			return;
		}
		
		requestInfo.setId(existInfo.getId());
		requestInfo.setPhoneInfoInvalidReason(existInfo.getPhoneInfoInvalidReason());
		requestInfo.setPhoneInfoValid(existInfo.isPhoneInfoValid());
	}
	
	private String generateSessionId() {
		return DateTimeUtil.getCurrentDateTimeStr("yyyyMMddHHmmss") + RandomUtil.getRandomStr(5);
	}
	
	@RequestMapping(value = "user/absence", method = {RequestMethod.POST})
	@ResponseBody
	public String onClientHeartbeat(HttpServletRequest request) {
		AnalyseResult<HeartbeatBeans, Object> requestData = 
				mParserService.deserializeRequestFirstLayer(request, HeartbeatBeans.class, true);
		
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
		if(StringUtils.isEmpty(requestData.mRequestObj.getPkg())) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "应用名称为空！");
			
			AnalyseResult<Object, Object> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_APP_NAME_INVALID, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 用户名为空
		if(StringUtils.isEmpty(requestData.mRequestObj.getUID())) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "uid为空！");
			AnalyseResult<Object, Object> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_USER_UNREGISTER, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 存数据
		mUserInfoService.addOrUpdateByAbsence(requestData.mChanId, requestData.mRequestObj);
		
		// 下发响应数据
		AnalyseResult<Object, UserPrivilegesReportBeans.Response> response = 
				mParserService.generateResponseStr(
						requestData.mErrCode, requestData.mChanId, null);
		return response.mResult;
	}
	
	private static final String M_JSP_NAME_STATISTICS = "user_statistics";
	
	/**
	 * 统计页面加载
	 * @param request
	 * @param model
	 * @return 
	 */
	@RequestMapping(value = "statistics", method = {RequestMethod.GET, RequestMethod.POST})
	public String onStatisticsPageLoad(HttpServletRequest request, Model model) {
		String startTime = request.getParameter("campaign_time");
		String chanNo = request.getParameter("chan_no");
		
		String res = M_JSP_NAME_STATISTICS;
		
		if(StringUtils.isEmpty(startTime) || StringUtils.isEmpty(chanNo)) {
			res = processOnPageLoad();
		} else {
			res = processOnQuery(model, startTime, chanNo);
		}
		
		return res;
	}
	
	private String processOnPageLoad() {
		return M_JSP_NAME_STATISTICS;
	}
	
	private String processOnQuery(Model model, String queryTime, String chanNo) {
		String res = M_JSP_NAME_STATISTICS;
		
		Timestamp start = DateTimeUtil.getTimestampFromStr(queryTime);
		if(null == start) {
			model.addAttribute("msg", "推广时间不合法，无法查询！");
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("startTime【{0}】不合法！", queryTime));
			return res;
		}
		
		if(StringUtils.isEmpty(chanNo)) {
			model.addAttribute("msg", "渠道号为空，无法查询！");
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("渠道号为空！", chanNo));
			return res;
		}
		
		// 调整起始时间为当天0时0分0秒
		Timestamp startTime = DateTimeUtil.addDate(start, 0, "00:00:00");
		Timestamp endTime = DateTimeUtil.addDate(start, 0, "23:59:59");
		if(null == endTime) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"根据查询起始时间【{0}】未找到当天的结束时间", start.toString()));
			return res;
		}
		
		// 获取当天所有的激活用户
		List<UserInfo> allActivedUsers = mUserInfoService.findRecordsByChanAndCreateTime(chanNo, startTime, endTime);
		if(allActivedUsers.isEmpty()) {
			model.addAttribute("msg", "查询无结果！");
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"渠道号【{0}】--推广起始时间【{1}】--推广结束时间【{2}】无对应记录！", 
					chanNo, startTime.toString(), endTime.toString()));
			return res;
		}
		
		float nxtDayAlive = getAliveRate(chanNo, allActivedUsers, startTime, 1);
		float twoDaysAlive = getAliveRate(chanNo, allActivedUsers, startTime, 2);
		float threeDaysAlive = getAliveRate(chanNo, allActivedUsers, startTime, 3);
		float fiveDaysAlive = getAliveRate(chanNo, allActivedUsers, startTime, 5);
		float weekAlive = getAliveRate(chanNo, allActivedUsers, startTime, 7);
		float fifteenDaysAlive = getAliveRate(chanNo, allActivedUsers, startTime, 15);
		float monthAlive = getAliveRate(chanNo, allActivedUsers, startTime, 30);
		
		model.addAttribute("ch", chanNo);
		model.addAttribute("time", startTime.toString());
		model.addAttribute("allActived", allActivedUsers.size());
		model.addAttribute("nxtAlive", getAliveString(nxtDayAlive));
		model.addAttribute("twoAlive", getAliveString(twoDaysAlive));
		model.addAttribute("threeAlive", getAliveString(threeDaysAlive));
		model.addAttribute("fiveAlive", getAliveString(fiveDaysAlive));
		model.addAttribute("weekAlive", getAliveString(weekAlive));
		model.addAttribute("halfMonthAlive", getAliveString(fifteenDaysAlive));
		model.addAttribute("monthAlive", getAliveString(monthAlive));
		
		return res;
	}
	
	private static final String M_NA_STRING = "N/A";
	
	private float getAliveRate(String chan, List<UserInfo> collection, Timestamp startTime, int dayInterval) {
		if(StringUtils.isEmpty(chan)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "chan为空，无法查询用户留存！");
			return M_NA_VAL;
		}
		
		float res = 0.0f;
		if(null == collection || collection.isEmpty()) {
			return res;
		}
		
		if(null == startTime) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "start time为null, 无法计算留存率！");
			return res;
		}
		
		if(dayInterval < 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"dayInterval[{0}]不合法，无法计算留存率！", dayInterval));
			return res;
		}
		
		// 获取今天0点的时间戳
		Timestamp today = DateTimeUtil.addDate(new Timestamp(System.currentTimeMillis()), 0, "00:00:00");
		
		// 调整查询起始时间到当天凌晨
		Timestamp leftTime = DateTimeUtil.addDate(startTime, dayInterval, "00:00:00");
		
		// 如果要计算的时间大于等于今天的，则不予计算
		if(leftTime.after(today)) {
			return M_NA_VAL;
		}
		
		// 取活跃大于限定时间之后的用户
		List<UserInfo> queryRes = new ArrayList<UserInfo>();
		for(UserInfo user : collection) {
			if(null == user) {
				continue;
			}
			
			if(user.getRecentHandshake() == null) {
				continue;
			}
			
			if(user.getRecentHandshake().after(leftTime)) {
				queryRes.add(user);
			}
		}
		
		return (float) queryRes.size() / (float) collection.size();
	}
	
	private static final float M_NA_VAL = 9999.5f;
	
	private String getAliveString(float val) {
		String res = "";
		
		if(val == new Float(M_NA_VAL)) {
			res = M_NA_STRING;
		} else {
			BigDecimal bd = new BigDecimal(val * 100);
			float convertVal = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			res = Float.toString(convertVal) + "%";
		}
		
		return res;
	}
}
