package com.spier.mapper.job;

import java.util.List;

import com.spier.common.bean.db.task.TasksHistoryInfo;

/**
 * 创建时间 数据层
 * @author Timo
 * @date 2019-04-01
 */
public interface TasksHistoryInfoMapper {
	/**
     * 查询创建时间信息
     * 
     * @param id 创建时间ID
     * @return 创建时间信息
     */
	public TasksHistoryInfo selectTasksHistoryInfoById(Long id);
	
	/**
     * 查询创建时间列表
     * 
     * @param tasksHistoryInfo 创建时间信息
     * @return 创建时间集合
     */
	public List<TasksHistoryInfo> selectTasksHistoryInfoList(TasksHistoryInfo tasksHistoryInfo);
	
	/**
     * 新增创建时间
     * 
     * @param tasksHistoryInfo 创建时间信息
     * @return 结果
     */
	public int insertTasksHistoryInfo(TasksHistoryInfo tasksHistoryInfo);
	
	/**
     * 修改创建时间
     * 
     * @param tasksHistoryInfo 创建时间信息
     * @return 结果
     */
	public int updateTasksHistoryInfo(TasksHistoryInfo tasksHistoryInfo);
	
	/**
     * 删除创建时间
     * 
     * @param id 创建时间ID
     * @return 结果
     */
	public int deleteTasksHistoryInfoById(Long id);
	
	/**
     * 批量删除创建时间
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
	public int deleteTasksHistoryInfoByIds(String[] ids);
	
}