package com.spier.referrerdispatchers;

public class ReferrerConfig {

	/**
	 * 转化回调失败：{@value}
	 */
	public static final int M_NOTIFIED_FAILED_VALUE = 2;
	/**
	 * 转化回调成功：{@value}
	 */
	public static final int M_NOTIFIED_SUCCEED_VALUE = 1;
	/**
	 * 转化还未处理：{@value}
	 */
	public static final int M_UNNOTIFIED_VALUE = 0;
	
	/**
	 * conversion_id字段名称：{@value}
	 */
	public static final String M_PARAM_NAME_CONVERSION_ID = "conversion_id";
	/**
	 * 渠道号字段名称：{@value}
	 */
	public static final String M_PARAM_NAME_CHAN = "utm_campaign";
	
	/**
	 * hexmobile的渠道号：{@value}
	 */
	public static final String M_CHAN_NO_HEXMOBILE = "20190118152234";
}
