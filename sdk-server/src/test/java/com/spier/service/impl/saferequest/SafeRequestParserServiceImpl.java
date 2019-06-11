/*package com.spier.service.impl.saferequest;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spier.common.config.ErrorCodes;
import com.spier.common.utils.AnalyseResult;
import com.spier.common.utils.RequestAnalyseUtil;
import com.spier.common.utils.RequestAnalyseUtil.CodecResult;
import com.spier.entity.SafeRequestOutlayerObj;
import com.spier.entity.SafeResponseObj;
import com.spier.service.channel.IChannelInfoService;
import com.spier.service.saferequest.ISafeRequestParserService;

*//**
 * 安全通信接口解析服务实现类
 * @author GHB
 * @version 1.0
 * @date 2018.12.31
 *//*
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class SafeRequestParserServiceImpl implements ISafeRequestParserService {
	
	@Autowired
	private IChannelInfoService mChanInfoService;

	private static final Gson mGson = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
			.disableHtmlEscaping()
			.create();
	
	@SuppressWarnings("unchecked")
	@Override
	public <RequestType, ResponseType> AnalyseResult<RequestType, ResponseType> deserializeRequestFirstLayer(
			HttpServletRequest request, Class<?> requestObjClazz, boolean requestHasParam) {
		AnalyseResult<RequestType, ResponseType> res = AnalyseResult.<RequestType, ResponseType>getDefaultObj();
		
		if(null == requestObjClazz) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "请求对象的类型为null，无法解析！");
			return res;
		}
		
		String outlayerJson = request.getParameter("s");
		if(StringUtils.isEmpty(outlayerJson)) {
			res.mIsSucceed = false;
			res.mErrCode = ErrorCodes.M_ERR_CODE_NO_OUTLAYER_REQUEST;
			return res;
		}
		
		// 反序列化最外层json
		SafeRequestOutlayerObj outlayerRequestObj = null;
		try {
//			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("outlayer json: {0}", outlayerJson));
			
			outlayerRequestObj =  mGson.fromJson(outlayerJson, SafeRequestOutlayerObj.class);
		} catch (Exception e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, e.getLocalizedMessage());
			res.mIsSucceed = false;
			res.mErrCode = ErrorCodes.M_ERR_CODE_DESERIALIZE_OUTLAYER;
			return res;
		}
		
		// 检查渠道号是否填写
		if(StringUtils.isEmpty(outlayerRequestObj.mChannelId)) {
			res.mIsSucceed = false;
			res.mErrCode = ErrorCodes.M_ERR_CODE_NO_CHANNEL_ID;
			return res;
		}
		
		// 记录渠道号
		res.mChanId = outlayerRequestObj.mChannelId;
		
		// 根据渠道号取出RSA私钥
		String rsaPrivKStr = getRSAPrivateKeyStrByChannelId(outlayerRequestObj.mChannelId);
		if(StringUtils.isEmpty(rsaPrivKStr)) {
			res.mIsSucceed = false;
			res.mErrCode = ErrorCodes.M_ERR_CODE_RSA_PRIV_MPTY;
			return res;
		}
		
		// 如果是无参请求，则直接返回
		if(!requestHasParam) {
			res.mIsSucceed = true;
			res.mErrCode = ErrorCodes.M_ERR_CODE_SUCCEED;
			return res;
		}
		
		// 解析请求数据
		CodecResult analyseRst = RequestAnalyseUtil.analyseJsonFromRequest(
				outlayerRequestObj.mBussinessData, outlayerRequestObj.mAesKeyCipher, rsaPrivKStr);
		if(!analyseRst.mIsSucceed) {
			res.mErrCode = analyseRst.mErrCode;
			res.mIsSucceed = false;
			return res;
		}
		
		// 反序列化请求业务数据
		try {
			res.mRequestObj = (RequestType) mGson.fromJson(analyseRst.mResult, requestObjClazz);
			res.mIsSucceed = true;
			res.mErrCode = ErrorCodes.M_ERR_CODE_SUCCEED;
		} catch (Exception e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, e.getLocalizedMessage());
			res.mIsSucceed = false;
			res.mErrCode = ErrorCodes.M_ERR_CODE_DESERIALIZE_OUTLAYER;
			return res;
		}
		
		return res;
	}
	
	@Override
	public <RequestType, ResponseType> AnalyseResult<RequestType, ResponseType> generateResponseStr(int errorCode,
			String chanId, ResponseType responseObj) {
		AnalyseResult<RequestType, ResponseType> res = AnalyseResult.<RequestType, ResponseType>getDefaultObj();
		
		SafeResponseObj obj = new SafeResponseObj();
		
		obj.mErrorCode = errorCode;
		
		try {
			String responseJson = serializeObj(responseObj);
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("json序列化后的业务数据：{0}", responseJson));
			CodecResult serialize = RequestAnalyseUtil.generateResponseStr(responseJson, getRSAPrivateKeyStrByChannelId(chanId));
			obj.mResponseContent = serialize.mResult;
			res.mIsSucceed = true;
		} catch (Exception e) {
			e.printStackTrace();
			res.mIsSucceed = false;	
		}
		
		res.mResult = serializeObj(obj);
		res.mErrCode = obj.mErrorCode;
		res.mChanId = chanId;
		
//		if(errorCode != ErrorCodes.M_ERR_CODE_SUCCEED) {
//			obj.mErrorCode = errorCode;
//		} else if(responseObj != null) {
//			try {
//				String responseJson = serializeObj(responseObj);
//				Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("json序列化后的业务数据：{0}", responseJson));
//				CodecResult serialize = RequestAnalyseUtil.generateResponseStr(responseJson, getRSAPrivateKeyStrByChannelId(chanId));
//				if(!serialize.mIsSucceed) {
//					obj.mErrorCode = serialize.mErrCode;
//				} else {
//					obj.mErrorCode = serialize.mErrCode;
//					obj.mResponseContent = serialize.mResult;
//				}
//			} catch (Exception e) {
//				obj.mErrorCode = ErrorCodes.M_ERR_CODE_RESP_DATA_SERIALIZE;
//			}
//		} else {
//			Logger.getAnonymousLogger().log(Level.WARNING, "no response data generated for the request");
//			obj.mErrorCode = ErrorCodes.M_ERR_CODE_SUCCEED;
//		}
//		
//		res.mResult = serializeObj(obj);
//		res.mErrCode = obj.mErrorCode;
//		res.mIsSucceed = (obj.mErrorCode == ErrorCodes.M_ERR_CODE_SUCCEED);
		
//		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("响应数据json：{0}", res.mResult));
		
		return res;
	}

	private String getRSAPrivateKeyStrByChannelId(String chanNo) {
		return mChanInfoService.getRSAPrivKB64StrByChanNo(chanNo);
	}
	
	private <ObjType> String serializeObj(ObjType obj) {
		if(null == obj) {
			return null;
		}
		
		String res = null;
		
		// 如果toJson的参数是string类型，则经过序列化之后会在外层加一个引号包裹
		if(obj instanceof String) {
			res = (String) obj;
		} else {
			res = mGson.toJson(obj);
		}
		
		return res;
	}
}
*/