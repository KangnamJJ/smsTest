package com.spier.common.utils;

import org.apache.commons.lang3.StringUtils;

public class HexUtil {
	/**
	 * 16进制转换成字符串
	 * @param byteArr
	 * @return 可能为null
	 */
	public static String hex2Str(byte[] byteArr) {
        if (null == byteArr || byteArr.length < 1) {
        	return null;
        }
        
        StringBuilder sb = new StringBuilder();
        for (byte t : byteArr) {
            if ((t & 0xF0) == 0) {
            	sb.append("0");
            }
            
          //t & 0xFF 操作是为去除Integer高位多余的符号位（java数据是用补码表示）
            sb.append(Integer.toHexString(t & 0xFF));  
        }
        
        return sb.toString();
    }

	/**
	 * 由字符串转换回hex
	 * @param hexStr
	 * @return 可能为null
	 */
	public static byte[] hexFromStr(String hexStr) {
        if (StringUtils.isEmpty(hexStr)) {
        	return null;
        }

        int byteLen = hexStr.length() / 2;
        byte[] result = new byte[byteLen];
        char[] hexChar = hexStr.toCharArray();
        for(int i = 0; i < byteLen; i++) {
           result[i] = (byte)(Character.digit(
        		   hexChar[i * 2], 16) << 4 | Character.digit(hexChar[i * 2 + 1], 16));
        }

        return result;
    }
}
