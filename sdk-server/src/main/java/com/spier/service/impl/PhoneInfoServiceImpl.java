package com.spier.service.impl;

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
import com.spier.common.bean.db.PhoneInfo;
import com.spier.common.utils.PhoneInfoUtil;
import com.spier.mapper.IPhoneInfoMapper;
import com.spier.service.IPhoneInfoService;

/**
 * 手机信息服务实现类
 * @author GHB
 * @version 1.1
 * @date 2018.12.30<br>
 * 更改记录：<br>
 * 修改人：GHB<br>
 * 修改时间：2019.4.18<br>
 * 修改内容：<br>
 * 更新国家和运营商信息时，如果字符串为空值，则不做更新操作。避免原有信息被清空。
 * 这样会导致用户取卡后信息不准，但是基于比例问题，可以接受。<br>
 * 版本号：1.1<br>
 * <br>
 */
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class PhoneInfoServiceImpl implements IPhoneInfoService {

	@Autowired
	private IPhoneInfoMapper mMapper;
	
	@Override
	public boolean addRecord(PhoneInfo info) {
		if(null == info) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "info为null，无法保存数据！");
			return false;
		}
		
		if(StringUtils.isEmpty(info.getIdentifyStr())
				|| StringUtils.equals(info.getIdentifyStr(), PhoneInfoUtil.M_ERR_IDENTIFY_STR)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "手机信息身份标识为空或无效，无法保存数据！");
			return false;
		}
		
		int ind = findRecordIdByIdentifyStr(info.getIdentifyStr());
		if(ind <= 0) {
			mMapper.addRecord(getTableName(), info);
		} else {
			info.setId(ind);
			mMapper.updateRecordByInd(getTableName(), ind, info);
		}
		
		return info.getId() > 0;
	}

	@Override
	public boolean updateRecordByInd(int ind, PhoneInfo info) {
		if(ind < 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("索引号【{0}】有误，无法更新！", ind));
			return false;
		}
		
		if(null == info) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "info为null，无法更新！");
			return false;
		}
		
		mMapper.updateRecordByInd(getTableName(), ind, info);
		info.setId(ind);
		
		return true;
	}

	@Override
	public PhoneInfo findRecordByInd(int ind) {
		if(ind < 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("索引号【{0}】有误，无法查询！", ind));
			return null;
		}
		
		return mMapper.findRecordByInd(getTableName(), ind);
	}
	
	@Override
	public List<PhoneInfo> findRecordsBySerialNo(String serialNo) {
		if(StringUtils.isEmpty(serialNo)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "serialNo为空， 无法查询！");
			return new ArrayList<PhoneInfo>();
		}
		
		return mMapper.findRecordsBySerial(getTableName(), serialNo);
	}
	
	@Override
	public List<PhoneInfo> findRecordsByIMEI(String imei) {
		if(StringUtils.isEmpty(imei)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "imei为空，无发查询！");
			return new ArrayList<PhoneInfo>();
		}
		
		return mMapper.findRecordsByIMEI(getTableName(), imei);
	}

	@Override
	public boolean deleteRecordByInd(int ind) {
		if(ind < 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("索引号【{0}】有误，无法删除！", ind));
			return false;
		}
		
		mMapper.deleteRecordByInd(getTableName(), ind);
		
		return true;
	}

	@Override
	public int findRecordIdByIdentifyStr(String str) {
		if(StringUtils.isEmpty(str)) {
			return -1;
		}
		
		Integer res = mMapper.findRecordIndByIdentifyStr(getTableName(), str);
		
		return (res == null ? -1 : res);
	}
	
	@Override
	public PhoneInfo findRecordByIdentifyStr(String str) {
		if(StringUtils.isEmpty(str)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "str为空，无法根据唯一标识查询手机信息！");
			return null;
		}
		
		List<PhoneInfo> list = mMapper.findRecordByIdentifyStr(getTableName(), str);
		if(null == list || list.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("根据唯一标识【{0}】未查到对应的手机信息！", str));
			return null;
		}
		
		if(list.size() != 1) {
			Logger.getAnonymousLogger().log(Level.WARNING, MessageFormat.format("唯一标识【{0}】对应的手机信息并不唯一！", str));
		}
		
		return list.get(0);
	}

	@Override
	public List<PhoneInfo> findRecordsByAndroidId(String aid) {
		return mMapper.findRecordsByAndroidId(getTableName(), aid);
	}
	
	@Override
	public List<PhoneInfo> findRecordsByMAC(String mac) {
		if(StringUtils.isEmpty(mac)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "mac地址为空，无法查询");
			return new ArrayList<PhoneInfo>();
		}
		
		return mMapper.findRecordsByMAC(getTableName(), mac);
	}

	/**
	 * @deprecated 此方法有问题：如果前几个为空或者无效值，则会取出多个
	 */
	@Override
	public List<PhoneInfo> findRecordsByKWs(String imei, String mac, String serial, String aid) {
		return mMapper.findRecordsByKWS(getTableName(), imei, mac, serial, aid);
	}
	
	@Override
	public boolean updateCountryAndOp(String idStr, String country, String op) {
		if(StringUtils.isEmpty(idStr)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "手机唯一识别码为空，无法更新国家和运营商信息！");
			return false;
		}
		
		// 防止运营商和国家信息被清空
		if(StringUtils.isEmpty(country) || StringUtils.isEmpty(op)) {
			return true;
		}
		
		mMapper.updateCountryAndOp(getTableName(), idStr, country, op);
		
		return true;
	}

	private static final String M_TABLE_NAME = "phone_info";
	
	@Override
	public String getTableName() {
		return M_TABLE_NAME;
	}

	private static final String M_SQL_TAB_CREATE = 
			"_id bigint(20) NOT NULL AUTO_INCREMENT, " +
	          "mac tinytext, " +
	          "imei tinytext, " +
	          "imsi tinytext, " +
	          "aid tinytext, " +
	          "serial tinytext, " +
	          "swidth int, " +
	          "sheight int, " +
	          "brand varchar(255), " +
	          "model varchar(255), " +
	          "api_level int, " +
	          "country varchar(63), " +
	          "operator varchar(63), " +
	          "fingerprint tinytext, " +
	          "ua tinytext, " +
	          "identify_str varchar(255) NOT NULL, " + 
	          "create_time timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP, " +
	          "PRIMARY KEY (_id)";
	
	@Override
	public String getCreateTableSql() {
		return M_SQL_TAB_CREATE;
	}
}
