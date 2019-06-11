package com.spier.common.utils;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 字符串工具类, 继承org.apache.commons.lang3.StringUtils类
 * @author ThinkGem
 * @version 2013-05-22
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
	
	private static final Logger LOGGER =  LoggerFactory.getLogger(StringUtils.class); 
	public static String lowerFirst(String str){
		if(StringUtils.isBlank(str)) {
			return "";
		} else {
			return str.substring(0,1).toLowerCase() + str.substring(1);
		}
	}
	
	public static String upperFirst(String str){
		if(StringUtils.isBlank(str)) {
			return "";
		} else {
			return str.substring(0,1).toUpperCase() + str.substring(1);
		}
	}

	/**
	 * 替换掉HTML标签方法
	 */
	public static String replaceHtml(String html) {
		if (isBlank(html)){
			return "";
		}
		String regEx = "<.+?>";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(html);
		String s = m.replaceAll("");
		return s;
	}

	/**
	 * 缩略字符串（不区分中英文字符）
	 * @param str 目标字符串
	 * @param length 截取长度
	 * @return
	 */
	public static String abbr(String str, int length) {
		if (str == null) {
			return "";
		}
		try {
			StringBuilder sb = new StringBuilder();
			int currentLength = 0;
			for (char c : replaceHtml(StringEscapeUtils.unescapeHtml4(str)).toCharArray()) {
				currentLength += String.valueOf(c).getBytes("GBK").length;
				if (currentLength <= length - 3) {
					sb.append(c);
				} else {
					sb.append("...");
					break;
				}
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Exception",e);
		}
		return "";
	}

	/**
	 * 缩略字符串（替换html）
	 * @param str 目标字符串
	 * @param length 截取长度
	 * @return
	 */
	public static String rabbr(String str, int length) {
        return abbr(replaceHtml(str), length);
	}
		
	
	/**
	 * 转换为Double类型
	 */
	public static Double toDouble(Object val){
		if (val == null){
			return 0D;
		}
		try {
			return Double.valueOf(trim(val.toString()));
		} catch (Exception e) {
			LOGGER.info(e.getMessage(),e);
			return 0D;
		}
	}

	/**
	 * 转换为Float类型
	 */
	public static Float toFloat(Object val){
		return toDouble(val).floatValue();
	}

	/**
	 * 转换为Long类型
	 */
	public static Long toLong(Object val){
		return toDouble(val).longValue();
	}

	/**
	 * 转换为Integer类型
	 */
	public static Integer toInteger(Object val){
		return toLong(val).intValue();
	}
	
//	/**
//	 * 获得i18n字符串
//	 */
//	public static String getMessage(String code, Object[] args) {
//		LocaleResolver localLocaleResolver = SpringContextHolder.getBean(LocaleResolver.class);
//		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();  
//		Locale localLocale = localLocaleResolver.resolveLocale(request);
//		return SpringContextHolder.getApplicationContext().getMessage(code, args, localLocale);
//	}
	
	/**
	 * 获得用户远程地址
	 */
	public static String getRemoteAddr(HttpServletRequest request){
		String remoteAddr = request.getHeader("X-Real-IP");
        if (isNotBlank(remoteAddr)) {
        	remoteAddr = request.getHeader("X-Forwarded-For");
        }
        if (isNotBlank(remoteAddr)) {
        	remoteAddr = request.getHeader("Proxy-Client-IP");
        }
        if (isNotBlank(remoteAddr)) {
        	remoteAddr = request.getHeader("WL-Proxy-Client-IP");
        }
        return remoteAddr != null ? remoteAddr : request.getRemoteAddr();
	}
	
	/**
	 * 去掉字符串中的控制字符
	 * 
	 * @param sourceString
	 *            源字符串
	 * @return 去掉控制字符后的字符串
	 */
	public static String removeCtrlChar(String sourceString) {
		String result = null;

		if (sourceString != null) {
			// The pattern matches control characters
			Pattern p = Pattern.compile("[\\t\\n\\x0B\\f\\r]");
			Matcher m = p.matcher("");
			m.reset(sourceString);
			// Replaces control characters with an empty string.
			result = m.replaceAll("");
		}

		return result;
	}

	public static boolean isNull(String str) {
		return isNullOrEmpty(str);
	}

	public static boolean isAllNull(String... ss) {
		for (String s : ss) {
			if (!isNull(s)) {
				return false;
			}
		}
		return true;
	}

	public static boolean hasNull(String... ss) {
		for (String s : ss) {
			if (isNull(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将字符串 s 用字符 c 从其左边填充至长度为 len
	 * 
	 * @param s
	 * @param len
	 * @param c
	 * @return
	 */
	public static String leftPad(String s, int len, char c) {
		if (s == null) {
			s = "";
		}
		if (len <= 0 || s.length() >= len) {
			return s;
		}

		int j = len - s.length();
		String pads = "";
		for (int k = 0; k < j; k++) {
			pads = pads + c;
		}
		pads = pads + s;

		return pads;
	}

	private static String getHexSeed(boolean isLowerCase) {
		String seed = null;
		if (isLowerCase) {
			seed = "0123456789abcdef";
		} else {
			seed = "0123456789ABCDEF";
		}

		return seed;
	}

	public static String byte2Hex(byte b, boolean isLowerCase) {
		String seed = getHexSeed(isLowerCase);
		return "" + seed.charAt(0xf & b >> 4) + seed.charAt(0xf & b);
	}

	public static byte hex2Byte(String str, boolean isLowerCase) {
		String seed = getHexSeed(isLowerCase);
		return (byte) (seed.indexOf(str.substring(0, 1)) * 16 + seed.indexOf(str.substring(1, 2)));
	}

	public static String bytes2HexString(byte[] bytes, boolean isLowerCase) {
		String result = "";
		for (int i = 0; i < bytes.length; i++) {
			result += byte2Hex(bytes[i], isLowerCase);
		}
		return result;
	}

	public static byte[] hexString2Bytes(String str, boolean isLowerCase) {
		byte[] b = new byte[str.length() / 2];
		for (int i = 0; i < b.length; i++) {
			String s = str.substring(i * 2, i * 2 + 2);
			b[i] = hex2Byte(s, isLowerCase);
		}
		return b;
	}

	public static boolean isLetter(char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
			return true;
		}
		return false;
	}

	public static boolean isLetterOrInt(char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' || c <= '9')) {
			return true;
		}
		return false;
	}

	public static boolean isPureLetter(String aStr) {
		if (aStr == null) {
			return false;
		}
		for (int index = 0; index < aStr.length(); index++) {
			char c = aStr.charAt(index);
			if (!isLetter(c)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isPureLetterOrInt(String aStr) {
		if (aStr == null) {
			return false;
		}
		for (int index = 0; index < aStr.length(); index++) {
			char c = aStr.charAt(index);
			if (!isLetterOrInt(c)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isPureInt(String aStr) {
		if (aStr == null) {
			return false;
		}
		for (int index = 0; index < aStr.length(); index++) {
			char c = aStr.charAt(index);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}

	public static boolean isTicketNo(String aStr) {
		if (aStr == null) {
			return false;
		}
		if (aStr.length() != 10 && aStr.length() != 13) {
			return false;
		}
		if (!isPureInt(aStr)) {
			return false;
		}
		return true;
	}

	public static boolean isPhoneNo(String aStr) {
		if (aStr == null) {
			return false;
		}
		if (aStr.length() != 11) {
			return false;
		}
		char firstChar = aStr.charAt(0);
		if (firstChar != '1') {
			return false;
		}
		if (!isPureInt(aStr)) {
			return false;
		}
		return true;
	}

	/**
	 * 判断是否是纯数字
	 *
	 * @param input
	 * @return
	 */

	public static boolean isPureNumber(String input) {
		if (isBlank(input)) {
			return false;
		}
		boolean result = input.matches("[0-9]+");
		if (!result) {
			return false;
		}
		return true;
	}

	/**
	 * 判断是否是字母+数字组合
	 */
	public static boolean isNumberAndLetter(String input){
		if (isBlank(input)) {
			return false;
		}
		boolean result = input.matches("^[A-Za-z0-9]+$");
		if (!result) {
			return false;
		}
		return true;
	}

	/**
	 * 判断12位纯数字是否满足如下条件：
	 后11位数字之和除以7余数为首位
	 */
	public static boolean isFirstDigit(String input){
		int summation = 0;
		//提取12位数字的首位数字
		char firstChar = input.charAt(0);
		int  firstNumber = firstChar - '0';
		//求后11位数字之和
		for (int i = 1; i < input.length(); i++) {
			char tempChar=input.charAt(i);
			int tempNumber=tempChar-'0';
			summation = summation + tempNumber;
		}
		/*
		 * 判断是否后11位数字之和除以7是否为首位数字
		 */
		if(summation%7==firstNumber){
			return true;
		}
		return false;
	}
	
	/**
	 * 校验手机号
	 * 手机号为13-18开头 后接9位数字
	 * @param aStr
	 * @return
	 */
	public static boolean isTelNo(String aStr){
		if (aStr == null) {
			return false;
		}
		String regEx = "^1[3-8]\\d{9}$";
		if (!aStr.matches(regEx)){
			return false;
		}
		return true;
	}
	
	/**
	 * 校验邮箱
	 * 字母、数字开头 可包含-_.等特殊字符 域名为2-5位的字母
	 * @param aStr
	 * @return
	 */
	public static boolean isEmailNo(String aStr){
		if (aStr == null) {
			return false;
		}
		String regEx = "^[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*\\@[a-zA-Z0-9]+\\.[a-zA-Z]{2,5}$";
		if (!aStr.matches(regEx)){
			return false;
		}
		return true;
	}

	public static boolean isAllLetter(String aStr, int min, int max) {
		if (aStr == null) {
			return false;
		}
		if (aStr.length() > max || aStr.length() < min) {
			return false;
		}
		if (!isPureLetter(aStr)) {
			return false;
		}
		return true;
	}

	public static boolean isAllLetterOrInt(String aStr, int min, int max) {
		if (aStr == null) {
			return false;
		}
		if (aStr.length() > max || aStr.length() < min) {
			return false;
		}
		if (!isPureLetterOrInt(aStr)) {
			return false;
		}
		return true;
	}

	public static String yyyy_MM_ddToyyMMdd(String aStr) {
		if (aStr == null || aStr.length() <= 0) {
			return null;
		}
		String[] strList = aStr.split("-");
		if (strList.length != 3 || strList[0].length() != 4) {
			return null;
		}
		return strList[0].substring(2) + strList[1] + strList[2];
	}

	public static String yyMMddToyyyy_MM_dd(String yy, String aStr) {
		if (aStr == null || aStr.length() != 6 || yy.length() != 2) {
			return null;
		}
		if (!isPureInt(aStr) || !isPureInt(yy)) {
			return null;
		}
		return yy + aStr.substring(0, 2) + "-" + aStr.substring(2, 4) + "-" + aStr.substring(4);
	}

	public static String HHmmToHH_mm(String aStr) {
		if (aStr == null || aStr.length() != 4) {
			return null;
		}
		return aStr.substring(0, 2) + ":" + aStr.substring(2);
	}
	
	 /**
	  * 判断字符串组合方式：纯中文、纯英文、中英文混合
	  * @param str 
	  * @return StringType 字符串枚举
	  */
    public static StringType checkStringLang(String str) {
        //去除特殊字符前
        String target = filterSpecialChar(str);
        String enRegEx = "^[0-9a-zA-Z]*";
        String zhRegEx = "^[0-9\u4e00-\u9fa5]*";
        boolean onlyEn = target.matches(enRegEx);
        boolean onlyZh = target.matches(zhRegEx);
        if (onlyZh) {
            return StringType.ZH;
        } else if (onlyEn) {
            return StringType.EN;
        } else {
            return StringType.ZH_EN;
        }
    }
    
    
    /**
     * 以特殊字符作为标志，切分字符串
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static String[] splitStringOnSpecialChar(String str) {
        String regEx = "[^0-9a-zA-Z\u4e00-\u9fa5]+";
        Pattern pattern = Pattern.compile(regEx);
        return pattern.split(str);
    }

    /***
     * 字符串数组组合为字符串
     *
     * @param strArray
     * @param isRevered
     *            顺序反转组合字符数组 true:从尾到头组合 false：从头到尾组合
     * @return
     */
    public static String buildStringSequence(String[] strArray, boolean isRevered) {
        if (strArray.length <= 0) {
            return "";
        }
        StringBuilder strBuilder = new StringBuilder();
        if (isRevered) {
            for (int i = strArray.length-1; i >= 0; i--) {
                strBuilder.append(strArray[i]);
            }
        } else {
            for (String str : strArray) {
                strBuilder.append(str);
            }
        }
        return strBuilder.toString();
    }

    /**
     * 去掉特殊字符 和空格
     * @param str
     * @return
     */
    public static String filterSpecialChar(String str){
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】'；：”“’。，、？\\s*|\\t|\\r|\\n]";
		Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
    
    /**
     * 在验证姓名前去掉姓名前的称谓
     * 特殊字符 [ MR/MS/MRS/MISS/MSTR/CHD/INF/CBBG/EXST/SD/JC/GM/(UM+数字)]
     * @param str
     * @return
     */
    public static String filerNameChar(String str){
    	String regEx = "\\ (M(R|S|RS|ISS|STR)|C(HD|BBG)|INF|EXST|SD|JC|GM|(UM\\+[0-9]+))?$";
    	Pattern p = Pattern.compile(regEx);
    	Matcher m = p.matcher(str);
    	return m.replaceAll("").trim();
    }

    /***
     * 字符串组合枚举
     */
    public enum StringType {
        /** 英文 */
        EN,
        /** 中文 */
        ZH,
        /** 中英混合 */
        ZH_EN;
    }
	public static String replaceAllIgnoreCase(String input, String regex, String replacement) {  
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
        Matcher m = p.matcher(input);  
        return m.replaceAll(replacement);  
    } 
	/*打印入参和url
	 * 
	 */
	public static void printRequestParam(HttpServletRequest request) {
		Object body = request.getAttribute("requestBody");
		if (body != null) {
			String param = body.toString();
			LOGGER.error("url: {}, input: {}", request.getRequestURL(), param);
		}

	}
	
	public static String  requestParam(HttpServletRequest request) {
		Object body = request.getAttribute("requestBody");
		if (body != null) {
			String param = body.toString();
			 return param;
		}
       return "";
	}

	/**
	 * 获取字符串中两个字符串中间隔的字符串
	 * ex：<a>bbbb<a/> 处理后得到bbbb
	 * @param aStr 需要处理的字符串
	 * @param str1 前字符串
	 * @param str2 后字符串
	 * @return
	 */
	public static String subStringBetweenStr1AndStr2(String aStr, String str1, String str2) {
		int fromInt = aStr.indexOf(str1);
		int toInt = aStr.indexOf(str2);
		if (fromInt >= 0 && toInt >= 0) {
			return aStr.substring(fromInt + str1.length(), toInt);
		} else {
			return null;
		}
	}

	/**
	 * 隔开的字符串转成map
	 * 如：1,2,3,4 转成map
	 * @return
	 */
	public static Map<String, Object> splitStringToMap(String string,String regex) {
		Map<String, Object> resultMap = new HashMap<>(10);
		if (StringUtils.isNotBlank(string)) {
			if (string.endsWith(regex)) {
				string = string.substring(0, string.length() - 1);
			}

			String[] strs;
			if (string.contains(regex)) {
				strs = string.split(regex);
			} else {
				strs = new String[]{string};
			}
			for (String str : strs) {
				resultMap.put(str, null);
			}
		}
		return resultMap;
	}

	/**
	 * 隔开的字符串转成set
	 * 如：1,2,3,4 转成set
	 * @return
	 */
	public static Set<String> splitStringToSet(String string,String regex) {

		Set<String> set = new HashSet<>(10);
		if (StringUtils.isNotBlank(string)) {
			if (string.endsWith(regex)) {
				string = string.substring(0, string.length() - 1);
			}

			String[] strs;
			if (string.contains(regex)) {
				strs = string.split(regex);
			} else {
				strs = new String[]{string};
			}
			set = new HashSet<>(Arrays.asList(strs));
		}
		return set;
	}

	/**
	 * 隔开的字符串转成set
	 * 如：1,2,3,4 转成set
	 */
	public static List<String> splitStringToList(String string,String regex) {

		List<String> list = new ArrayList<>(10);
		if (StringUtils.isNotBlank(string)) {
			if (string.endsWith(regex)) {
				string = string.substring(0, string.length() - 1);
			}

			String[] strs;
			if (string.contains(regex)) {
				strs = string.split(regex);
			} else {
				strs = new String[]{string};
			}
			list = new ArrayList<>(Arrays.asList(strs));
		}
		return list;
	}

	public static String parseSetToStringByChar(Set<String> sets, String s) {
		StringBuilder builder = new StringBuilder();
		if (null != sets && !sets.isEmpty() && StringUtils.isNotBlank(s)) {
			for (String string : sets) {
				if (StringUtils.isBlank(builder.toString())) {
					builder.append(string);
				}else {
					builder.append(s).append(string);
				}
			}

		}
		return builder.toString();
	}
	/**
	 * 
	 *字符串拼接
	 *
	 */
	public static String appendStrings(String root,String patten,String ...args){
		StringBuilder sbBuilder=new StringBuilder(root);
		for(int i=0;i<args.length;i++){
			sbBuilder.append(args[i]);
			if(isNotBlank(patten)&&i!=args.length-1){
				sbBuilder.append(patten);
			}
		}
		return sbBuilder.toString();
	}

	/**
	 *
	 *字符串拼接
	 *
	 */
	public static String appendStrings(String root,String patten,List<String> args){
		StringBuilder sbBuilder=new StringBuilder(root);
		for(int i=0;i<args.size();i++){
			sbBuilder.append(args.get(i));
			if(isNotBlank(patten)&&i!=args.size()-1){
				sbBuilder.append(patten);
			}
		}
		return sbBuilder.toString();
	}

	public static StringBuilder appendStr(StringBuilder sb, Object... args) {
		if (args.length > 0) {
			for (Object arg : args) {
				sb.append(arg);
			}
		}
		return sb;
	}
}
