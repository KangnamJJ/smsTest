package com.spier.common.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;

/**
 * 带有安全URL功能的Base64工具
 * @author GHB
 * @version 1.0
 * @date 2018.12.19
 */
public class URLSafeBase64Utils {
	/**
	 * base64编码
	 * @param data 要编码的数据
	 * @return 编码后的数据，可能为null
	 */
	public static byte[] bas64Encode(byte[] data) {
		if(null == data) {
			return null;
		}
		
		byte[] res = null;
		
		try {
			res = Base64.getEncoder().encode(data);
			res = URLEncoder.encode(new String(res), "UTF-8").getBytes();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	/**
	 * base64编码为字符串
	 * @param data 要编码的数据
	 * @return 编码后的结果，可能为null
	 */
	public static String base64EncodeToString(byte[] data) {
		if(null == data) {
			return null;
		}
		
		byte[] encData = bas64Encode(data);
		
		return (encData == null ? null : new String(encData));
//		return Base64.getEncoder().encodeToString(data);
	}
	
	/**
	 * base64解码
	 * @param data
	 * @return 可能为null
	 */
	public static byte[] base64Decode(byte[] data) {
		if(null == data) {
			return null;
		}
		
		byte[] res = null;
		
		try {
			res = URLDecoder.decode(new String(data), "UTF-8").getBytes();
			res = Base64.getDecoder().decode(res);
//			res = Base64.getDecoder().decode(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
}
