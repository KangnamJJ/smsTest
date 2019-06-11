package com.spier.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spier.common.bean.db.PhoneInfo;

/**
 * 手机信息表操作mapper
 * @author GHB
 * @version 1.0
 * @date 2018.12.30
 */
public interface IPhoneInfoMapper {

	/**
	 * 添加手机信息记录。新增成功后，mId属性将被设置
	 * @param info
	 */
	public void addRecord(@Param("tableName") String tabName, @Param("info") PhoneInfo info);
	
	/**
	 * 根据序列号删除记录
	 * @param ind 序列号
	 */
	public void deleteRecordByInd(@Param("tableName") String tabName, @Param("ind") int ind);
	
	/**
	 * 根据序列号查询记录
	 * @param ind 序列号
	 * @return 查询到的记录，可能为null
	 */
	public PhoneInfo findRecordByInd(@Param("tableName") String tabName, @Param("ind") int ind);
	
	/**
	 * 通过手机身份唯一标识查找信息的序号
	 * @return 如果没查到则返回……
	 */
	public Integer findRecordIndByIdentifyStr(@Param("tableName") String tabName, @Param("idstr") String identifyStr);
	
	/**
	 * 通过唯一标识查找记录
	 * @param tabName
	 * @param id
	 * @return 可能为null
	 */
	public List<PhoneInfo> findRecordByIdentifyStr(@Param("tableName") String tabName, @Param("ids") String id);
	
	/**
	 * 根据serial查询记录
	 * @param serialNo 序列号
	 * @return 可能为null
	 */
	public List<PhoneInfo> findRecordsBySerial(@Param("tableName") String tabName, @Param("serialNo") String serialNo);
	
	/**
	 * 根据imei查询所有匹配的记录
	 * @param IMEI
	 * @return 可能为null
	 */
	public List<PhoneInfo> findRecordsByIMEI(@Param("tableName") String tabName, @Param("IMEI") String IMEI);
	
	/**
	 * 根据mac查询所有匹配的记录
	 * @param MAC
	 * @return
	 */
	public List<PhoneInfo> findRecordsByMAC(@Param("tableName") String tabName, @Param("MAC") String MAC);
	
	/**
	 * 根据安卓id查询所有匹配的记录
	 * @param androidId
	 * @return
	 */
	public List<PhoneInfo> findRecordsByAndroidId(@Param("tableName") String tabName, @Param("AndroidId") String androidId);
	
	/**
	 * 根据imei和mac查询所有匹配的记录，or的关系
	 * @param IMEI
	 * @param MAC
	 * @return
	 */
	public List<PhoneInfo> findRecordsByIMEIorMAC(@Param("tableName") String tabName, @Param("IMEI") String IMEI, @Param("MAC") String MAC);
	
	/**
	 * 根据imei和serailno查询所有匹配的记录，or的关系
	 * @param IMEI
	 * @param serialNo
	 * @return
	 */
	public List<PhoneInfo> findRecordsByIMEIorSerial(@Param("tableName") String tabName, @Param("IMEI") String IMEI, @Param("serialNo") String serialNo);
	
	/**
	 * 根据mac和serialno查询所有匹配的记录，or的关系
	 * @param MAC
	 * @param serialNo
	 * @return
	 */
	public List<PhoneInfo> findRecordsByMACorSerial(@Param("tableName") String tabName, @Param("MAC") String MAC, @Param("serialNo") String serialNo);
	
	/**
	 * 根据imei、mac、serialno查询所有匹配的记录，or的关系
	 * @param imei
	 * @param mac
	 * @param serial
	 * @return
	 */
	public List<PhoneInfo> findRecordsByIMEIorMACorSerial(@Param("tableName") String tabName, @Param("IMEI") String imei, @Param("MAC") String mac, @Param("serialNo") String serial);
	
	/**
	 * 根据这些关键字段查询所有匹配的记录，或的关系
	 * @param imei
	 * @param mac
	 * @param serial
	 * @param androidId
	 * @return
	 */
	public List<PhoneInfo> findRecordsByKWS(@Param("tableName") String tabName, @Param("IMEI") String imei, @Param("MAC") String mac, 
			@Param("serialNo") String serial, @Param("androidId") String androidId);
	
	/**
	 * 更新编号为ind的记录
	 * @param ind 编号
	 * @param info 要更改的记录
	 */
	public void updateRecordByInd(@Param("tableName") String tabName, @Param("ind") int ind, @Param("info") PhoneInfo info);
	
	/**
	 * 更新国家和运营商
	 * @param tabName
	 * @param idStr 手机唯一识别码
	 * @param country
	 * @param op
	 */
	public void updateCountryAndOp(@Param("tabName") String tabName, @Param("idstr") String idStr, 
			@Param("country") String country, @Param("op") String op);
}
