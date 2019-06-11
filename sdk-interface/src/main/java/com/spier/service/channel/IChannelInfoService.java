package com.spier.service.channel;

import java.util.List;

import com.spier.common.bean.db.ChannelInfo;
import com.spier.service.IAutoInitTable;

/**
 * 渠道信息的mapper
 * @author GHB
 * @version 1.0
 * @date 2018.12.20
 */
public interface IChannelInfoService extends IAutoInitTable {

	/**
	 * 取出所有的渠道数据
	 * @return 不为null
	 */
	public List<ChannelInfo> getAll();
	
	/**
	 * 根据渠道号获取渠道信息
	 * @param chanNo 渠道号
	 * @return 可能为null
	 */
	public ChannelInfo getChanInfoByChanNo(String chanNo);
	
	/**
	 * 根据索引号获取渠道信息
	 * @param ind
	 * @return 可能为null
	 */
	public ChannelInfo getChanInfoByIndex(int ind);
	
	/**
	 * 根据渠道号获取其RSA私钥Base64编码后的字符串
	 * @param chanNo 渠道号
	 * @return 可能为null
	 */
	public String getRSAPrivKB64StrByChanNo(String chanNo);
	
	/**
	 * 将渠道信息插入数据库
	 * @param info 渠道信息
	 * @return 操作是否成功
	 */
	public boolean insertChanInfo(ChannelInfo info);
	
	/**
	 * 根据渠道号更新渠道I型南溪
	 * @param chNo
	 * @param info
	 * @return 操作结果
	 */
	public boolean updateChanInfoByChanNo(String chNo, ChannelInfo info);
	
	/**
	 * 根据序列号删除记录
	 * @param ind
	 * @return 操作结果
	 */
	public boolean deleteByIndex(int ind);
	
	/**
	 * 根据渠道号删除记录
	 * @param chanNo
	 * @return 操作结果
	 */
	public boolean deleteByChanNo(String chanNo);
	
	/**
	 * 检查某个渠道的任务开关是否打开
	 * @param chanNo
	 * @return
	 */
	public boolean isTaskSwitchOn(String chanNo);
}
