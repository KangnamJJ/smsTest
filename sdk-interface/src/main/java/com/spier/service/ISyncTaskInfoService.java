package com.spier.service;

import java.util.List;

import com.spier.common.bean.db.task.SyncTaskInfo;

/**
 * 同步任务信息服务接口，区别于自建任务
 * @author GHB
 * @version 1.0
 * @date 2019.2.18
 */
public interface ISyncTaskInfoService extends IAutoInitTable {

	/**
	 * 插入或升级任务
	 * @param tasks
	 * @return 操作是否成功
	 */
	public boolean updateOrInsert(List<SyncTaskInfo> tasks);
	
	/**
	 * 插入或更新单个任务
	 * @param task
	 * @return 操作结果
	 */
	public boolean updateOrInsert(SyncTaskInfo task);
	
	/**
	 * 获取所有的任务
	 * @return 不为null
	 */
	public List<SyncTaskInfo> getAll();
	
	/**
	 * 根据offerId获取任务
	 * @param id
	 * @return 可能为null
	 */
	public SyncTaskInfo getTaskByOfferId(String id);
	
	/**
	 * 根据offerId批量删除
	 * @param ids
	 * @return 操作结果
	 */
	public boolean deleteByIdsBatch(List<Integer> ids);
}
