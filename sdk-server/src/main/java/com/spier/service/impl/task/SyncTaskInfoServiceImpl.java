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
import com.spier.common.bean.db.task.SyncTaskInfo;
import com.spier.mapper.task.ISyncTaskInfoMapper;
import com.spier.service.ISyncTaskInfoService;

/**
 * 同步任务信息实现类
 * @author GHB
 * @version 1.0
 * @date 2019.2.18
 */
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class SyncTaskInfoServiceImpl implements ISyncTaskInfoService {

	@Autowired
	private ISyncTaskInfoMapper mMapper;
	
	@Override
	public boolean updateOrInsert(List<SyncTaskInfo> tasks) {
		if(null == tasks) {
			return false;
		}
		
		if(tasks.isEmpty()) {
			return true;
		}
		
		mMapper.updateOrInsert(getTableName(), tasks);
		
		return true;
	}
	
	@Override
	public boolean updateOrInsert(SyncTaskInfo task) {
		if(null == task) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "task为null，停止更新或插入同步任务！");
			return false;
		}
		
		List<SyncTaskInfo> list = new ArrayList<SyncTaskInfo>();
		list.add(task);
		
		return updateOrInsert(list);
	}

	@Override
	public SyncTaskInfo getTaskByOfferId(String id) {
		if(StringUtils.isEmpty(id)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "offer-id为空，无法获取同步任务！");
			return null;
		}
		
		return mMapper.getTaskByOfferId(getTableName(), id);
	}
	
	@Override
	public List<SyncTaskInfo> getAll() {
		List<SyncTaskInfo> res = mMapper.getAll(getTableName());
		if(null == res) {
			res = new ArrayList<SyncTaskInfo>();
		}
		
		return res;
	}

	@Override
	public boolean deleteByIdsBatch(List<Integer> ids) {
		if(ids == null) {
			return false;
		}
		
		if(ids.isEmpty()) {
			return true;
		}
		
		mMapper.deleteByIdsBatch(getTableName(), ids);
		
		return true;
	}

	@Override
	public String getTableName() {
		return "sync_tasks";
	}

	private static final String M_SQL_TAB_CREATE = 
			"campaign varchar(255), " +
			"pkg varchar(255), " +
			"camp_description varchar(255), " +
			"icon_ids varchar(255), " +
			"mat_ids varchar(255), " + 
			"switcher int(8), " + 
			"finished int(16), " +
			"offer_id integer NOT NULL, " +
			  "offer_name varchar(255), " +
			  "offer_cate varchar(127), " +
			  "offer_desc varchar(511), " +
			  "version tinytext, " +
			  "incent tinytext, " +
			  "track_link varchar(255), " +
			  "preview_link varchar(255), " +
			  "flow varchar(127), " +
			  "market varchar(127), " +
			  "need_dev_info tinytext, " +
			  "currency tinytext, " +
			  "platforms varchar(63), " +
			  "countries varchar(63), " +
			  "payout double, " +
			  "payout_type varchar(63), " +
			  "cap int(12), " +
			  "allow_net_types varchar(63), " +
			  "forbid_net_types varchar(63), " +
			  "sub_aff_black_list varchar(255), " +
			  "create_time timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP, " +
			  "change_time timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP, " +
			  "PRIMARY KEY (offer_id)";
	
	@Override
	public String getCreateTableSql() {
		return M_SQL_TAB_CREATE;
	}
}
