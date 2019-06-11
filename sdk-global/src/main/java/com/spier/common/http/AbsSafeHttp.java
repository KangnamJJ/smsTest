package com.spier.common.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.spier.common.config.ErrorCodes;
import com.spier.common.utils.AESUtil;
import com.spier.common.utils.Base64Util;
import com.spier.common.utils.RSAUtils;
import com.spier.common.utils.RandomUtil;



/**
 * 封装了安全通信流程以及参数封装的的HTTP抽象基类
 * @author GHB
 * @version 1.0
 * @date 2018.12.10
 * @param <ResponseType> 响应的业务数据类型
 */
public abstract class AbsSafeHttp<ResponseType> extends Http {

	private static final Gson mGson = new GsonBuilder().
			disableHtmlEscaping().
			excludeFieldsWithoutExposeAnnotation().
			create();
	
	public static Gson getGson() {
		return mGson;
	}
	
	/**
	 * 构造方法
	 * @param url 请求地址
	 * @param channelId 取到号
	 * @param rkPub RSA公钥的base64编码后的字符串
	 * @throws IllegalArgumentException channelId为空或rkPub为空
	 */
	public AbsSafeHttp(String url, String channelId, String rkPub) throws IllegalArgumentException {
		super(url, null);
		
		if(StringUtils.isEmpty(channelId)) {
			throw new IllegalArgumentException("channelId is empty");
		}
		
		if(StringUtils.isEmpty(rkPub)) {
			throw new IllegalArgumentException("rkPub is empty");
		}
		
		mChannelId = channelId;
		mRkPub = rkPub;
	}

	private String mChannelId;
	
	/**
	 * 获取渠道号
	 * @return 不为null
	 */
	public String getChannelId() {
		return mChannelId;
	}
	
	private String mRkPub;
	
	public String getRKPub() {
		return mRkPub;
	}
	
	/**
	 * 获取相应对象
	 * @return 可能为null
	 */
	public ResponseType getResponseObj() {
		return mResponseObj;
	}
	
	private ResponseType mResponseObj;
	
	/**
	 * 将请求数据对象序列号成字符串
	 * @return 可以为null
	 */
	public abstract String serializeRequestObj();
	/**
	 * 将业务数据的json串反序列化成对象
	 * @param dataStr
	 * @return 可能为null
	 */
	public abstract ResponseType deserializeResponseObj(String dataStr);

	private static final int M_AES_KEY_LEN = 16;
	
	@Override
	public void accessInternet(HttpCtx ctx) {
		// 生成请求参数
		if(!generateRequestParam()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"failed to generate para for the http request[{0}], abort requesting", getUrl()));
			return;
		}
		
		// http请求
		super.accessInternet(ctx);
		if(!super.isSucceed()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"failed to request for the http[{0}]: {1}", 
					getUrl(), getErrorStr()));
			return;
		}
		
		// 解析请求参数
		if(!analyseRespose()) {
			setResponseAnalyseSucceed(false);
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"failed to analyse response for the http[{0}]", getUrl()));
			return;
		}
		
		setResponseAnalyseSucceed(true);
		
		Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("http request[{0}] succeed!", getUrl()));
	}
	
	private boolean mIsResponseAnalyseSucceed = false;
	
	/**
	 * 相应数据解析是否成功
	 * @return
	 */
	public boolean isResponseAnalyseSucceed() {
		return mIsResponseAnalyseSucceed;
	}
	
	private void setResponseAnalyseSucceed(boolean succeed) {
		mIsResponseAnalyseSucceed = succeed;
	}
	
	@Override
	public boolean isSucceed() {
		return super.isSucceed() & isResponseAnalyseSucceed() & isErrorCodeSucceed();
	}

	private boolean generateRequestParam() {
		// 序列号请求数据
		String requestStr = serializeRequestObj();
		if(StringUtils.isEmpty(requestStr)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "no request para");
			return true;
		}
		
		// 生成随机AES密钥明文
		String aesKClear = RandomUtil.getRandomStr(M_AES_KEY_LEN);
		if(aesKClear.length() < M_AES_KEY_LEN) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "failed to generate aes-clear-k");
			return false;
		}
		
		// 组织参数
		String requestStrCrypt = generateRequestEncStr(requestStr, aesKClear);
		if(StringUtils.isEmpty(requestStrCrypt)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "no request str generated");
			return false;
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("outlayer json: {0}", requestStrCrypt));
		
		setPara("s=" + requestStrCrypt);
		
		return true;
	}
	
	private String generateRequestEncStr(String requestStrClear, String akClear) {
		
		// 用AES密钥明文加密请求数据
		byte[] encDataBytes = AESUtil.encrypt(requestStrClear.getBytes(), akClear.getBytes());
		if(null == encDataBytes || encDataBytes.length == 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "no enc data bytes generated, abort requesting!");
			return null;
		}
		
		// Base64编码
		String encDataStr = Base64Util.base64EncodeToStr(encDataBytes);
		if(StringUtils.isEmpty(encDataStr)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "no enc data string generated, abort requesting!");
			return null;
		}
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("data-clear: {0}", requestStrClear));
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("data-cipher: {0}", encDataStr));
		
		// 加密AES密钥
		byte[] encAKBytes = RSAUtils.encryptWithPubKey(akClear.getBytes(), getRKPub());
		if(null == encAKBytes || encAKBytes.length == 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "no enc aes-key bytes generated, abort requesting!");
			return null;
		}
		
		// base64编码
		String aesKCipher = Base64Util.base64EncodeToStr(encAKBytes);
		if(StringUtils.isEmpty(aesKCipher)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "no aes-cipher generated, abort requesting!");
			return null;
		}
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("ak-clear: {0}", akClear));
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("ak-cipher: {0}", aesKCipher));
		
		// 组织参数
		JsonObject obj = new JsonObject();
		obj.addProperty("c", modifyRequestString(getChannelId()));
		obj.addProperty("k", modifyRequestString(aesKCipher));
		obj.addProperty("d", modifyRequestString(encDataStr));
//		try {
//			obj.put("c", modifyRequestString(getChannelId()))
//				.put("k", modifyRequestString(aesKCipher))
//				.put("d", modifyRequestString(encDataStr));
//		} catch (JSONException e) {
//			e.printStackTrace();
//			return null;
//		}
		
		return obj.toString();
	}
	
	// 去除base64编码时添加的回车换行、以及特殊字符的处理
	private String modifyRequestString(String src) {
		if(StringUtils.isEmpty(src)) {
			return src;
		}
		
		// 去除回车换行
		String res = src.replaceAll("\r|\n", "");
		
		// 替换特殊字符
		try {
			res = URLEncoder.encode(res, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	private boolean analyseRespose() {
		if(StringUtils.isEmpty(getHtml())) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"no response html found to the http[{0}], cannot analyse.", getUrl()));
			return false;
		}
		
		if(StringUtils.isEmpty(getRKPub())) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"no pubk for dec the response of the http[{0}]", getUrl()));
			return false;
		}
		
		// 反序列化相应
		ResponseEntity entity = null;
		try {
			entity = mGson.fromJson(getHtml(), ResponseEntity.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(null == entity) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "response反序列化失败！");
			return false;
		}
//		try {
//			JsonObject rootObj = new JsonObject(getHtml());
//			String content = JsonUtils.getString(rootObj, "c");
//			int errCode = rootObj.getInt("cd");
//			
//			entity = new ResponseEntity();
//			entity.mContent = content;
//			entity.mErrCode = errCode;
//		} catch (JSONException e) {
//			e.printStackTrace();
//			return false;
//		}
		
		mResponseErrorCode = entity.mErrCode;
		
		if(entity.mErrCode != ResponseEntity.M_ERRCD_SUCCEED) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"bussiness error-code received[{0}], request[{1}] failed, responce content [{2}]", 
					entity.mErrCode, getUrl(), entity.mContent));
			return false;
		}
		
		if(StringUtils.isEmpty(entity.mContent)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"requset[{0}] succeed, and no respose data.", getUrl()));
			return true;
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("response html: {0}", entity.mContent));
		
		// 解码业务数据
		byte[] encData = null;
		try {
			encData = Base64Util.base64Decode(entity.mContent.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(encData == null || encData.length == 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE,"no b64-dec data, abort analysing");
			return false;
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("response cipher: {0}", Arrays.toString(encData)));
		
		// 解密数据
		String decDataStr = new String(RSAUtils.decryptWithPubK(encData, getRKPub()));
		if(StringUtils.isEmpty(decDataStr)) {
			Logger.getAnonymousLogger().log(Level.SEVERE,"no decData, abort analysing");
			return false;
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("response clear: {0}", decDataStr));
		
		// 反序列化业务数据
		mResponseObj = deserializeResponseObj(decDataStr);
		if(null == mResponseObj) {
			Logger.getAnonymousLogger().log(Level.SEVERE,"no response obj deserialized.");
			return false;
		}
		
		return true;
	}
	
	private int mResponseErrorCode = ErrorCodes.M_ERR_CODE_NOT_ANALYSE;
	
	/**
	 * 获取响应错误码，大于等于1为后台响应数据，否则是客户端未赋值数据
	 * @return
	 */
	public int getResponseErrorCode() {
		return mResponseErrorCode;
	}
	
	/**
	 * 响应数据中，错误码这一层是否正确
	 * @return
	 */
	public boolean isErrorCodeSucceed() {
		return getResponseErrorCode() == ResponseEntity.M_ERRCD_SUCCEED;
	}

	private static class ResponseEntity {
		@Expose
		@SerializedName("cd")
		public int mErrCode;
		
		@Expose
		@SerializedName("c")
		public String mContent;
		
		public static final int M_ERRCD_SUCCEED = 1;
	}
}
