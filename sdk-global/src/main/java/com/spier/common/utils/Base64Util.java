package com.spier.common.utils;

import java.util.Base64;

/**
 * 普通base64工具类
 * @author GHB
 * @version 1.0
 * @date 2018.12.19
 */
public class Base64Util {

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
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	/**
	 * 获取base64编码字符串
	 * @param data 要编码的数据
	 * @return 可能为null
	 */
	public static String base64EncodeToStr(byte[] data) {
		byte[] resData = bas64Encode(data);
		
		return (resData == null ? null : new String(resData));
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
			res = Base64.getDecoder().decode(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
}
