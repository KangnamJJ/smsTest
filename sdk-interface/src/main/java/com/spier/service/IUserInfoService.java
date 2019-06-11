package com.spier.service;

import java.sql.Timestamp;
import java.util.List;

import com.spier.common.bean.db.PhoneInfo;
import com.spier.common.bean.db.SimInfo;
import com.spier.common.bean.db.UserInfo;
import com.spier.common.bean.net.HeartbeatBeans;

/**
 * 用户信息服务接口
 * @author GHB
 * @version 1.0
 * @date 2018.12.30
 */
public interface IUserInfoService extends IAutoInitTable {

	/**
	 * 添加记录
	 * @param userInfo 用户信息
	 * @param phoneInfo 手机信息
	 * @param simInfo 手机卡信息
	 * @return 操作是否成功
	 */
	public boolean addRecord(UserInfo userInfo, PhoneInfo phoneInfo, SimInfo simInfo);
	
	/**
	 * 通过记录序列号查询记录
	 * @param ind 序列号
	 * @return 可能为null
	 */
	public UserInfo findRecordByInd(int ind);
	
	/**
	 * 根据用户名和应用名查找对应记录的序号
	 * @param userName 用户名
	 * @param appName 应用名
	 * @param chanNo 渠道号
	 * @return 大于0的合法值；否则没找到
	 */
	public int findRecordIndByKWs(String userName, String appName, String chanNo);
	
	/**
	 * 根据用户名和应用名查找整行记录
	 * @param userName 用户名
	 * @param appName 应用名
	 * @param chanNo 渠道号
	 * @return 可能为null
	 */
	public UserInfo findRecordByKWs(String userName, String appName, String chanNo);
	
	/**
	 * 根据用户名查找其所有的用户信息
	 * @param userName
	 * @return 不为null
	 */
	public List<UserInfo> findRecordsByUserName(String userName);
	
	/**
	 * 根据行号更新数据行
	 * @param ind
	 * @param userInfo
	 * @return 
	 */
	public boolean updateInfoRecordByInd(int ind, UserInfo userInfo);
	
	/**
	 * 根据序列号更新用户会话状态
	 * @param ind
	 * @param sid 会话id
	 * @param esTime 会话建立时间
	 * @param state 用户状态
	 */
	public boolean updateSessionByInd(int ind, String sid, String esTime, int state);
	
	/**
	 * 通过用户id更新手机号
	 * @param uid
	 * @param phoneNumber
	 * @return 操作是否成功
	 */
	public boolean updateUserPhoneNumberByUID(String uid, String phoneNumber);
	
	/**
	 * 更新用户权限
	 * @param uid
	 * @param privileges
	 * @return 操作结果
	 */
	public boolean updateUserPrivileges(String uid, List<String> privileges);
	
	/**
	 * 根据索引号删除记录
	 * @param ind
	 */
	public void deleteInfoRecordByInd(int ind);
	
	/**
	 * 通过心跳数据更新用户状态
	 * @param chan 渠道号
	 * @param heartbeat 心跳数据
	 * @return 操作结果
	 */
	public boolean addOrUpdateByAbsence(String chan, HeartbeatBeans heartbeat);
	
	/**
	 * 根据渠道号和新增时间段查询用户信息
	 * @param chan 渠道号
	 * @param startTime 起始时间
	 * @param endTime 结束时间
	 * @return 不为null
	 */
	public List<UserInfo> findRecordsByChanAndCreateTime(String chan, Timestamp startTime, Timestamp endTime);
	
	/**
	 * 根据渠道号和留存时间查询直到所查询时间为止，所有留存用户信息。由于留存信息为覆盖的，因此在查询数据库时，应该包括
	 * 要查询日期之后的所有留存。
	 * @param chan 渠道号
	 * @param time 查询日期
	 * @return 不为null
	 */
	public List<UserInfo> findRecordsByChanAndAliveTime(String chan, Timestamp time);
}
