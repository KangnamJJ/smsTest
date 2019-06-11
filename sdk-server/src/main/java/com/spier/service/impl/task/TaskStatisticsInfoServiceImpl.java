package com.spier.service.impl.task;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.spier.common.bean.db.task.TaskExecResultInfo;
import com.spier.common.config.GlobalConfig;
import com.spier.mapper.task.ITaskStatisticsInfoMapper;
import com.spier.service.task.ITaskStatisticsInfoService;

/**
 * 任务统计完成情况统计表服务实现类
 * @author GHB
 * @version 1.0
 * @date 2019.1.17
 */
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class TaskStatisticsInfoServiceImpl implements ITaskStatisticsInfoService {

	@Autowired
	private ITaskStatisticsInfoMapper mMapper;
	
	@Override
	public List<TaskExecResultInfo> getUserFinishedTaskList(String userId) {
		List<TaskExecResultInfo> res = new ArrayList<TaskExecResultInfo>();
		if(StringUtils.isEmpty(userId)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "用户id为空，无法查询任务完成列表");
			return res;
		}
		
		res = mMapper.getTaskListByUserIdAndState(getTableName(), userId, GlobalConfig.M_TASK_RUNNING_STATE_SUCCEED);
		if(res == null) {
			res = new ArrayList<TaskExecResultInfo>();
		}
		
		return res;
	}
	
	private List<TaskExecResultInfo> getUserUnFinishedTaskList(String userId) {
		List<TaskExecResultInfo> res = new ArrayList<TaskExecResultInfo>();
		if(StringUtils.isEmpty(userId)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "用户id为空，无法查询任务未完成列表");
			return res;
		}
		
		res = mMapper.getTaskListByUserIdAndState(getTableName(), userId, GlobalConfig.M_TASK_RUNNING_STATE_RUNNING);
		if(res == null) {
			res = new ArrayList<TaskExecResultInfo>();
		}
		
		return res;
	}

	@Override
	public boolean doesUserHaveUnfinishedTask(String userId) {
		return !getUserUnFinishedTaskList(userId).isEmpty();
	}

	@Override
	public boolean addOrUpdateTaskExecStateRecord(TaskExecResultInfo info) {
		if(null == info) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "info为null，无法新增或更新任务完成情况！");
			return false;
		}
		
		boolean res = false;
		
		if(checkTaskFlowExists(info.getTaskFlowId())) {
			mMapper.updateStatisticsInfoByFlowId(getTableName(), info);
			res = true;
		} else {
			mMapper.addStatisticsInfo(getTableName(), info);
			res = info.getIndex() > 0;
		}
		
		return res;
	}

	@Override
	public TaskExecResultInfo getTaskFlowInfoByFlowId(String fid) {
		if(StringUtils.isEmpty(fid)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "flowId为空，无法查询任务完成情况记录！");
			return null;
		}
		
		return mMapper.findRecordByFlowId(getTableName(), fid);
	}
	
	@Override
	public boolean checkTaskFlowExists(String fid) {
		if(StringUtils.isEmpty(fid)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "flowId为空，无法查询记录是否存在！");
			return false;
		}
		
		Integer res = mMapper.getRecordIndexByFlowId(getTableName(), fid);
		if(res == null) {
			return false;
		}
		
		return res > 0;
	}
	
	@Override
	public void deleteTaskRecordByFlowId(String fid) {
		if(StringUtils.isEmpty(fid)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "fid为空，无法删除记录！");
			return;
		}
		
		mMapper.deleteRecordByFlowId(getTableName(), fid);
	}
	
	@Override
	public List<TaskExecResultInfo> getTasksByUser(String userId) {
		List<TaskExecResultInfo> res = new ArrayList<TaskExecResultInfo>();
		
		if(StringUtils.isEmpty(userId)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "userId为空，无法查询用户任务记录！");
			return res;
		}
		
		res = mMapper.findRecordsByUser(getTableName(), userId);
		if(null == res) {
			res = new ArrayList<TaskExecResultInfo>();
		}
		
		return res;
	}

	private static final String M_TAB_NAME = "task_statistics_info";
	
	@Override
	public String getTableName() {
		return M_TAB_NAME;
	}

	private static final String M_SQL_TAB_CREATE = 
			"_id bigint(20) NOT NULL AUTO_INCREMENT, " +
			"task_flow_id varchar(64) NOT NULL, " +
			"task_id varchar(64) NOT NULL, " +
	        "chan_no varchar(64) NOT NULL, " +
	        "app_name varchar(64) NOT NULL, " +
	        "user_id varchar(64) NOT NULL, " +
	        "phone_num varchar(20), " +
	        "click_url varchar(255), " +
	        "task_state int(5), " +
	        "change_time timestamp NOT NULL, " +
	        "PRIMARY KEY (_id)";
	
	@Override
	public String getCreateTableSql() {
		return M_SQL_TAB_CREATE;
	}
}
