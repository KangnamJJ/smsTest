package com.spier.mapper.job;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spier.common.bean.db.CPIOfferInfo;

/**
 * CPI Offers信息db映射接口
 * @author GHB
 * @version 1.0
 * @date 2019.2.13
 */
public interface ICPIOffersInfoMapper {

	/**
	 * 获取所有的
	 * @param tabName
	 * @return 可能为null
	 */
	public List<CPIOfferInfo> getAll(@Param("tableName") String tabName);
	
	/**
	 * 根据id批量获取
	 * @param tabName
	 * @param ids
	 * @return 可能为null
	 */
	public List<CPIOfferInfo> getOffersByIds(@Param("tableName") String tabName, @Param("ids") List<Integer> ids);
	
	/**
	 * 通过oid删除
	 * @param tabName
	 * @param oid
	 */
	public void deleteById(@Param("tableName") String tabName, @Param("id") int oid);
	
	/**
	 * 批量插入，插入时只插入未出现过的offer，根据offerid检查
	 * @param tabName
	 * @param offers
	 */
	public void batchInsertOffersOrUpdate(@Param("tableName") String tabName, @Param("offers") List<CPIOfferInfo> offers);
	
	/**
	 * 批量清理
	 * @param tabName
	 * @param offers
	 */
	public void batchClearOffers(@Param("tableName") String tabName, @Param("offerIds") List<Integer> offers);
}
