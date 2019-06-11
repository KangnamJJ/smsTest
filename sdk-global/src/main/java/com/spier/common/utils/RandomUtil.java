package com.spier.common.utils;

import java.util.Random;

public class RandomUtil {

	private static Random mRandom = new Random();
	
	private static String mCharset = 
			"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	/**
	 * 随机生成一个长度为length的字符串
	 * @param length
	 * @return 不为null，length小于等于0时为空字符串
	 */
	synchronized public static String getRandomStr(int length) {
		if(length <= 0) {
			return "";
		}
		
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < length; ++i) {
			int ind = mRandom.nextInt(mCharset.length());
			builder.append(mCharset.charAt(ind));
		}
		
		return builder.toString();
	}
}
