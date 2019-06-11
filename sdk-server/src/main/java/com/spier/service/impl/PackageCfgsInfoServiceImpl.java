package com.spier.service.impl;

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
import com.spier.common.bean.db.PackageCfgsInfo;
import com.spier.mapper.IPkgCfgsInfoMapper;
import com.spier.service.IPkgCfgsInfoService;

/**
 * 包配置信息服务实现类
 * @author GHB
 * @version 1.0
 * @date 2019.2.20
 */
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class PackageCfgsInfoServiceImpl implements IPkgCfgsInfoService {

	@Autowired
	private IPkgCfgsInfoMapper mMapper;
	
	@Override
	public Map<String, Boolean> getCfgsByPackageName(String pkgName) {
		Map<String, Boolean> res = new HashMap<String, Boolean>();
		
		if(StringUtils.isEmpty(pkgName)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "包名为空，无法获取配置信息！");
			return res;
		}
		
		PackageCfgsInfo info = mMapper.getCfgsByPackageName(getTableName(), pkgName);
		if(null == info) {
			return res;
		}
		
		res = info.deserializeCfgs();
		if(null == res) {
			res = new HashMap<String, Boolean>();
		}
		
		return res;
	}
	
	@Override
	public String getTableName() {
		return "pkg_cfgs";
	}

	private static final String M_SQL_TAB_CREATE = 
	          "pkg varchar(255) NOT NULL, " +
	          "cfgs varchar(2047), " +
	          "create_time timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP, " +
	          "PRIMARY KEY (pkg)";
	
	@Override
	public String getCreateTableSql() {
		return M_SQL_TAB_CREATE;
	}
}
