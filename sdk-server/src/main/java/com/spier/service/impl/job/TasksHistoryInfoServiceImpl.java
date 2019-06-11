package com.spier.service.impl.job;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.spier.common.bean.db.task.TaskInfo;
import com.spier.common.bean.db.task.TasksHistoryInfo;
import com.spier.common.utils.Convert;
import com.spier.mapper.job.TasksHistoryInfoMapper;
import com.spier.service.job.ITasksHistoryInfoService;
import com.spier.service.task.ITaskInfoService;

/**
 * 创建时间 服务层实现
 * 
 * @author Timo
 * @date 2019-04-01
 */
@Service
public class TasksHistoryInfoServiceImpl implements ITasksHistoryInfoService 
{
	@Autowired
	private TasksHistoryInfoMapper tasksHistoryInfoMapper;

	@Autowired
	private ITaskInfoService iTaskInfoService;

	/**
     * 查询创建时间信息
     * 
     * @param id 创建时间ID
     * @return 创建时间信息
     */
    @Override
	public TasksHistoryInfo selectTasksHistoryInfoById(Long id)
	{
	    return tasksHistoryInfoMapper.selectTasksHistoryInfoById(id);
	}
	
	/**
     * 查询创建时间列表
     * 
     * @param tasksHistoryInfo 创建时间信息
     * @return 创建时间集合
     */
	@Override
	public List<TasksHistoryInfo> selectTasksHistoryInfoList(TasksHistoryInfo tasksHistoryInfo)
	{
	    return tasksHistoryInfoMapper.selectTasksHistoryInfoList(tasksHistoryInfo);
	}
	
    /**
     * 新增创建时间
     * 
     * @param tasksHistoryInfo 创建时间信息
     * @return 结果
     */
	@Override
	public int insertTasksHistoryInfo(TasksHistoryInfo tasksHistoryInfo, TaskInfo taskInfo)
	{
		if (taskInfo == null || tasksHistoryInfo == null) {
			throw new RuntimeException(String.format("参数不能为空, {tasksHistoryInfo=%s, taskInfo=%s}",
					tasksHistoryInfo, taskInfo));
		}
		String taskId = taskInfo.getTaskId();
		if (StringUtils.isEmpty(taskId)) {
			throw new RuntimeException("参数非法, taskId=" + taskId);
		}
		taskInfo.setTaskFinishedCount(0);

		iTaskInfoService.updateRecordById(taskInfo.getTaskId(), taskInfo);
		return tasksHistoryInfoMapper.insertTasksHistoryInfo(tasksHistoryInfo);

	}
	
	/**
     * 修改创建时间
     * 
     * @param tasksHistoryInfo 创建时间信息
     * @return 结果
     */
	@Override
	public int updateTasksHistoryInfo(TasksHistoryInfo tasksHistoryInfo)
	{
	    return tasksHistoryInfoMapper.updateTasksHistoryInfo(tasksHistoryInfo);
	}

	/**
     * 删除创建时间对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
	@Override
	public int deleteTasksHistoryInfoByIds(String ids)
	{
		return tasksHistoryInfoMapper.deleteTasksHistoryInfoByIds(Convert.toStrArray(ids));
	}
	
}
