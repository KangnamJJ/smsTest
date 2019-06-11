package com.spier.service.impl.spot;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.spier.entity.SpotInfo;
import com.spier.mapper.spot.ISpotInfoMapper;
import com.spier.service.spot.ISpotInfoService;

/**
 * 埋点信息服务实现类
 * @author GHB
 * @version 1.0
 * @date 2019.1.18
 */
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class SpotInfoServiceImpl implements ISpotInfoService {

	@Autowired
	private ISpotInfoMapper mSpotInfoMapper;
	
	@Override
	public boolean addOrUpdateRecord(SpotInfo spot) {
		if(null == spot) {
			return false;
		}
		
		boolean res = false;
		
		SpotInfo record = getSpotByKeyConds(spot.getUID(), spot.getChanNo(), spot.getFlowId());
		if(record == null) {
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("新增埋点：{0}", spot));
			mSpotInfoMapper.addRecord(getTableName(), spot);
			res = spot.getIndex() > 0;
		} else {
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("更新埋点：{0}", record));
			int ind = record.getIndex();
			record.copyFrom(spot);
			record.setIndex(ind);
			mSpotInfoMapper.updateRecord(getTableName(), record);
			res = true;
		}
		
		return res;
	}
	
	private static final int M_MAX_MSG_LENGTH = 1024;
	
	@Override
	public boolean addRecordForClientTest(SpotInfo spot) {
		if(null == spot) {
			return false;
		}
		
		// 截断msg，超长会有异常
		if(!StringUtils.isEmpty(spot.getInfo())) {
			if(spot.getInfo().length() > M_MAX_MSG_LENGTH) {
				spot.setInfo(spot.getInfo().substring(0, M_MAX_MSG_LENGTH));
			}
		}
		
		mSpotInfoMapper.addRecord(getTableName(), spot);
		
		return true;
	}

	@Override
	public List<SpotInfo> getSpotsByUser(String uid, String chanNo) {
		List<SpotInfo> res = new ArrayList<SpotInfo>();
		
		if(StringUtils.isEmpty(uid)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "uid为空，无法查询埋点");
			return res;
		}
		
		if(StringUtils.isEmpty(chanNo)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "渠道号为空，无法查询埋点");
			return res;
		}

		res = mSpotInfoMapper.getSpotsByKeyUser(getTableName(), uid, chanNo);
		if(res == null) {
			res = new ArrayList<SpotInfo>();
		}
		
		return res;
	}

	@Override
	public SpotInfo getSpotByKeyConds(String uid, String chanNo, String fid) {
		SpotInfo res = null;
		
		if(StringUtils.isEmpty(uid)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "uid为空，无法查询埋点");
			return res;
		}
		
		if(StringUtils.isEmpty(chanNo)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "渠道号为空，无法查询埋点");
			return res;
		}
		
		if(StringUtils.isEmpty(fid)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "会话id为空，无法查询埋点");
			return res;
		}
		
		return mSpotInfoMapper.getSpotByKeyConds(getTableName(), uid, chanNo, fid);
	}
	
	@Override
	public void deleteByInd(int ind) {
		if(ind <= 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"ind【{0}】无效，不能删除埋点", ind));
			return;
		}
		
		mSpotInfoMapper.deleteByInd(getTableName(), ind);
	}

	@Override
	public List<SpotInfo> getSpotsByCondition(Map<String, Object> args) {
	List<SpotInfo>	res = mSpotInfoMapper.getSpotsByCondition(args);
		if(res == null) {
			res = new ArrayList<SpotInfo>();
		}

		return res;
	}

	@Override
	public List<SpotInfo> getSpotsLimit(int from, int pageLimit) {
		List<SpotInfo> res = new ArrayList<SpotInfo>();
		
		from = Math.max(0, from);
		if(pageLimit <= 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("页条数【{0}】无效，无法查询埋点信息", pageLimit));
			return res;
		}
		
		res = mSpotInfoMapper.getSpotsLimit(getTableName(), from, pageLimit);
		if(null == res) {
			res = new ArrayList<SpotInfo>();
		}
		
		return res;
	}
	
	@Override
	public int getSpotsAmount() {
		Integer res = mSpotInfoMapper.getSpotsAmount(getTableName());
		if(null == res) {
			return 0;
		}
		
		return res;
	}

	@Override
	public int getSpotsAmountByConditon(Map<String, Object> args) {
		List<SpotInfo> res = mSpotInfoMapper.getSpotsByCondition(args);
		if(null == res) {
			return 0;
		}

		return res.size();
	}

	private static final String M_TAB_NAME = "spot_info";
	
	@Override
	public String getTableName() {
		return M_TAB_NAME;
	}

	private static final String M_SQL_TAB_CREATE = 
			"_id bigint(20) NOT NULL AUTO_INCREMENT, " +
	          "ch_no varchar(64), " +
	          "app varchar(64), " +
	          "user varchar(64), " +
	          "session varchar(64), " +
	          "type int(5), " +
	          "tag varchar(128), " +
	          "msg varchar(2048), " +
	          "change_time timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP, " +
	          "PRIMARY KEY (_id)";
	
	@Override
	public String getCreateTableSql() {
		return M_SQL_TAB_CREATE;
	}

}
