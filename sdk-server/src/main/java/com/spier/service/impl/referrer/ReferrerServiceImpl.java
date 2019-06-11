package com.spier.service.impl.referrer;

import java.text.MessageFormat;
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
import com.spier.common.bean.db.referrer.ReferrerBeans.ReferrerRequest;
import com.spier.common.bean.db.referrer.ReferrerInfo;
import com.spier.common.config.ReferrerConfig;
import com.spier.mapper.referrer.IReferrerMapper;
import com.spier.service.referrer.IReferrerService;

/**
 * 广告渠道数据服务实现类
 * @author GHB
 * @version 1.0
 * @date 2019.3.7
 */
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class ReferrerServiceImpl implements IReferrerService {

	@Autowired
	private IReferrerMapper mReferrerMapper;
	
	@Override
	public void addReferrer(String ip, String chan, ReferrerRequest request) {
		if(StringUtils.isEmpty(chan)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "渠道号为空，无法保存推广数据！");
			return;
		}
		
		if(null == request) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "request为null，无法保存推广数据！");
			return;
		}
		
		if(StringUtils.isEmpty(request.mAppName)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "app-name为空，无法保存推广数据！");
			return;
		}
		
		if(StringUtils.isEmpty(request.mUID)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "uid为空，无法保存推广数据！");
			return;
		}
		
		if(StringUtils.isEmpty(request.mReferrer)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "ref为空，无法保存推广数据！");
			return;
		}
		
		List<ReferrerInfo> exists = getRecordsByKeys(chan, request.mAppName, request.mUID);
		if(!exists.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.WARNING, MessageFormat.format(
					"新增【{0}---{1}---{2}】已经存在，不再重复新增！", chan, request.mAppName, request.mUID));
			return;
		}
		
		mReferrerMapper.addRef(getTableName(), chan, ReferrerInfo.fromRequest(ip, chan, request));
	}
	
	@Override
	public List<ReferrerInfo> getFreshConversions(int maxLimit) {
		List<ReferrerInfo> res = new ArrayList<ReferrerInfo>();
		
		if(maxLimit <= 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"maxLimit【{0}】不合法，停止查找referrer！", maxLimit));
			return res;
		}
		
		res = mReferrerMapper.getRecordsNotNotifiedLimit(getTableName(), maxLimit, ReferrerConfig.M_UNNOTIFIED_VALUE);
		if(null == res) {
			res = new ArrayList<ReferrerInfo>();
		}
		
		return res;
	}

	@Override
	public void markReferrerNotifiedState(List<Integer> inds, int state) {
		if(inds == null || inds.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "inds为null或空，不需要记录转化下发状态！");
			return;
		}
		
		mReferrerMapper.markRecordNotifyState(getTableName(), inds, state);
	}
	
	@Override
	public void deleteByInd(int ind) {
		if(ind <= 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("ind[{0}]不合法，无法删除记录！", ind));
			return;
		}
		
		mReferrerMapper.deleteByInd(getTableName(), ind);
	}
	
	@Override
	public List<ReferrerInfo> getRecordsByKeys(String chan, String pkg, String uid) {
		List<ReferrerInfo> res = new ArrayList<ReferrerInfo>();
		
		if(StringUtils.isEmpty(chan)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "chan为空，无法查询！");
			return res;
		}
		
		if(StringUtils.isEmpty(pkg)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "pkg为空，无法查询！");
			return res;
		}
		
		if(StringUtils.isEmpty(uid)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "uid为空，无法查询！");
			return res;
		}
		
		res = mReferrerMapper.getRecordByChanPkgUser(getTableName(), chan, pkg, uid);
		if(null == res) {
			res = new ArrayList<ReferrerInfo>();
		}
		
		return res;
	}

	@Override
	public String getTableName() {
		return "ref_raw";
	}

	private static final String M_SQL_TAB_CREATE = "_id bigint(20) NOT NULL AUTO_INCREMENT, " +
	          "chan varchar(255), " +
	          "app varchar(255), " +
	          "uid varchar(255), " +
	          "ip varchar(32), " +
	          "notif_state int(3), " +
	          "ref varchar(2047), " +
	          "create_time timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP, " +
	          "PRIMARY KEY (_id)";
	
	@Override
	public String getCreateTableSql() {
		return M_SQL_TAB_CREATE;
	}

}
