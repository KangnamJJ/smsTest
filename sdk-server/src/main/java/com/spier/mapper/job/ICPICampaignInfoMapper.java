package com.spier.mapper.job;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spier.common.bean.db.CPICampaignInfo;

/**
 * CPI Campaign信息db映射类
 * @author GHB
 * @version 1.0
 * @date 2019.2.14
 */
public interface ICPICampaignInfoMapper {
	/**
	 * 批量插入或更新
	 * @param tabName
	 * @param list
	 */
	public void addOrUpdateBatch(@Param("tableName") String tabName, @Param("list") List<CPICampaignInfo> list);
	
	/**
	 * 根据id批量删除
	 * @param tabName
	 * @param ids offerid
	 */
	public void deleteByIdsBatch(@Param("tableName") String tabName, @Param("ids") List<String> ids);
	
	/**
	 * 获取所有的
	 * @param tabName
	 * @return 可能为null
	 */
	public List<CPICampaignInfo> getAll(@Param("tableName") String tabName);
}
