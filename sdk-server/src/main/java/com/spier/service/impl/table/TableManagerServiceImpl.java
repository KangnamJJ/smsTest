package com.spier.service.impl.table;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.spier.mapper.table.ITableManagerMapper;
import com.spier.service.table.ITableManagerService;

/**
 * 表管理服务实现类
 * @author GHB
 * @version 1.0
 * @date 2019.1.11
 */
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class TableManagerServiceImpl implements ITableManagerService {

	@Autowired
	private ITableManagerMapper mMapper;
	
	@Override
	public boolean checkTableExists(String tabName) {
		if(StringUtils.isEmpty(tabName)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "表名称为空，无法检查表是否存在");
			return false;
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("检查表【{0}】是否存在", tabName));
		
		return mMapper.checkTableExists(tabName);
	}

	@Override
	public boolean createTable(String tabName, String sqlCmd) {
		if(StringUtils.isEmpty(tabName)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "表名为空，无法创建表");
			return false;
		}
		
		if(StringUtils.isEmpty(sqlCmd)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "sql语句为空，无法创建表");
			return false;
		}
		
		try {
			mMapper.createTable(tabName, sqlCmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return checkTableExists(tabName);
	}

	@Override
	public boolean deleteTable(String tabName) {
		if(StringUtils.isEmpty(tabName)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "表名称为空，无法删除表");
			return false;
		}
		
		mMapper.dropTable(tabName);
		
		return !checkTableExists(tabName);
	}

	@Override
	public boolean clearTable(String tabName) {
		if(StringUtils.isEmpty(tabName)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "表名称为空，无法清空表");
			return false;
		}
		
		mMapper.clearTable(tabName);
		
		return true;
	}

}
