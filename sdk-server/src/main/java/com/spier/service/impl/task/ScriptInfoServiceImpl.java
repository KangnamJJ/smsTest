package com.spier.service.impl.task;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.spier.common.bean.db.ScriptInfo;
import com.spier.common.config.RediskeyConstants;
import com.spier.common.utils.Objects;
import com.spier.config.cache.JedisClusterService;
import com.spier.mapper.task.IScriptInfoMapper;
import com.spier.service.task.IScriptInfoService;

/**
 * 脚本文件信息服务实现类
 * @author GHB
 * @version 1.0
 * @date 2019.1.5
 */
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class ScriptInfoServiceImpl implements IScriptInfoService {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(ScriptInfoServiceImpl.class);
	@Autowired
	private IScriptInfoMapper mScriptInfoMapper;
	@Autowired
	private JedisClusterService jedisClusterService;
	@Override
	public List<ScriptInfo> getAllInfo() {
		List<ScriptInfo> res = null;
		try {
			String scriptinfo_key = RediskeyConstants.SCRIPTS_INFO_KEY ; 
			String scriptInfoStr = jedisClusterService.get(scriptinfo_key);
			res = JSON.parseObject(scriptInfoStr, new TypeReference<List<ScriptInfo>>(){});
			//缓存中获取不到，查询数据库，并保存在缓存中
			if(res == null || res.size()<0) {
				res = mScriptInfoMapper.getAll(getTableName());
				if(res == null || res.size()<0) {
					logger.error("查询scripts_info数据库,无数据");
					return new ArrayList<ScriptInfo>();
				}
				jedisClusterService.saveOrUpdate(scriptinfo_key, res);
			}
		} catch (Exception e) {
			logger.error("查询scripts_info数据库报错",e);
		}
		/*List<ScriptInfo> res = mScriptInfoMapper.getAll(getTableName());
		if(null == res) {
			res = new ArrayList<ScriptInfo>();
		}*/
		return res;
	}

	@Override
	public void deleteRecordByInd(int ind) {
		mScriptInfoMapper.deleteRecordByInd(getTableName(), ind);
	}

	@Override
	public void deleteRecordByScriptId(String id) {
		mScriptInfoMapper.deleteRecordByScriptId(getTableName(), id);
	}

	@Override
	public boolean insertScriptInfo(ScriptInfo info) {
		if(null == info) {
			return false;
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("文件路径：【{0}】", info.getFilePath()));
		mScriptInfoMapper.insertRecord(getTableName(), info);
		
		return info.getIndex() > 0;
	}

	@Override
	public ScriptInfo findScriptInfoByInd(int ind) {
		//return mScriptInfoMapper.findRecordByInd(getTableName(), ind);
		ScriptInfo record = null;
		try {
			String key = RediskeyConstants.SCRIPTS_INFO_KEY + ind ; 
			record = jedisClusterService.getValue(key,ScriptInfo.class);
			//缓存中获取不到，查询数据库，并保存在缓存中
			if(Objects.isEmpty(record)) {
				record = mScriptInfoMapper.findRecordByInd(getTableName(), ind);
				if(Objects.isEmpty(record)) {
					logger.error("根据脚本序列号ind{}查询，无数据",ind);
					return null;
				}
				jedisClusterService.saveOrUpdate(key, record);
			}
		} catch (Exception e) {
			logger.error("根据脚本序列号ind{}查询报错",ind,e);
		}
		return record;
	}

	@Override
	public List<ScriptInfo> findScriptInfosByScriptId(String id) {
		if(StringUtils.isEmpty(id)) {
			return new ArrayList<ScriptInfo>();
		}
		/*List<ScriptInfo> res = mScriptInfoMapper.findRecordByScriptId(getTableName(), id);
		if(null == res) {
			res = new ArrayList<ScriptInfo>();
		}*/
		List<ScriptInfo> res = null;
		try {
			String scriptInfo_key = RediskeyConstants.NORAML_FILE_INFO_KEY ; 
			String scriptInfoStr = jedisClusterService.get(scriptInfo_key);
			res = JSON.parseObject(scriptInfoStr, new TypeReference<List<ScriptInfo>>(){});
			//缓存中获取不到，查询数据库，并保存在缓存中
			if(res == null || res.size()<0) {
				res = mScriptInfoMapper.findRecordByScriptId(getTableName(), id);
				if(res == null || res.size()<0) {
					logger.error("获取所有文件记录信息,无数据");
					return new ArrayList<ScriptInfo>();
				}
				jedisClusterService.saveOrUpdate(scriptInfoStr, res);
			}
		} catch (Exception e) {
			logger.error("获取所有文件记录信息报错",e);
		}
		return res;
	}

	@Override
	public boolean updateScriptInfoByInd(int ind, ScriptInfo info) {
		if(ind <= 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("ind【{0}】不合法，无法更新", ind));
			return false;
		}
		
		if(null == info) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "info为null，无法更新");
			return false;
		}
		
		mScriptInfoMapper.updateRecordByInd(getTableName(), ind, info);
		
		return true;
	}

	@Override
	public boolean updateScriptInfoByScriptId(String scriptId, ScriptInfo info) {
		if(StringUtils.isEmpty(scriptId)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "scriptId为空，无法更新");
			return false;
		}
		
		if(null == info) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "info为null，无法更新");
			return false;
		}
		
		mScriptInfoMapper.updateRecordsByScriptId(getTableName(), scriptId, info);
		
		return true;
	}

	private static final String M_TAB_NAME = "scripts_info";
	
	@Override
	public String getTableName() {
		return M_TAB_NAME;
	}

	private static final String M_SQL_TAB1_CREATE = 
			"_id bigint(20) NOT NULL AUTO_INCREMENT, " +
	          "script_id varchar(255) NOT NULL, " +
	          "hash varchar(255), " +
	          "ver_code int, " +
	          "country varchar(12), " +
	          "op varchar(128), " +
	          "sc varchar(63), " +
	          "env int, " +
	          "path varchar(255), " +
	          "script_desc varchar(255), " +
	          "PRIMARY KEY (_id)";
	
	@Override
	public String getCreateTableSql() {
		return M_SQL_TAB1_CREATE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getScriptIdsByKeyConds(String countryAbb, String op, int netEnv) {
		List<String> res = null ; 
		try {
			String key = RediskeyConstants.SCRIPTS_INFO_KEY  + countryAbb + op + netEnv ; 
			res = jedisClusterService.getValue(key, List.class);
			//缓存中获取不到，查询数据库，并保存在缓存中
			if(res == null || res.size()<0) {
				res = mScriptInfoMapper.findScriptIdsByKeyConds(getTableName(), countryAbb, op, netEnv);
				if(res == null || res.size()<0) {
					logger.error("根据关键条件查询符合此条件的脚本id,无数据,入参countryAbb:{},op:{},netEnv:{}",countryAbb,op,netEnv);
					return new ArrayList<String>();
				}
				jedisClusterService.saveOrUpdate(key, res);
			}
		} catch (Exception e) {
			logger.error("根据关键条件查询符合此条件的脚本id报错,入参countryAbb:{},op:{},netEnv:{}",countryAbb,op,netEnv,e);
		}
		/*
		List<String> res = mScriptInfoMapper.findScriptIdsByKeyConds(getTableName(), countryAbb, op, netEnv);
		if(null == res) {
			res = new ArrayList<String>();
		}*/
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getScriptIdsByCountryAbbAndOp(String countryAbb, String op) {
		List<String> res = null ; 
		try {
			String key = RediskeyConstants.SCRIPTS_INFO_KEY  + countryAbb + op; 
			res = jedisClusterService.getValue(key, List.class);
			//缓存中获取不到，查询数据库，并保存在缓存中
			if(res == null || res.size()<0) {
				res = mScriptInfoMapper.findScriptIdsByCountryAbbAndOp(getTableName(), countryAbb, op);
				if(res == null || res.size()<0) {
					logger.error("根据国家简称和运营商查询脚本id,无数据,入参countryAbb:{},op:{}",countryAbb,op);
					return new ArrayList<String>();
				}
				jedisClusterService.saveOrUpdate(key, res);
			}
		} catch (Exception e) {
			logger.error("根据国家简称和运营商查询脚本id报错,入参countryAbb:{},op:{}",countryAbb,op,e);
		}
		/*List<String> res = mScriptInfoMapper.findScriptIdsByCountryAbbAndOp(getTableName(), countryAbb, op);
		if(null == res) {
			res = new ArrayList<String>();
		}*/
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getScriptInfosByCondition(String countryAbb, String op, int netEnv) {
		/*List<String> res = mScriptInfoMapper.getScriptInfosByCondition(getTableName(), countryAbb, op, netEnv);
		if(null == res) {
			res = new ArrayList<String>();
		}*/
		List<String> res = null ; 
		try {
			String key = RediskeyConstants.SCRIPTS_INFO_KEY  + countryAbb + op + netEnv ; 
			res = jedisClusterService.getValue(key, List.class);
			//缓存中获取不到，查询数据库，并保存在缓存中
			if(res == null || res.size()<0) {
				res = mScriptInfoMapper.findScriptIdsByKeyConds(getTableName(), countryAbb, op, netEnv);
				if(res == null || res.size()<0) {
					logger.error("根据关键条件查询符合此条件的脚本id,无数据,入参countryAbb:{},op:{},netEnv:{}",countryAbb,op,netEnv);
					return new ArrayList<String>();
				}
				jedisClusterService.saveOrUpdate(key, res);
			}
		} catch (Exception e) {
			logger.error("根据关键条件查询符合此条件的脚本id报错,入参countryAbb:{},op:{},netEnv:{}",countryAbb,op,netEnv,e);
		}
		return res;
	}
}
