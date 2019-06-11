package com.spier.common.utils;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

/**
 * 日期时间工具类
 * @author GHB
 * @version 1.0
 * @date 2018.12.29
 */
public class DateTimeUtil {

	/**
	 * 获得当前时间的时间日期格式化字符串
	 * @param pattern 格式化模式
	 * @return 可能为null
	 */
	public static String getCurrentDateTimeStr(String pattern) {
		if(StringUtils.isEmpty(pattern)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "pattern is empty, cannot format date-time string of current time.");
			return null;
		}
		
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(new Date());
	}
	
	/**
	 * 由字符串转换成Date对象
	 * @param str 字符串
	 * @param pattern 字符串的格式
	 * @return 可能为null
	 */
	public static Date getDateFromString(String str, String pattern) {
		if(StringUtils.isEmpty(str)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "str为空，无法解析date");
			return null;
		}
		
		if(StringUtils.isEmpty(pattern)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "pattern为空，无法解析date");
			return null;
		}
		
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		
		Date res = null;
		try {
			res = df.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	/**
	 * 查询某个时间点之前或者之后的一段时间的Date对象
	 * @param sourceDate 时间点
	 * @param interval 时间段，单位ms
	 * @param isBefore 是否是之前。true-之前；false-之后
	 * @return 时间对象，可能为null
	 */
	public static Date getDateBAInterval(Date sourceDate, int interval, boolean isBefore) {
		if(sourceDate == null) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "时间点为null，无法计算");
			return null;
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(sourceDate);
		if(isBefore) {
			c.add(Calendar.MILLISECOND, -interval);
		} else {
			c.add(Calendar.MILLISECOND, interval);
		}
		
		return c.getTime();
	}
	
	/**
	 * 把字符串转换为timestamp类型
	 * @param str s - timestamp in format yyyy-[m]m-[d]d hh:mm:ss[.f...]. The fractional seconds may be omitted. The leading zero for mm and dd may also be omitted.
	 * @return 可能为null
	 */
	public static Timestamp getTimestampFromStr(String str) {
		Timestamp res = null;
		
		try {
			res = Timestamp.valueOf(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	/**
	 * 在原有时间的基础上加若干天，得到新的时间。新的时间里的时分秒会被调整成timeStr所示的时间。如果此参数未提供，则默认调整为00:00:00
	 * @param raw 原有时间
	 * @param day 天数
	 * @param 时分秒
	 * @return 可能为null
	 */
	public static Timestamp addDate(Timestamp raw, long day, String timeStr) {
		if(null == raw) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "raw为null，无法计算时间！");
			return null;
		}
		
		String timeFormat = StringUtils.isEmpty(timeStr) ? "00:00:00" : timeStr;
		timeFormat = MessageFormat.format("yyyy-MM-dd {0}", timeFormat);
//		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("经调整后，时间格式为【{0}】", timeFormat));
		
		SimpleDateFormat df = new SimpleDateFormat(timeFormat);
		
		long newTime = raw.getTime() + 24 * 60 * 60 * 1000 * day;
		String resStr = df.format(new Date(newTime));
		
//		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("计算后的时间字符串为：【{0}】", resStr));
		
		return getTimestampFromStr(resStr);
	}
	
	/**
	 * 时间戳加分钟
	 * @param raw
	 * @param minute
	 * @return 可能为null
	 */
	public static Timestamp addMinute(Timestamp raw, long minute) {
		if(null == raw) {
			return null;
		}
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		long newTime = raw.getTime() + minute * 60 * 1000;
		String resStr = df.format(new Date(newTime));
		
		return getTimestampFromStr(resStr);
	}

	/**
	 * 时间转换成字符串格式
	 * @param date
	 * @return
	 */
	public static String formatDateToString(Date date){
		if (null == date) {
			throw new RuntimeException("参数非法, 时间不能为空");
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String resStr = df.format(date);
		return resStr;
	}
}
