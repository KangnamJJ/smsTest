package com.spier.service.impl.job;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.spier.common.bean.db.CPICampaignInfo;
import com.spier.mapper.job.ICPICampaignInfoMapper;
import com.spier.service.job.ICPICampaignInfoService;

/**
 * CPI Campaign信息服务实现类
 * @author GHB
 * @version 1.0
 * @date 2019.2.14
 */
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class CPICampaignInfoServiceImpl implements ICPICampaignInfoService {

	@Autowired
	private ICPICampaignInfoMapper mMapper;
	
	@Override
	public boolean addOrUpdateCampaignsBatch(List<CPICampaignInfo> campaigns) {
		if(null == campaigns) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "campaigns为null，无法新增或更新Campaign信息");
			return false;
		}
		
		if(campaigns.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.WARNING, "campaigns为空，无需新增或更新Campaign信息");
			return true;
		}
		
		mMapper.addOrUpdateBatch(getTableName(), campaigns);
		
		return true;
	}
	
	@Override
	public boolean deleteCampaignsByIdBatch(List<String> ids) {
		if(null == ids) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "ids为null，无法批量删除campaign");
			return false;
		}
		
		if(ids.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.WARNING, "ids为空，无需批量删除Campaign");
			return true;
		}
		
		mMapper.deleteByIdsBatch(getTableName(), ids);
		return true;
	}

	@Override
	public List<CPICampaignInfo> getAll() {
		List<CPICampaignInfo> res = mMapper.getAll(getTableName());
		if(null == res) {
			res = new ArrayList<CPICampaignInfo>();
		}
		
		return res;
	}
	
	@Override
	public String getTableName() {
		return "cpi_campaigns";
	}

	private static final String M_SQL_TAB_CREATE = 
	          "campaign varchar(255), " +
	          "pkg varchar(127), " +
	          "camp_desc varchar(2047), " +
	          "icon_ids varchar(255), " +
	          "mat_ids varchar(255), " +
	          "offer_ids varchar(255), " +
	          "create_time timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP, " +
	          "change_time timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP, " +
	          "PRIMARY KEY (offer_ids)";
	
	@Override
	public String getCreateTableSql() {
		return M_SQL_TAB_CREATE;
	}

}
