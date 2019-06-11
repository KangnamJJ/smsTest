package com.spier.service;

import java.util.List;

import com.spier.common.bean.db.OperatorInfo;

/**
 * 运营商信息服务接口
 * @author GHB
 * @version 1.0
 * @date 2019.1.15
 */
public interface IOperatorsInfoService extends IAutoInitTable {

	/**
	 * 添加运营商信息
	 * @param info
	 * @return 操作是否成功
	 */
	public boolean addOperator(OperatorInfo info);
	
	/**
	 * 批量插入运营商信息
	 * @param ops
	 * @return 操作是否成功
	 */
	public boolean addOperatorsBatch(List<OperatorInfo> ops);
	
	/**
	 * 根据国家简称查询运营商列表
	 * @param abb 
	 * @return 不为null
	 */
	public List<OperatorInfo> getOperatorsByCountryAbb(String abb);
	
	/**
	 * 根据MCC查询国家简称
	 * @param mcc
	 * @return 可能为null
	 */
	public String getCountryAbbByMCC(String mcc);
	
	/**
	 * 根据国家简称和MNC查询运营商名称
	 * @param abb 国家简称
	 * @param mnc
	 * @return 可能为null
	 */
	public String getOperatorNameByAbbMNC(String abb, String mnc);
	
	/**
	 * 根据国家简称和运营商名称字符串查询对应的运营商规则名称，忽略大小写。
	 * @param abb
	 * @param opTxt
	 * @return 不为null
	 */
	public List<String> getOperatorNameByAbbAndOpTxt(String abb, String opTxt);
	
	/**
	 * 获取表中的行数量
	 * @return
	 */
	public int getOperatorsAmount();
	
	/**
	 * 根据国家简称和运营商名称查询此运营商信息是否存在
	 * @param abb
	 * @param op
	 * @return
	 */
	public boolean checkOperatorExistsByCountryAbbAndOpName(String abb, String op);
	
	/**
	 * 获取所有记录
	 * @return 不为null
	 */
	public List<OperatorInfo> getAll();
}
