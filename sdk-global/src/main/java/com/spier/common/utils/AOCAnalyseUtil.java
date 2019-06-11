package com.spier.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * AOC解析工具类
 * @author GHB
 * @version 1.0
 * @date 2019.4.12
 */
public class AOCAnalyseUtil {

	// 之所以多一个字符，是为了找出02-965-7087情况，正确识别出取出的7087不是独立的号码。
	private static final String M_PATTERN_4_NUMS = ".\\d{4,}";
	
	/**
	 * 解析AOC
	 * @param content
	 * @return 不为null
	 */
	public static List<String> analyseAOCContent(String content) {
		List<String> res = new ArrayList<String>();
		
		if(StringUtils.isEmpty(content)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "content为空，无法解析！");
			return res;
		}
		
		Pattern p = Pattern.compile(M_PATTERN_4_NUMS);
		Matcher m = p.matcher(content);
		
		while(m.find()) {
			String str = m.group(0);
			if(StringUtils.isEmpty(str)) {
				continue;
			}
			
			if(str.startsWith("-")) {
				continue;
			}
			
			res.add(str.substring(1));
		}
		
		return res;
	}
}
