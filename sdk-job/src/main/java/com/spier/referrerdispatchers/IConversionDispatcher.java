package com.spier.referrerdispatchers;

import java.util.Map;

import com.spier.common.bean.db.referrer.ReferrerInfo;

/**
 * 转化分发器，每个渠道对应一个
 * @author GHB
 * @version 1.0
 * @date 2019.3.8
 */
public interface IConversionDispatcher {

	/**
	 * 获取渠道。这也是分发器的唯一标识
	 * @return 不可为null
	 */
	public String getChan();
	
	/**
	 * 设置渠道号
	 * @param chanNo
	 */
	public void setChan(String chanNo);
	
	/**
	 * 设置postback
	 * @param postback
	 */
	public void setPostback(String postback);
	
	/**
	 * 获取postback
	 * @return 可能为null
	 */
	public String getPostback();
	
	/**
	 * 分发前调用的接口，用于组织要替换的数据
	 * @param referrerInfo
	 * @return 操作结果。如果为false，则不再继续分发流程
	 */
	public boolean beforeDispatching(ReferrerInfo referrerInfo);
	
	/**
	 * 分发接口
	 * @param conv
	 * @return 操作结果
	 */
	public boolean dispatch(ReferrerInfo conv);
	
	/**
	 * 获取转化id要替换的部分
	 * @return 不为null
	 */
	public String getConversionIdReplacePart();
	
	/**
	 * 获取其他要替换的部分以及对应的值
	 * @return 不能为null
	 */
	public Map<String, String> getReplacement();
	
	/**
	 * 添加替换部分与替换值
	 * @param replacePart 要替换的部分
	 * @param replacement 要替换的值
	 */
	public void addReplacement(String replacePart, String replacement);
}
