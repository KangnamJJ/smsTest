package com.spier.service.task;

import java.util.List;

import com.spier.common.bean.db.task.SyncTaskExecResult;
import com.spier.service.IAutoInitTable;

/**
 * 同步任务统计接口
 * @author GHB
 * @version 1.0
 * @Date 2019.2.18
 */
public interface ISyncTaskStatisticsService extends IAutoInitTable {

	/**
	 * 插入或更新同步任务执行结果
	 * @param info
	 * @return 操作是否成功
	 */
	public boolean insertOrUpdate(SyncTaskExecResult info);
	
	/**
	 * 获取所有的
	 * @return 不为null
	 */
	public List<SyncTaskExecResult> getAll();
	
	/**
	 * 根据offerid批量删除
	 * @param ids
	 * @return 操作结果
	 */
	public boolean deleteBatch(List<String> ids);
	
	/**
	 * 根据offerid获取所有记录
	 * @param id
	 * @return 不为null
	 */
	public List<SyncTaskExecResult> getAllByOfferId(String offerId);
	
	/**
	 * 根据用户信息查询所有记录
	 * @param userId
	 * @param chanNo
	 * @param pkgName
	 * @return 不为null
	 */
	public List<SyncTaskExecResult> getAllByUser(String userId, String chanNo);
}
