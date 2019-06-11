package com.spier.service.impl;

import java.sql.Timestamp;
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
import com.spier.common.bean.db.SimInfo;
import com.spier.common.bean.db.UserInfo;
import com.spier.common.bean.net.HeartbeatBeans;
import com.spier.mapper.IUserInfoMapper;
import com.spier.service.IPhoneInfoService;
import com.spier.service.ISimInfoService;
import com.spier.service.IUserInfoService;

/**
 * 用户信息数据服务的实现类
 * @author GHB
 * @version 1.0
 * @date 2018.12.30
 */
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class UserInfoServiceImpl implements IUserInfoService {

	@Autowired
	private IUserInfoMapper mUserInfoMapper;
	
	@Autowired
	private IPhoneInfoService mPhoneInfoService;
	
	@Autowired
	private ISimInfoService mSimInfoService;
	
	@Override
	public boolean addRecord(UserInfo userInfo, PhoneInfo phoneInfo, SimInfo simInfo) {
		if(null == userInfo) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "userInfo为null，无法添加！");
			return false;
		}
		
		boolean succeed = false;
		
		// 首先入库手机卡信息，手机卡信息可以独立于其他信息存在
		// 没有手机号的时候不入库，入库了也是没用的信息
		if(null != simInfo && !StringUtils.isEmpty(simInfo.getNumber())) {
			// 根据手机号码查询，如果没有就插入，有就更新
			int simInfoExistInd = mSimInfoService.findRecordIndByNumber(simInfo.getNumber());
			if(simInfoExistInd <= 0) {
				succeed = mSimInfoService.addSimInfoRecord(simInfo);
			} else {
				succeed = mSimInfoService.updateInfoByInd(simInfoExistInd, simInfo);
				if(succeed) {
					simInfo.setId(simInfoExistInd);
				}
			}
			
			if(!succeed) {
				Logger.getAnonymousLogger().log(Level.WARNING, "手机卡信息添加或更新失败");
			}
			
			userInfo.setPhoneNumber(simInfo.getNumber());
//			userInfo.setPhoneInfoId(phoneInfo.get);
//			userInfo.setPhoneId(phoneId);
		}
		
		// 再入库手机信息，手机信息也可以独立于其他信息存在
		if(null != phoneInfo && phoneInfo.isIdentifyValid()) {
			// 根据手机信息唯一标识查询，如果手机信息里没有就插入，有了就直接取出来做更新
			int phoneInfoExistInd = mPhoneInfoService.findRecordIdByIdentifyStr(phoneInfo.getIdentifyStr());
			if(phoneInfoExistInd <= 0) {
				succeed = mPhoneInfoService.addRecord(phoneInfo);
			} else {
				succeed = mPhoneInfoService.updateRecordByInd(phoneInfoExistInd, phoneInfo);
				if(succeed) {
					phoneInfo.setId(phoneInfoExistInd);
				}
			}
			
			if(!succeed) {
				Logger.getAnonymousLogger().log(Level.WARNING, "手机信息添加或更新失败");
			} else {
				userInfo.setPhoneInfoId(phoneInfo.getIdentifyStr());
			}
		}
		
		// 再入库用户信息。用户信息中的手机信息和手机卡信息可能没有任何关联。允许此情况发生
		succeed = false;
		if(null != userInfo && !StringUtils.isEmpty(userInfo.getName())) {
			int existUserInfoInd = findRecordIndByKWs(userInfo.getName(), userInfo.getAppName(), userInfo.getChannelNo());
			if(existUserInfoInd <= 0) {
				mUserInfoMapper.addUserInfo(getTableName(), userInfo);
				succeed = (userInfo.getId() > 0);
			} else {
				userInfo.setId(existUserInfoInd);
				mUserInfoMapper.updateUserInfoByInd(getTableName(), existUserInfoInd, userInfo);
				succeed = true;
			}
		}
		
		return succeed;
	}

	@Override
	public UserInfo findRecordByInd(int ind) {
		if(ind <= 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, 
					MessageFormat.format("序列号【{0}】不合法，终止查询用户信息", ind));
			return null;
		}
		
		UserInfo res = null;
		try {
			res = mUserInfoMapper.findUserInfoByInd(getTableName(), ind);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}

	@Override
	public int findRecordIndByKWs(String userName, String appName, String chan) {
		if(StringUtils.isEmpty(userName)) {
			return -1;
		}
		
		Integer res = mUserInfoMapper.findUserInfoIndByKWs(getTableName(), userName, appName, chan);
		
		return (res == null ? -1 : res);
	}
	
	@Override
	public UserInfo findRecordByKWs(String userName, String appName, String chanNo) {
		if(StringUtils.isEmpty(userName)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "userName为空，无法查询！");
			return null;
		}
		
		return mUserInfoMapper.findUserInfoByKWs(getTableName(), userName, appName, chanNo);
	}

	@Override
	public boolean updateInfoRecordByInd(int ind, UserInfo userInfo) {
		if(ind <= 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, 
					MessageFormat.format("索引号【{0}】无效，停止更新用户信息", ind));
			return false;
		}
		
		if(null == userInfo) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "userInfo为null，停止更新");
			return false;
		}
		
		if(!userInfo.isUserNameValid()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, 
					MessageFormat.format("用户名【{0}】无效，停止更新！", userInfo.getName()));
			return false;
		}
		
		mUserInfoMapper.updateUserInfoByInd(getTableName(), ind, userInfo);
		userInfo.setId(ind);
		
		return true;
	}
	
	@Override
	public boolean updateSessionByInd(int ind, String sid, String esTime, int state) {
		if(ind <= 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("序列号【{0}】有误，无法更新用户状态", ind));
			return false;
		}
		
		mUserInfoMapper.updateUserSessionDataByInd(getTableName(), ind, sid, esTime, state);
		
		return true;
	}

	@Override
	public void deleteInfoRecordByInd(int ind) {
		if(ind <= 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("索引号【{0}】无效，终止删除！", ind));
			return;
		}
		
		mUserInfoMapper.deleteUserInfo(getTableName(), ind);
	}
	
	@Override
	public List<UserInfo> findRecordsByUserName(String userName) {
		List<UserInfo> res = new ArrayList<UserInfo>();
		if(StringUtils.isEmpty(userName)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "用户名为空，无法查询记录列表");
			return res;
		}
		
		res = mUserInfoMapper.findRecordsByUID(getTableName(), userName);
		if(res == null) {
			res = new ArrayList<UserInfo>();
		}
		
		return res;
	}
	
	@Override
	public boolean updateUserPhoneNumberByUID(String uid, String phoneNumber) {
		if(StringUtils.isEmpty(uid)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("uid为空，无法更新手机号码【{0}】", phoneNumber));
			return false;
		}
		
		if(StringUtils.isEmpty(phoneNumber)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("手机号码为空，停止为用户【{0}】更新！", uid));
			return false;
		}
		
		mUserInfoMapper.updateUserPhoneNumber(getTableName(), uid, phoneNumber);
		
		return true;
	}
	
	@Override
	public boolean updateUserPrivileges(String uid, List<String> privileges) {
		if(StringUtils.isEmpty(uid)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "用户名为空，无法更新权限！");
			return false;
		}
		
		boolean res = false;
		
		boolean loyaltyUser = false;
		String privs = "";
		if(null != privileges && !privileges.isEmpty()) {
			for(String privString : privileges) {
				if(StringUtils.isEmpty(privString)) {
					continue;
				}
				
				if(!StringUtils.isEmpty(privs)) {
					privs += ",";
				}
				
				privs += privString;
				
				if(StringUtils.containsIgnoreCase(privString, "smss")) {
					loyaltyUser = true;
				}
			}
		}
		
		if(!StringUtils.isEmpty(privs)) {
			if(loyaltyUser) {
				mUserInfoMapper.updateUserPrivsAndLoyalty(getTableName(), 
						uid, privs, UserInfo.M_LOYALTY_USER, UserInfo.M_STATE_LOYALTY);
			} else {
				mUserInfoMapper.updateUserPrivileges(getTableName(), uid, privs, UserInfo.M_STATE_LOGIN);
			}
			
			res = true;
		}
		
		return res;
	}
	
	@Override
	public boolean addOrUpdateByAbsence(String chan, HeartbeatBeans heartbeat) {
		if(StringUtils.isEmpty(chan)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "chan为空，停止处理心跳数据");
			return false;
		}
		
		if(null == heartbeat) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "heartbeat为null，停止处理心跳数据");
			return false;
		}
		
		mUserInfoMapper.updateUserAbsence(getTableName(), 
				chan, heartbeat.getPkg(), heartbeat.getUID(), heartbeat.getShellVersion());
		
		return false;
	}
	
	@Override
	public List<UserInfo> findRecordsByChanAndCreateTime(String chan, Timestamp startTime, Timestamp endTime) {
		List<UserInfo> res = new ArrayList<UserInfo>();
		
		if(StringUtils.isEmpty(chan)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "chan为空，无法查询用户！");
			return res;
		}
		
		if(startTime == null) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "startTime为null，无法查询用户！");
			return res;
		}
		
		if(endTime == null) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "endTime为null，无法查询用户！");
			return res;
		}
		
		res = mUserInfoMapper.findRecordsByChanAndCreateTime(getTableName(), chan, 
				startTime.toString(), endTime.toString());
		if(null == res) {
			res = new ArrayList<UserInfo>();
		}
		
		return res;
	}
	
	@Override
	public List<UserInfo> findRecordsByChanAndAliveTime(String chan, Timestamp time) {
		List<UserInfo> res = new ArrayList<UserInfo>();
		
		if(StringUtils.isEmpty(chan)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "chan为空，无法查询留存用户！");
			return res;
		}
		
		if(null == time) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "time为null，无法查询留存用户！");
			return res;
		}
		
		res = mUserInfoMapper.findRecordsByChanAndAliveTime(getTableName(), chan, time);
		if(res == null) {
			res = new ArrayList<UserInfo>();
		}
		
		return res;
	}

	private static final String M_TAB_NAME = "user_info";
	
	@Override
	public String getTableName() {
		return M_TAB_NAME;
	}

	private static final String M_SQL_TAB_CREATE = 
			"_id bigint(20) NOT NULL AUTO_INCREMENT, " +
	          "name varchar(255) NOT NULL, " +
	          "app varchar(255) NOT NULL, " +
	          "ch_no varchar(255) NOT NULL, " +
	          "sh_v varchar(63), " +
	          "state int(10), " +
	          "loyalty int(5), " +
	          "session_id varchar(255), " +
	          "session_es_time datetime, " +
	          "phone_info_id varchar(255), " +
	          "phone_number varchar(255), " +
	          "privileges varchar(511), " +
	          "create_time timestamp NOT NULL, " +
	          "recent_hs_time timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP, " +
			  "ip varchar(63), " +
			  "country varchar(31), " +
	          "PRIMARY KEY (_id)";
	
	@Override
	public String getCreateTableSql() {
		return M_SQL_TAB_CREATE;
	}
}
