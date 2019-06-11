package com.spier.mapper;

import org.apache.ibatis.annotations.Param;

import com.spier.common.bean.db.SimInfo;

/**
 * 手机卡信息dao层映射接口
 * @author GHB
 * @version 1.0
 * @date 2018.12.30
 */
public interface ISimCardInfoMapper {

	/**
	 * 新增手机卡信息。新增成功后，info中的mId会被设置
	 * @param info
	 */
	public void addSimInfo(@Param("tableName") String tabName, @Param("info") SimInfo info);
	
	/**
	 * 根据序列号删除手机卡信息
	 * @param ind
	 */
	public void deleteSimInfo(@Param("tableName") String tabName, @Param("ind") int ind);
	
	/**
	 * 根据信息记录索引查询手机卡信息
	 * @param ind 索引号
	 * @return 可能为null
	 */
	public SimInfo findSimInfoByInd(@Param("tableName") String tabName, @Param("ind") int ind);
	
	/**
	 * 根据手机号码查询记录索引号
	 * @param num 手机号码
	 * @return 存在：>0；不存在：<=0
	 */
	public Integer findSimInfoIndByNumber(@Param("tableName") String tabName, @Param("num") String num);
	
	/**
	 * 通过手机号码查找记录
	 * @param tabName
	 * @param num
	 * @return 可能为null
	 */
	public SimInfo findSimInfoByNumber(@Param("tableName") String tabName, @Param("num") String num);
	
	/**
	 * 根据索引号更新手机卡信息
	 * @param ind 索引号
	 * @param info 信息
	 */
	public void updateSimInfoByInd(@Param("tableName") String tabName, @Param("ind") int ind, @Param("info") SimInfo info);
}
