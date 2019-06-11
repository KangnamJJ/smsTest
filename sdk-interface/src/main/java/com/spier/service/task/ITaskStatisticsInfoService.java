package com.spier.service.task;

import java.util.List;

import com.spier.common.bean.db.task.TaskExecResultInfo;
import com.spier.service.IAutoInitTable;

/**
 * 任务统计结果服务接口
 * @author GHB
 * @version 1.0
 * @date 2019.1.16
 */
public interface ITaskStatisticsInfoService extends IAutoInitTable {
	
	/**
	 * 获取用户做过的所有任务，包括成功的和失败的
	 * @param userId 用户id
	 * @return 不为null
	 */
	public List<TaskExecResultInfo> getTasksByUser(String userId);

	/**
	 * 根据用户id查询此用户做完的任务
	 * @param userId
	 * @return 不为null
	 */
	public List<TaskExecResultInfo> getUserFinishedTaskList(String userId);
	
	/**
	 * 检查用户是否有未完成的任务
	 * @param userId
	 * @return 
	 */
	public boolean doesUserHaveUnfinishedTask(String userId);
	
	/**
	 * 添加或者更新任务执行状态
	 * @param info 
	 * @return 操作结果
	 */
	public boolean addOrUpdateTaskExecStateRecord(TaskExecResultInfo info);
	
	/**
	 * 根据流水号查询任务执行信息
	 * @param fid
	 * @return 可能是null
	 */
	public TaskExecResultInfo getTaskFlowInfoByFlowId(String fid);
	
	/**
	 * 根据流水号查询任务记录是否存在
	 * @param fid
	 * @return
	 */
	public boolean checkTaskFlowExists(String fid);
	
	/**
	 * 通过flowId删除记录
	 * @param fid
	 */
	public void deleteTaskRecordByFlowId(String fid);
	
}
