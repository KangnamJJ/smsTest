package com.spier.mapper.task;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spier.common.bean.db.task.SyncTaskExecResult;

/**
 * 同步任务汇总情况表映射接口
 * @author GHB
 * @version 1.0
 * @date 2019.2.18
 */
public interface ISyncTaskStatisticsMapper {

	/**
	 * 根据用户信息更新或者插入
	 * @param tabName
	 * @param task
	 */
	public void insertOrUpdateAccording2User(@Param("tableName") String tabName, @Param("task") SyncTaskExecResult task);
	
	/**
	 * 获取所有的
	 * @param tabName
	 * @return 可能为null
	 */
	public List<SyncTaskExecResult> getAll(@Param("tableName") String tabName);
	
	/**
	 * 根据offerid批量删除
	 * @param tabName
	 * @param ids
	 */
	public void deleteByIdsBatch(@Param("tableName") String tabName, @Param("ids") List<String> ids);
	
	/**
	 * 根据offerid获取全部
	 * @param tabName
	 * @param offerId
	 * @return 可能为null
	 */
	public List<SyncTaskExecResult> getRecordsByOfferId(@Param("tableName") String tabName, @Param("id") String offerId);
	
	/**
	 * 根据用户信息获取全部
	 * @param tabName
	 * @param chanNo 渠道号
	 * @param usrName 用户名
	 * @return 可能为null
	 */
	public List<SyncTaskExecResult> getRecordsByUserInfo(@Param("tableName") String tabName, 
			@Param("chan") String chanNo, @Param("user") String usrName);
}
