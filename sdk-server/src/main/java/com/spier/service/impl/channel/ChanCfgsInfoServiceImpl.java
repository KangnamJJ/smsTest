package com.spier.service.impl.channel;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.spier.common.bean.db.ChanCfgsInfo;
import com.spier.mapper.IChanCfgsInfoMapper;
import com.spier.service.channel.IChanCfgsInfoService;

/**
 * 渠道配置信息服务实现类
 * @author GHB
 * @version 1.0
 * @date 2019.2.20
 */
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class ChanCfgsInfoServiceImpl implements IChanCfgsInfoService {

	@Autowired
	private IChanCfgsInfoMapper mMapper;
	
	@Override
	public Map<String, Boolean> getCfgsByChanNo(String chanNo) {
		Map<String, Boolean> res = new HashMap<String, Boolean>();
		if(StringUtils.isEmpty(chanNo)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "渠道号为空，无法获取配置信息！");
			return res;
		}
		
		ChanCfgsInfo info = mMapper.getByChanNo(getTableName(), chanNo);
		if(info == null) {
			return res;
		}
		
		res = info.deserializeConfigs();
		if(null == res) {
			res = new HashMap<String, Boolean>();
		}
		
		return res;
	}
	
	@Override
	public String getTableName() {
		return "chan_cfgs";
	}

	private static final String M_SQL_TAB_CREATE = 
	          "chan_no varchar(63) NOT NULL, " +
	          "cfgs varchar(2047), " +
	          "create_time timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP, " +
	          "PRIMARY KEY (chan_no)";
	
	@Override
	public String getCreateTableSql() {
		return M_SQL_TAB_CREATE;
	}
}
