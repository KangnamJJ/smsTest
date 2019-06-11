package com.spier.service.task;

import java.util.List;

import com.spier.common.bean.db.task.TaskInfo;
import com.spier.service.IAutoInitTable;

/**
 * 任务信息服务接口
 * @author GHB
 * @version 1.0
 * @date 2019.1.8
 */
public interface ITaskInfoService extends IAutoInitTable {

	/**
	 * 新增任务
	 * @param info
	 * @return 操作是否成功
	 */
	public boolean addRecord(TaskInfo info);
	
	/**
	 * 根据记录索引号删除记录
	 * @param ind
	 */
	public void deleteRecordByIndex(int ind);
	
	/**
	 * 根据索引号查询记录
	 * @param ind 索引号
	 * @return 可能为null
	 */
	public TaskInfo findRecordByIndex(int ind);
	
	/**
	 * 根据任务id查询记录
	 * @param id 任务id
	 * @return 可能为null
	 */
	public TaskInfo findRecordByTaskId(String id);
	
	/**
	 * 根据任务id集合批量获取任务信息
	 * @param ids
	 * @return 不为null
	 */
	public List<TaskInfo> getTaskByIds(List<String> ids);
	
	/**
	 * 根据序列号更新任务信息
	 * @param ind 序列号
	 * @param info 任务信息
	 * @return 操作是否成功
	 */
	public boolean updateRecordByIndex(int ind, TaskInfo info);
	
	/**
	 * 根据任务id更新任务信息
	 * @param id 任务id
	 * @param info
	 * @return 操作是否成功
	 */
	public boolean updateRecordById(String id, TaskInfo info);
	
	/**
	 * 根据国家、运营商、网络类型获取任务列表
	 * @param country
	 * @param op
	 * @param netEnv
	 * @return 不为null
	 */
	public List<TaskInfo> getTaskListByKeyInfo(String country, String op, int netEnv);
	
	/**
	 * 记录任务完成数
	 * @param taskId
	 * @param count
	 * @return 操作结果
	 */
	public boolean recordTaskFinishedCount(String taskId, int count);
	
	/**
	 * 获取所有任务
	 * @return 不为null
	 */
	public List<TaskInfo> getAllTasks();
	
	/**
	 * 根据任务类型获取所有任务
	 * @param type
	 * @return 不为null
	 */
	public List<TaskInfo> getTasksByTaskType(int type);
}
