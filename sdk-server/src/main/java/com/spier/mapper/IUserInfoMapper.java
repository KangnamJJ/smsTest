package com.spier.mapper;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spier.common.bean.db.UserInfo;

/**
 * 用户信息表操作mapper
 * @author GHB
 * @version 1.0
 * @date 2018.12.30
 */
public interface IUserInfoMapper {

	/**
	 * 添加用户信息
	 * @param info
	 */
	public void addUserInfo(@Param("tableName") String tabName, @Param("info") UserInfo info);
	
	/**
	 * 删除用户信息
	 * @param ind 
	 */
	public void deleteUserInfo(@Param("tableName") String tabName, @Param("ind") int ind);
	
	/**
	 * 根据索引号查用户信息
	 * @param ind 索引号
	 * @return 可能为null
	 */
	public UserInfo findUserInfoByInd(@Param("tableName") String tabName, @Param("ind") int ind);
	
	/**
	 * 根据关键字段查询行序列号
	 * @param userName 用户名称
	 * @param appName 应用名称
	 * @param chNo 渠道号
	 * @return 大于0存在；否则不存在
	 */
	public Integer findUserInfoIndByKWs(@Param("tableName") String tabName, @Param("userName") String userName, @Param("appName") String appName, @Param("chno") String chNo);
	
	/**
	 * 根据用户名和应用名查询整行记录
	 * @param userName 用户名
	 * @param appName 应用名
	 * @return 可能为null
	 */
	public UserInfo findUserInfoByKWs(@Param("tableName") String tabName, @Param("userName") String userName, @Param("appName") String appName, @Param("chno") String chNo);
	
	/**
	 * 根据用户名查询所有的用户信息记录
	 * @param tabName
	 * @param uid
	 * @return 可能为null
	 */
	public List<UserInfo> findRecordsByUID(@Param("tableName") String tabName, @Param("uid") String uid);
	
	/**
	 * 查询某个时间段内某个渠道的用户信息
	 * @param tabName
	 * @param chan 渠道
	 * @param start 起始时间
	 * @param end 结束时间
	 * @return 可能为null
	 */
	public List<UserInfo> findRecordsByChanAndCreateTime(@Param("tableName") String tabName, @Param("chan") String chan,
			@Param("startTime") String start, @Param("endTime") String end);
	
	/**
	 * 根据渠道号查询指定时间所有的留存用户信息，在此时间之后的留存也应算在此日的留存中。
	 * @param tabName
	 * @param chan 渠道号
	 * @param time 留存时间
	 * @return 可能为null
	 */
	public List<UserInfo> findRecordsByChanAndAliveTime(@Param("tableName") String tabName, @Param("chan") String chan,
			@Param("time") Timestamp time);
	
	/**
	 * 根据记录的序列号查询记录的入库时间
	 * @param ind 序列号
	 * @return 可能为null
	 */
	public Timestamp getRecordCreateTimeByInd(@Param("tableName") String tabName, @Param("ind") int ind);
	
	/**
	 * 通过索引号更改用户信息
	 * @param ind 索引号
	 * @param info 信息
	 */
	public void updateUserInfoByInd(@Param("tableName") String tabName, @Param("ind") int ind, @Param("info") UserInfo info);
	
	/**
	 * 更新用户会话
	 * @param ind
	 * @param sessionId 会话id
	 * @param establishTime 会话建立时间
	 * @param state 用户状态
	 */
	public void updateUserSessionDataByInd(@Param("tableName") String tabName, @Param("ind") int ind, 
			@Param("sid") String sessionId, @Param("es_time") String establishTime, @Param("user_state") int state);
	
	/**
	 * 
	 * @param tabName
	 * @param uid
	 * @param phoneNum
	 */
	public void updateUserPhoneNumber(@Param("tableName") String tabName, @Param("uid") String uid, @Param("pnum") String phoneNum);
	
	/**
	 * 更新用户的权限
	 * @param tabName
	 * @param uid
	 * @param privs
	 */
	public void updateUserPrivileges(@Param("tableName") String tabName, @Param("uid") String uid, @Param("privs") String privs,
			@Param("state") int userState);
	
	/**
	 * 更新用户权限和忠实记录
	 * @param tabName
	 * @param uid
	 * @param privs
	 * @param loyalty
	 * @param userState 
	 */
	public void updateUserPrivsAndLoyalty(@Param("tabName") String tabName, 
			@Param("uid") String uid, @Param("privs") String privs, @Param("loyal") int loyalty, @Param("state") int userState);
	
	/**
	 * 根据握手信息更新用户信息
	 * @param tabName
	 * @param chan
	 * @param uid
	 * @param shellVersion
	 */
	public void updateUserAbsence(@Param("tableName") String tabName, @Param("chan") String chan, @Param("pkg") String pkgName,
			@Param("uid") String uid, @Param("shv") String shellVersion);
}
