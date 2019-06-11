package com.spier.service.impl;

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
import com.spier.common.bean.db.OperatorInfo;
import com.spier.common.config.RediskeyConstants;
import com.spier.config.cache.JedisClusterService;
import com.spier.mapper.IOperatorsInfoMapper;
import com.spier.service.IOperatorsInfoService;

/**
 * 运营商信息服务实现类
 * @author GHB
 * @version 1.0
 * @date 2019.1.15
 */
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class OperatorsInfoServiceImpl implements IOperatorsInfoService {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(OperatorsInfoServiceImpl.class);
	private static final String M_TAB_NAME = "operators_info";

	@Autowired
	private JedisClusterService jedisClusterService;
	@Override
	public String getTableName() {
		return M_TAB_NAME;
	}

	private static final String M_SQL_TAB_CREATE = 
			"_id bigint(20) NOT NULL AUTO_INCREMENT, " +
	          "abb varchar(255) NOT NULL, " +
	          "op varchar(255) NOT NULL, " +
	          "mcc varchar(10), " +
	          "mnc varchar(10), " +
	          "PRIMARY KEY (_id)";
	
	@Override
	public String getCreateTableSql() {
		return M_SQL_TAB_CREATE;
	}

	@Autowired
	private IOperatorsInfoMapper mOperatorsInfoMapper;
	
	@Override
	public boolean addOperator(OperatorInfo info) {
		if(null == info) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "info为null，无法进行运营商信息新增操作！");
			return false;
		}
		
		mOperatorsInfoMapper.addOperator(getTableName(), info);
		
		return info.getIndex() > 0;
	}

	@Override
	public List<OperatorInfo> getOperatorsByCountryAbb(String abb) {
		if(StringUtils.isEmpty(abb)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "国家简称为空，无法进行运营商信息查询操作！");
			return new ArrayList<OperatorInfo>();
		}
		
		List<OperatorInfo> res = mOperatorsInfoMapper.getOperatorsByCountryAbb(getTableName(), abb);
		if(res == null) {
			res = new ArrayList<OperatorInfo>();
		}
		
		return res;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getOperatorNameByAbbAndOpTxt(String abb, String opTxt) {
		List<String> res = new ArrayList<String>();
		
		if(StringUtils.isEmpty(abb)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"abb为空，无法查询【{0}】对应的运营商名称！", opTxt));
			return res;
		}
		
		if(StringUtils.isEmpty(opTxt)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "文本为空，无法查询运营商名称！");
			return res;
		}
		try {
			String key = RediskeyConstants.OPERATORS_INFO_KEY  + abb + opTxt ; 
			res = jedisClusterService.getValue(key, List.class);
			//缓存中获取不到，查询数据库，并保存在缓存中
			if(res == null || res.size()<0) {
				res = mOperatorsInfoMapper.findOperatorNameByAbbAndOpTxtIgnoreCase(getTableName(), abb, opTxt);
				if(res == null || res.size()<0) {
					logger.error("查询运营商名称数据库无数据,入参abb:{},opTxt{}:",abb,opTxt);
					return new ArrayList<String>();
				}
				jedisClusterService.saveOrUpdate(key, res);
			}
		} catch (Exception e) {
			logger.error("查询国家简称报错,入参abb:{},opTxt{}:",abb,opTxt,e);
		}
		/*res = mOperatorsInfoMapper.findOperatorNameByAbbAndOpTxtIgnoreCase(getTableName(), abb, opTxt);
		if(null == res) {
			res = new ArrayList<String>();
		}*/
		return res;
	}

	@Override
	public int getOperatorsAmount() {
		return mOperatorsInfoMapper.getOperatorsCount(getTableName());
	}

	@Override
	public List<OperatorInfo> getAll() {
		List<OperatorInfo> res = mOperatorsInfoMapper.getAll(getTableName());
		
		return (res == null ? new ArrayList<OperatorInfo>() : res);
	}

	@Override
	public boolean checkOperatorExistsByCountryAbbAndOpName(String abb, String op) {
		List<OperatorInfo> exists = getOperatorsByCountryAbb(abb);
		if(exists.isEmpty()) {
			return false;
		}
		
		boolean res = false;
		for(OperatorInfo existOp : exists) {
			if(StringUtils.equals(op, existOp.getOperatorName())) {
				res = true;
				break;
			}
		}
		
		return res;
	}

	@Override
	public boolean addOperatorsBatch(List<OperatorInfo> ops) {
		if(null == ops || ops.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "无运营商信息，停止批量插入！");
			return false;
		}
		
		Integer res = mOperatorsInfoMapper.addOperatorsBatch(getTableName(), ops);
		if(res == null) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "批量插入运营商信息失败！");
			return false;
		}
		
		return res > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getCountryAbbByMCC(String mcc) {
		if(StringUtils.isEmpty(mcc)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "mcc文本为空，无法查询国家简称！");
			return null;
		}
		List<String> res = null;
		try {
			String key = RediskeyConstants.COUNTRY_INFO_KEY  + mcc ; 
			res = jedisClusterService.getValue(key, List.class);
			//缓存中获取不到，查询数据库，并保存在缓存中
			if(res == null || res.size()<0) {
				res = mOperatorsInfoMapper.getCountryAbbByMCC(getTableName(), mcc);
				if(res == null || res.size()<0) {
					logger.error("查询国家简称无数据,入参mcc:{}",mcc);
					return null;
				}
				jedisClusterService.saveOrUpdate(key, res);
			}
		} catch (Exception e) {
			logger.error("查询国家简称报错,入参mcc:{}",mcc,e);
		}
		/*List<String> res = mOperatorsInfoMapper.getCountryAbbByMCC(getTableName(), mcc);
		if(null == res || res.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("根据MCC【{0}】未查到对应的国家名称！", mcc));
			return null;
		}*/
		return res.get(0);
	}

	@Override
	public String getOperatorNameByAbbMNC(String abb, String mnc) {
		if(StringUtils.isEmpty(mnc)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "mnc文本为空，无法查询运营商名称！");
			return null;
		}
		String mOperatorsInfo = null;
		try {
			String key = RediskeyConstants.OPERATORS_INFO_KEY + abb + mnc ; 
			mOperatorsInfo = jedisClusterService.get(key);
			//缓存中获取不到，查询数据库，并保存在缓存中
			if(StringUtils.isBlank(mOperatorsInfo)) {
				mOperatorsInfo = mOperatorsInfoMapper.getOperatorNameByAbbMNC(getTableName(), abb, mnc);
				if(StringUtils.isBlank(mOperatorsInfo)) {
					logger.error("查询运营商名称无数据,入参abb:{},mnc:{}",abb,mnc);
					return null;
				}
				jedisClusterService.saveOrUpdate(key, mOperatorsInfo);
			}
		} catch (Exception e) {
			logger.error("查询运营商名称报错,入参abb:{},mnc:{}",abb,mnc,e);
		}
		//return mOperatorsInfoMapper.getOperatorNameByAbbMNC(getTableName(), abb, mnc);
		return mOperatorsInfo;
	}


}
