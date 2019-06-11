package com.spier.service;

import java.util.List;

import com.spier.common.bean.db.PhoneInfo;

/**
 * 手机信息服务接口类
 * @author GHB
 * @version 1.0
 * @date 2018.12.30
 */
public interface IPhoneInfoService extends IAutoInitTable {

	/**
	 * 增加记录，成功入库后，info.mId将会被赋值
	 * @param info
	 * @return 操作结果
	 */
	public boolean addRecord(PhoneInfo info);
	
	/**
	 * 更新索引号是ind的记录
	 * @param ind 序列号
	 * @param info 
	 */
	public boolean updateRecordByInd(int ind, PhoneInfo info);
	
	/**
	 * 根据索引号查询记录
	 * @param ind 索引号
	 * @return 可能为null
	 */
	public PhoneInfo findRecordByInd(int ind);
	
	/**
	 * 根据androidId查询所有符合条件的记录
	 * @param aid
	 * @return 不为null
	 */
	public List<PhoneInfo> findRecordsByAndroidId(String aid);
	
	/**
	 * 根据手机序列号查询整行信息
	 * @param serialNo 序列号
	 * @return 不为null
	 */
	public List<PhoneInfo> findRecordsBySerialNo(String serialNo);
	
	/**
	 * 根据mac查询所有符合的信息
	 * @param mac
	 * @return 不为null
	 */
	public List<PhoneInfo> findRecordsByMAC(String mac);
	
	/**
	 * 根据imei查询所有记录
	 * @param imei
	 * @return 不为null
	 */
	public List<PhoneInfo> findRecordsByIMEI(String imei);
	
	/**
	 * 根据全部的关键信息查询所有符合条件的信息，关键信息之间是或的关系
	 * @param imei
	 * @param mac
	 * @param serial serialno
	 * @param aid anroidid
	 * @return 不为null
	 */
	public List<PhoneInfo> findRecordsByKWs(String imei, String mac, String serial, String aid);
	
	/**
	 * 通过唯一标识查找记录的序号
	 * @param str 唯一标识
	 * @return 如果没找到小于0的值，否则大于0
	 */
	public int findRecordIdByIdentifyStr(String str);
	
	/**
	 * 根据唯一标识查出记录
	 * @param str
	 * @return 可能为null
	 */
	public PhoneInfo findRecordByIdentifyStr(String str);
	
	/**
	 * 更新国家和运营商信息
	 * @param idStr 手机唯一标识
	 * @param country
	 * @param op
	 * @return 操作是否成功
	 */
	public boolean updateCountryAndOp(String idStr, String country, String op);
	
	/**
	 * 根据索引号删除记录
	 * @param ind
	 * @return 操作是否成功
	 */
	public boolean deleteRecordByInd(int ind);
}
