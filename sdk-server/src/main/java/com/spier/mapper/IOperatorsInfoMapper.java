package com.spier.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spier.common.bean.db.OperatorInfo;

/**
 * 运营商信息表
 * @author GHB
 * @version 1.0
 * @date 2019.1.15
 */
public interface IOperatorsInfoMapper {

	/**
	 * 添加运营商信息
	 * @param tabName
	 * @param info
	 */
	public void addOperator(@Param("tableName") String tabName, @Param("info") OperatorInfo info);
	
	/**
	 * 批量添加
	 * @param tabName
	 * @param ops
	 * @return 最后一条记录的id，可能为null
	 */
	public Integer addOperatorsBatch(@Param("tableName") String tabName, @Param("ops") List<OperatorInfo> ops);
	
	/**
	 * 根据国家名称简称获取运营商列表
	 * @param tabName
	 * @param country
	 * @return 可能为null
	 */
	public List<OperatorInfo> getOperatorsByCountryAbb(@Param("tableName") String tabName, @Param("country") String country);
	
	/**
	 * 根据mcc文本查询国家简称
	 * @param tabName
	 * @param txt
	 * @return 可能为null
	 */
	public List<String> getCountryAbbByMCC(@Param("tableName") String tabName, @Param("mcc") String txt);
	
	/**
	 * 根据国家简称和mnc文本查询运营商名称
	 * @param tabName
	 * @param txt
	 * @return 可能为null
	 */
	public String getOperatorNameByAbbMNC(@Param("tableName") String tabName, @Param("abb") String abb, @Param("mnc") String txt);
	
	/**
	 * 根据国家简称和运营商名称查找运营商名称的正规写法。忽略大小写
	 * @param tabName
	 * @param abb
	 * @param opTxt
	 * @return 可能为null
	 */
	public List<String> findOperatorNameByAbbAndOpTxtIgnoreCase(@Param("tableName") String tabName, @Param("abb") String abb,
			@Param("opTxt") String opTxt);
	
	/**
	 * 统计表中有多少行数据
	 * @param tabName
	 * @return
	 */
	public int getOperatorsCount(@Param("tableName") String tabName);
	
	/**
	 * 获取所有记录
	 * @return
	 */
	public List<OperatorInfo> getAll(@Param("tableName") String tabName);
}
