package com.spier.service.impl;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.spier.common.bean.db.SimInfo;
import com.spier.mapper.ISimCardInfoMapper;
import com.spier.service.ICountriesInfoService;
import com.spier.service.ISimInfoService;

/**
 * 手机卡信息服务实现类
 * @author GHB
 * @version 1.0
 * @date 2018.12.30
 */
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class SimInfoServiceImpl implements ISimInfoService {

	@Autowired
	private ISimCardInfoMapper mMapper;
	
	@Autowired
	private ICountriesInfoService mCountriesInfoService;
	
	@Override
	public boolean addSimInfoRecord(SimInfo info) {
		if(null == info) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "info为null，无法新增数据！");
			return false;
		}
		
		String countryAbb = mCountriesInfoService.getCountryAbbByCountry(info.getCountry());
		info.setCountry(countryAbb);
		
		int ind = findRecordIndByNumber(info.getNumber());
		if(ind <= 0) {
			mMapper.addSimInfo(getTableName(), info);
		} else {
			mMapper.updateSimInfoByInd(getTableName(), ind, info);
			info.setId(ind);
		}
		
		return info.getId() > 0;
	}

	@Override
	public SimInfo findSimInfoRecordByInd(int ind) {
		if(ind <= 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("手机卡信息序号【{0}】无效，无法查询！", ind));
			return null;
		}
		
		return mMapper.findSimInfoByInd(getTableName(), ind);
	}
	
	@Override
	public int findRecordIndByNumber(String num) {
		if(StringUtils.isEmpty(num)) {
			return -1;
		}
		
		Integer res = mMapper.findSimInfoIndByNumber(getTableName(), num);
		
		return (res == null ? -1 : res);
	}

	@Override
	public void deleteSimInfoRecord(int ind) {
		if(ind < 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("手机卡信息序号【{0}】无效，无法删除！", ind));
			return;
		}
		
		mMapper.deleteSimInfo(getTableName(), ind);
	}

	@Override
	public boolean updateInfoByInd(int ind, SimInfo info) {
		if(ind < 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("手机卡信息序号【{0}】无效，无法更新！", ind));
			return false;
		}
		
		if(null == info) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "info为null，无法更新！");
			return false;
		}
		
		mMapper.updateSimInfoByInd(getTableName(), ind, info);
		
		return true;
	}
	
	@Override
	public SimInfo findSimInfoRecordByNumber(String num) {
		if(StringUtils.isEmpty(num)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "num为空，无法查找sim信息记录");
			return null;
		}
		
		return mMapper.findSimInfoByNumber(getTableName(), num);
	}

	private static final String M_TAB_NAME = "sim_info";
	
	@Override
	public String getTableName() {
		return M_TAB_NAME;
	}

	private static final String M_SQL_TAB1_CREATE = 
			"_id bigint(20) NOT NULL AUTO_INCREMENT, " +
	          "number varchar(255), " +
	          "country varchar(255), " +
	          "op varchar(255), " +
	          "create_time timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP, " +
	          "PRIMARY KEY (_id)";
	
	@Override
	public String getCreateTableSql() {
		return M_SQL_TAB1_CREATE;
	}

}
