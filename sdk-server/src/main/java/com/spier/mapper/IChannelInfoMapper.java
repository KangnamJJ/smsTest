package com.spier.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spier.common.bean.db.ChannelInfo;

public interface IChannelInfoMapper {
	
	/**
	 * @param tabName 表名称
	 * 获取所有的渠道信息
	 * @return
	 */
	public List<ChannelInfo> getAll(@Param("tableName") String tabName);
	
	/**
	 * 根据渠道号查询渠道信息
	 * @param tabName 表名称
	 * @param chanNo 渠道号
	 * @return 可能为null
	 */
	public ChannelInfo getChanInfoByChanNo(@Param("tableName") String tabName, @Param("chanNo")String chanNo);
	
	/**
	 * 根据索引号获取渠道信息
	 * @param ind 索引号
	 * @return 可能为null
	 */
	public ChannelInfo getChanInfoByIndex(@Param("tableName") String tabName, @Param("ind") int ind);
	
	/**
	 * 根据渠道号查询RSA私钥
	 * @param chanNo 渠道号
	 * @return
	 */
	public String getRSAPrivKStrByChanNo(@Param("tableName") String tabName, @Param("chanNo") String chanNo);
	
	/**
	 * 插入渠道信息
	 * @param info 渠道信息
	 * @return 操作结果
	 */
	public void insertChanInfo(@Param("tableName") String tabName, @Param("chInfo")ChannelInfo info);
	
	/**
	 * 根据渠道号更新条目
	 * @param tabName
	 * @param chNo
	 * @param info
	 */
	public void updateChanInfoByChanNo(@Param("tableName") String tabName, 
			@Param("chanNo") String chNo, @Param("chInfo") ChannelInfo info);
	
	/**
	 * 根据序号删除记录
	 * @param tabName 表名
	 * @param ind 序列号
	 */
	public void deleteByIndex(@Param("tableName") String tabName, @Param("ind") int ind);
	
	/**
	 * 根据渠道号删除记录
	 * @param tabName
	 * @param chanNo
	 */
	public void deleteByChNo(@Param("tableName") String tabName, @Param("chNo") String chanNo);
	
	/**
	 * 获取渠道的开关状态
	 * @param tabName
	 * @param chanNo
	 * @return 可能为null
	 */
	public Integer getChanTaskSwitchState(@Param("tableName") String tabName, @Param("chNo") String chanNo);
}
