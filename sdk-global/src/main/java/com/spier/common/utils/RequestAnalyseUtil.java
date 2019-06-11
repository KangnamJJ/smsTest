package com.spier.common.utils;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.spier.common.config.ErrorCodes;

/**
 * 客户端请求解析工具
 * @author GHB
 * @version 1.0
 * @dat 2018.12.12
 */
public class RequestAnalyseUtil {
	
	public static final String M_CHARSET_UTF8 = "UTF-8";

	
	
	/**
	 * 请求或响应数据加解密结果
	 * @author GHB
	 * @version 1.0
	 * @date 2018.12.12
	 */
	public static class CodecResult {
		/**
		 * 解析过程是否成功
		 */
		public boolean mIsSucceed;
		/**
		 * 解析成功后的结果，解析失败为null
		 */
		public String mResult;
		/**
		 * 如果发生错误，此属性会被赋值，默认是还未解析：{@link RequestAnalyseUtil#M_ERR_CODE_NOT_ANALYSE}
		 */
		public int mErrCode;
		
		public static CodecResult getDefault() {
			return new CodecResult();
		}
		
		public CodecResult() {
			mIsSucceed = false;
			mResult = null;
			mErrCode = ErrorCodes.M_ERR_CODE_NOT_ANALYSE;
		}
	}
	
	/**
	 * 从请求的'd'部分解析出请求的json字符串。如果请求的'd'部分设定为空，则无需调用此方法。
	 * @param requestData 请求的'd'部分的
	 * @param aesKeyEncode
	 * @param rsaPrivKeyB64Str
	 * @return 解析结果，不为null
	 */
	public static CodecResult analyseJsonFromRequest(String requestDataStr, String aesKeyEncode, String rsaPrivKeyB64Str) {
		CodecResult res = CodecResult.getDefault();
		
		if(requestDataStr == null || requestDataStr.isEmpty()) {
			res.mIsSucceed = false;
			res.mErrCode = ErrorCodes.M_ERR_CODE_REQUEST_DATA_EMPTY;
			return res;
		}
		
		if(aesKeyEncode == null || aesKeyEncode.isEmpty()) {
			res.mIsSucceed = false;
			res.mErrCode = ErrorCodes.M_ERR_CODE_REQUEST_AES_EMPTY;
			return res;
		}
		
		if(rsaPrivKeyB64Str == null || rsaPrivKeyB64Str.isEmpty()) {
			res.mIsSucceed = false;
			res.mErrCode = ErrorCodes.M_ERR_CODE_RSA_PRIV_MPTY;
			return res;
		}
		
		// aeskey base64解码
//		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("AES-Key cipher: {0}", aesKeyEncode));
		
		byte[] aesKEncData = Base64Util.base64Decode(aesKeyEncode.getBytes());
		if(null == aesKEncData || aesKEncData.length == 0) {
			res.mIsSucceed = false;
			res.mErrCode = ErrorCodes.M_ERR_CODE_REQUEST_AES_DEC_B64;
			return res;
		}
		
		// 先用rsa私钥解密aeskey
		byte[] aesK = RSAUtils.decryptWithPrivKeyStr(aesKEncData, rsaPrivKeyB64Str);
		if(null == aesK || aesK.length == 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"使用私钥【{0}】解密AESkey失败！", rsaPrivKeyB64Str));
			res.mIsSucceed = false;
			res.mErrCode = ErrorCodes.M_ERR_CODE_AES_KEY_DEC_CLEAR;
			return res;
		}
		
//		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("AES-key clear: {0}", new String(aesK)));
		
		// 业务数据base64解码
		byte[] requestData = Base64Util.base64Decode(requestDataStr.getBytes());
		if(null == requestData || requestData.length == 0) {
			res.mIsSucceed = false;
			res.mErrCode = ErrorCodes.M_ERR_CODE_REQUEST_DATA_B64_DEC;
			return res;
		}
		
		byte[] requestDataDec = AESUtil.decrypt(requestData, aesK);
		if(null == requestDataDec || requestDataDec.length == 0) {
			res.mIsSucceed = false;
			res.mErrCode = ErrorCodes.M_ERR_CODE_REQUEST_DATA_AES_DEC;
			return res;
		}
		
		try {
			res.mResult = new String(requestDataDec, M_CHARSET_UTF8);
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("data-clear: {0}", res.mResult));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			res.mIsSucceed = false;
			res.mErrCode = ErrorCodes.M_ERR_CODE_REQUEST_DATA_NOT_UTF8;
			return res;
		}
		
		res.mIsSucceed = true;
		res.mErrCode = ErrorCodes.M_ERR_CODE_SUCCEED;
		
		return res;
	}
	
	/**
	 * 生成加密后的响应数据，即'c'部分和'cd'部分
	 * @param responseJson 响应的业务数据的json序列化字符串
	 * @param rsaPrivKB64 rsa私钥的base64编码后的字符串
	 * @return 不为null
	 */
	public static CodecResult generateResponseStr(String responseJson, String rsaPrivKB64) {
		CodecResult res = CodecResult.getDefault();
		
		if(StringUtils.isEmpty(responseJson)) {
			res.mIsSucceed = true;
			res.mErrCode = ErrorCodes.M_ERR_CODE_SUCCEED;
			return res;
		}
		
//		if(null == responseJson || responseJson.isEmpty()) {
//			res.mIsSucceed = false;
//			res.mErrCode = ErrorCodes.M_ERR_CODE_RESP_DATA_EMPTY;
//			return res;
//		}
		
		if(null == rsaPrivKB64 || rsaPrivKB64.isEmpty()) {
			res.mIsSucceed = false;
			res.mErrCode = ErrorCodes.M_ERR_CODE_RSA_PRIV_MPTY;
			return res;
		}
		
		// rsa加密
//		Logger.getAnonymousLogger().log(Level.INFO, "RSA私钥加密……");
		byte[] respData = RSAUtils.encryptWithPrivKey(responseJson.getBytes(), rsaPrivKB64);
		if(null == respData || respData.length == 0) {
			res.mIsSucceed = false;
			res.mErrCode = ErrorCodes.M_ERR_CODE_RSA_PRIV_ENC;
			return res;
		}
		
//		Logger.getAnonymousLogger().log(Level.INFO, 
//				MessageFormat.format("reponse data cipher: {0}", Arrays.toString(respData)));
		
		// base64编码
//		Logger.getAnonymousLogger().log(Level.INFO, "BASE64编码……");
//		byte[] respDataEnc = URLSafeBase64Utils.bas64Encode(respData);
		byte[] respDataEnc = Base64Util.bas64Encode(respData);
		if(null == respDataEnc || respDataEnc.length == 0) {
			res.mIsSucceed = false;
			res.mErrCode = ErrorCodes.M_ERR_CODE_RESP_DATA_B64;
			return res;
		}
		
		// 生成响应正文字符串
//		Logger.getAnonymousLogger().log(Level.INFO, "生成响应正文字符串……");
		try {
			res.mResult = new String(respDataEnc, M_CHARSET_UTF8);
		} catch (UnsupportedEncodingException e) {
			res.mIsSucceed = false;
			res.mErrCode = ErrorCodes.M_ERR_CODE_RESP_DATA_UTF8;
			return res;
		}
		
		res.mIsSucceed = true;
		res.mErrCode = ErrorCodes.M_ERR_CODE_SUCCEED;
		
		return res;
	}
}
