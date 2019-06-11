package com.spier.mapper.task;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spier.common.bean.db.task.TaskInfo;

/**
 * 任务信息mapper
 * @author GHB
 * @version 1.0
 * @date 2019.1.8
 */
public interface ITaskInfoMapper {

	/**
	 * 添加记录
	 * @param info
	 */
	public void addTask(@Param("tableName") String tabName, @Param("info") TaskInfo info);
	
	/**
	 * 根据序列号删除任务信息
	 * @param ind
	 */
	public void deleteTaskByIndex(@Param("tableName") String tabName, @Param("ind") int ind);
	
	/**
	 * 根据序列号查找对应额记录
	 * @param ind
	 */
	public TaskInfo findRecordByIndex(@Param("tableName") String tabName, @Param("ind") int ind);
	
	/**
	 * 根据任务id查找对应的记录
	 * @param taskId
	 * @return 可能为为null
	 */
	public TaskInfo findRecordById(@Param("tableName") String tabName, @Param("id") String taskId);
	
	/**
	 * 根据索引号更新记录
	 * @param ind 索引号
	 * @param info 记录信息
	 */
	public void updateRecordByInd(@Param("tableName") String tabName, @Param("ind") int ind, @Param("info") TaskInfo info);
	
	/**
	 * 根据任务id更新记录
	 * @param taskId 任务id
	 * @param info 
	 */
	public void updateRecordByTaskId(@Param("tableName") String tabName, @Param("id") String taskId, @Param("info") TaskInfo info);
	
	/**
	 * 获取所有记录
	 * @return 可能为null
	 */
	public List<TaskInfo> getAllRecords(@Param("tableName") String tabName);
	
	/**
	 * 根据记录类型获取所有记录
	 * @param tabName 表名
	 * @param type 记录类型
	 * @return 可能为null
	 */
	public List<TaskInfo> getTasksByTaskType(@Param("tableName") String tabName, @Param("type") int type);
	
	/**
	 * 更新某条记录的完成数
	 * @param tabName
	 * @param taskId
	 * @param count
	 */
	public void updateTaskFinishedCount(@Param("tableName") String tabName, @Param("id") String taskId, @Param("finish") int count);
	
	/**
	 * 根据脚本id查询所有任务
	 * @param tabName
	 * @param scriptIds
	 * @return 可能为null
	 */
	public List<TaskInfo> findTaskListByScriptIds(@Param("tableName") String tabName, @Param("scriptIds") List<String> scriptIds);
}
