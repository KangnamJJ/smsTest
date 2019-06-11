package com.spier.service.impl.job;

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
import com.spier.common.bean.db.CPIMaterialInfo;
import com.spier.mapper.job.ICPIMaterialInfoMapper;
import com.spier.service.job.ICPIMaterialInfoService;

/**
 * CPI素材信息服务实现类
 * @author GHB
 * @version 1.0
 * @date 2019.2.14
 */
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class CPIMaterialInfoServiceImpl implements ICPIMaterialInfoService {

	@Autowired
	private ICPIMaterialInfoMapper mMapper;
	
	@Override
	public boolean addOrUpdateBatch(List<CPIMaterialInfo> items) {
		if(null == items) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "items为null，无法插入或更新素材信息");
			return false;
		}
		
		if(items.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.WARNING, "items为空，无需插入或更新素材信息");
			return true;
		}
		
		mMapper.addOrUpdateBatch(getTableName(), items);
		
		// 为参数中的元素设置主键
		setKeys(items);
		
		return true;
	}
	
	private void setKeys(List<CPIMaterialInfo> items) {
		if(null == items || items.isEmpty()) {
			return;
		}
		
		List<CPIMaterialInfo> listInDB = mMapper.getAll(getTableName());
		if(null == listInDB) {
			return;
		}
		
		for(CPIMaterialInfo info : listInDB) {
			if(null == info) {
				continue;
			}
			
			for(CPIMaterialInfo item : items) {
				if(null == item) {
					continue;
				}
				
				if(StringUtils.equals(info.getPixel(), item.getPixel())
						&& StringUtils.equals(info.getUrl(), item.getUrl())) {
					item.setIndex(info.getIndex());
					break;
				}
			}
		}
	}
	
	@Override
	public List<CPIMaterialInfo> getAll() {
		List<CPIMaterialInfo> res = mMapper.getAll(getTableName());
		if(res == null) {
			res = new ArrayList<CPIMaterialInfo>();
		}
		
		return res;
	}

	@Override
	public boolean deleteByIdBatch(List<Integer> ids) {
		if(null == ids) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "ids为null，无法批量删除素材信息");
			return false;
		}
		
		if(ids.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.WARNING, "ids为空，无需批量删除素材信息");
			return true;
		}
		
		mMapper.deleteByIdBatch(getTableName(), ids);
		
		return true;
	}
	
	@Override
	public String getTableName() {
		return "cpi_materials";
	}

	private static final String M_SQL_TAB_CREATE = 
			"_id bigint(20) NOT NULL AUTO_INCREMENT, " +
	          "pixel varchar(255), " +
	          "url varchar(255), " +
	          "create_time timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP, " +
	          "change_time timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP, " +
	          "PRIMARY KEY (_id)";
	
	@Override
	public String getCreateTableSql() {
		return M_SQL_TAB_CREATE;
	}

	
}
