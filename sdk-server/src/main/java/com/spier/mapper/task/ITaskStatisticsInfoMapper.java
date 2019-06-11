/**
 * 
 */
package com.spier.mapper.task;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spier.common.bean.db.task.TaskExecResultInfo;

/**
 * 任务完成情况统计表数据库映射类
 * @author GHB
 * @version 1.0
 * @date 2019.1.16
 */
public interface ITaskStatisticsInfoMapper {

	/**
	 * 增加任务完成情况记录
	 * @param tabName
	 * @param info
	 */
	public void addStatisticsInfo(@Param("tableName") String tabName, @Param("info") TaskExecResultInfo info);
	
	/**
	 * 通过flowid升级
	 * @param tabName
	 * @param info
	 */
	public void updateStatisticsInfoByFlowId(@Param("tableName") String tabName, @Param("info") TaskExecResultInfo info);
	
	/**
	 * 通过序列号升级
	 * @param tabName
	 * @param info
	 */
	public void updateStatisticsInfoByIndex(@Param("tableName") String tabName, @Param("info") TaskExecResultInfo info);
	
	/**
	 * 通过flowid查找任务完成情况
	 * @param tabName
	 * @param flowId
	 * @return 可能为null
	 */
	public TaskExecResultInfo findRecordByFlowId(@Param("tableName") String tabName, @Param("fid") String flowId);
	
	/**
	 * 通过序列号查找任务完成情况
	 * @param tabName
	 * @param index
	 * @return 可能为null
	 */
	public TaskExecResultInfo findRecrodByIndex(@Param("tableName") String tabName, @Param("ind") int index);
	
	/**
	 * 查找用户的所有任务记录
	 * @param tabName
	 * @param userName
	 * @return 可能为null
	 */
	public List<TaskExecResultInfo> findRecordsByUser(@Param("tableName") String tabName, @Param("user") String userName);
	
	/**
	 * 通过flowid删除记录
	 * @param tabName
	 * @param flowId
	 */
	public void deleteRecordByFlowId(@Param("tableName") String tabName, @Param("fid") String flowId);
	
	/**
	 * 通过序列号删除记录
	 * @param tabName
	 * @param index
	 */
	public void deleteRecordByIndex(@Param("tableName") String tabName, @Param("ind") int index);
	
	/**
	 * 通过用户id查询其所有完成的任务
	 * @param tabName
	 * @param userId
	 * @return 可能为null
	 */
	public List<TaskExecResultInfo> getTaskListByUserIdAndState(@Param("tableName") String tabName, 
			@Param("uid") String userId, @Param("state") int state);
	
	/**
	 * 通过flowid查找记录的序列号
	 * @param tabName
	 * @param flowId
	 * @return >0为正常值
	 */
	public Integer getRecordIndexByFlowId(@Param("tableName") String tabName, @Param("fid") String flowId);
}
