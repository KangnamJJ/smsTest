package com.spier.mapper.task;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spier.common.bean.db.task.SyncTaskInfo;

/**
 * 同步任务信息DB映射接口
 * @author GHB
 * @version 1.0
 * @date 2019.2.18
 */
public interface ISyncTaskInfoMapper {

	/**
	 * 升级或更新
	 * @param tabName
	 * @param tasks
	 */
	public void updateOrInsert(@Param("tableName") String tabName, @Param("tasks") List<SyncTaskInfo> tasks);
	
	/**
	 * 根据offerid获取任务信息
	 * @param tabName
	 * @param id
	 * @return 可能为null
	 */
	public SyncTaskInfo getTaskByOfferId(@Param("tableName") String tabName, @Param("offerId") String id);
	
	/**
	 * 获取所有的
	 * @param tabName
	 * @return 可能为null
	 */
	public List<SyncTaskInfo> getAll(@Param("tableName") String tabName);
	
	/**
	 * 批量删除
	 * @param tabName
	 * @param ids
	 */
	public void deleteByIdsBatch(@Param("tableName") String tabName, @Param("ids") List<Integer> ids);
}
