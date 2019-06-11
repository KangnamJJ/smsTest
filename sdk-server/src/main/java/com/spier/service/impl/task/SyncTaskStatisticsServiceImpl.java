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
import com.spier.common.bean.db.task.SyncTaskExecResult;
import com.spier.mapper.task.ISyncTaskStatisticsMapper;
import com.spier.service.task.ISyncTaskStatisticsService;

@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class SyncTaskStatisticsServiceImpl implements ISyncTaskStatisticsService {

	@Autowired
	private ISyncTaskStatisticsMapper mMapper;
	
	@Override
	public boolean insertOrUpdate(SyncTaskExecResult info) {
		if(null == info) {
			return false;
		}
		
		// 根据用户名、包名、渠道号确定新增还是更新
		mMapper.insertOrUpdateAccording2User(getTableName(), info);
		
		return true;
	}

	@Override
	public List<SyncTaskExecResult> getAll() {
		List<SyncTaskExecResult> res = mMapper.getAll(getTableName());
		if(res == null) {
			res = new ArrayList<SyncTaskExecResult>();
		}
		
		return res;
	}

	@Override
	public boolean deleteBatch(List<String> ids) {
		// TODO Auto-generated method stub
		if(ids == null) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "ids为null，无法批量删除！");
			return false;
		}
		
		if(ids.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.WARNING, "ids为空，无需批量删除！");
			return false;
		}
		
		mMapper.deleteByIdsBatch(getTableName(), ids);
		
		return true;
	}

	@Override
	public List<SyncTaskExecResult> getAllByOfferId(String offerId) {
		List<SyncTaskExecResult> res = new ArrayList<SyncTaskExecResult>();
		
		if(StringUtils.isEmpty(offerId)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "offerId为空，无法批量查询！");
			return res;
		}
		
		res = mMapper.getRecordsByOfferId(getTableName(), offerId);
		if(null == res) {
			res = new ArrayList<SyncTaskExecResult>();
		}
		
		return res;
	}

	@Override
	public List<SyncTaskExecResult> getAllByUser(String userId, String chanNo) {
		List<SyncTaskExecResult> res = new ArrayList<SyncTaskExecResult>();
		
		if(StringUtils.isEmpty(userId)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "userId为空，无法批量查询！");
			return res;
		}
		
		if(StringUtils.isEmpty(chanNo)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "渠道号为空，无法批量查询！");
			return res;
		}
		
		res = mMapper.getRecordsByUserInfo(getTableName(), chanNo, userId);
		if(res == null) {
			res = new ArrayList<SyncTaskExecResult>();
		}
		
		return res;
	}

	@Override
	public String getTableName() {
		return "sync_task_statistics_info";
	}

	private static final String M_SQL_TAB_CREATE = 
	          "_id bigint(20) NOT NULL AUTO_INCREMENT, " +
	          "offer_id varchar(63) NOT NULL, " +
	          "user_id varchar(255) NOT NULL, " +
	          "chan_no varchar(255) NOT NULL, " +
	          "pkg_name varchar(255) NOT NULL, " +
	          "task_state int(7), " +
	          "create_time timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP, " +
	          "change_time timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP, " +
	          "PRIMARY KEY (_id)";
	
	@Override
	public String getCreateTableSql() {
		return M_SQL_TAB_CREATE;
	}

}
