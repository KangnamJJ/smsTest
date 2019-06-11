package com.spier.common.http;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.spier.common.http.XZAnnotations.KVSerialize;
import com.spier.common.utils.KVSerializerUtils;

/**
 * 带有参数序列化功能的HTTP，序列化成如下格式：p1=xxx&p2=xxx
 * @author GHB
 * @version 1.0
 */
public class KVSerializerHTTP extends Http implements IKVSerializer, Serializable {

	private static final long serialVersionUID = -6489878118570891699L;

	/**
	 * 默认开启urlEncode：false
	 */
	public static final boolean DEFAULT_ENABLE_URL_ENCODE = false;
	
	public static final String DEFAULT_NAME = "KVSerializerHTTP";
	
	/**
	 * 构造方法
	 * @param url
	 * @param name 名称
	 * @param enableUrlEncode 是否开启MIME编码
	 */
	public KVSerializerHTTP(String url, String name, boolean enableUrlEncode) {
		super(url);
		
		setName(name);
		setEnableUrlEncode(enableUrlEncode);
	}
	
	/**
	 * 构造方法，默认开启MIME编码
	 * @param url
	 */
	public KVSerializerHTTP(String url) {
		this(url, DEFAULT_NAME, true);
	}
	
	private String mName;
	
	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	private boolean mEnableUrlEncode;
	
	/**
	 * 设置是否开启MIME编码
	 * @param enable
	 */
	public void setEnableUrlEncode(boolean enable) {
		mEnableUrlEncode = enable;
	}
	
	/**
	 * 当前是否开启MIME编码
	 * @return
	 */
	public boolean getUrlEncodeEnabled() {
		return mEnableUrlEncode;
	}
	
	private HttpCtx mCtx;
	
	@Override
	public void access(HttpCtx ctx) {
		mCtx = ctx;
		
		if(!generateRequestParams()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"failed to generate request params, cancel http request to the url[{0}].", getUrl()));
			return;
		}
		
		super.access(ctx);
	}

	@Override
	public void access() {
		mCtx = HttpCtx.getDefault();
		
		if(!generateRequestParams()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"failed to generate request params, cancel http request to the url[{0}].", getUrl()));
			return;
		}
		
		super.access();
	}

	/**
	 * 产生请求参数表，后续HTTP类会将其转化为请求参数
	 * @return
	 */
	private boolean generateRequestParams() {
		Map<String, String> pMap = serilize();
		if(null == pMap) {
			return true;
		}
		
		String params = extendsKVMap(pMap);
		if(null == params) {
			Logger.getAnonymousLogger().log(Level.INFO, "no params generated.");
		}
		
		setPara(params);
		
		return true;
	}
	
	@Override
	public Map<String, String> serilize() {
		Map<String, Object> rawParams = KVSerializerUtils.getMemberValuePairsByAnnotation(this, KVSerialize.class);
		if(null == rawParams || rawParams.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, "对象执行getMemberValuePairs()方法后返回值为null或空！");
			return null;
		}
		
		Map<String, String> params = getKVPairStrings(rawParams);
		if(null == params) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "key-value map is null, cannot generate parameterized string.");
			return null;
		}
		
		return params;
	}

	// 把键值对转换成字符串对
	private Map<String, String> getKVPairStrings(Map<String, Object> datus) {
		if(null == datus) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "argument is null, failed to invoke \"getKVPairStrings()\"");
			return null;
		}
		
		Map<String, String> res = new HashMap<String, String>();
		Set<Entry<String, Object>> entries = datus.entrySet();
		if(null == entries) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "no entry found, failed to invoke \"getKVPairStrings()\"");
			return res;
		}
		
		for(Entry<String, Object> entry : entries) {
			String key = entry.getKey();
			Object value = entry.getValue();
			
			if(null == key || null == value) {
				Logger.getAnonymousLogger().log(Level.INFO, "key or value is null, ignore.");
				continue;
			}
			
			Map<String, String> unit = KVSerializerUtils.getObjectValueString(key, value);
			if(null == unit || unit.isEmpty()) {
				Logger.getAnonymousLogger().log(Level.WARNING, MessageFormat.format(
						"the value of the key[{0}] is null or empty, will not be put into the map.", key));
				continue;
			}
			
			res.putAll(unit);
		}
		
		return res;
	}
	
	
	private String extendsKVMap(Map<String, String> map) {
		String charSet = mCtx == null ? "UTF-8" : mCtx.getCharset();
		
		return KVSerializerUtils.extendsKVMap2HttpParams(map, getUrlEncodeEnabled(), charSet);
	}
}
