package com.spier.service.job;

import java.util.List;

import com.spier.common.bean.db.CPIOfferInfo;
import com.spier.service.IAutoInitTable;

/**
 * CPI offer服务接口
 * @author GHB
 * @version 1.0
 * @date 2019.2.12
 */
public interface ICPIOfferService extends IAutoInitTable {

	/**
	 * 获取所有的
	 * @return 不为null
	 */
	public List<CPIOfferInfo> getAll();
	
	/**
	 * 根据id批量获取
	 * @param ids
	 * @return 不为null
	 */
	public List<CPIOfferInfo> getOffersByIds(List<Integer> ids);
	
	/**
	 * 通过offerid删除
	 * @param oid
	 */
	public void deleteById(int oid);
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public boolean batchDeleteByIds(List<Integer> ids);
	
	/**
	 * 将offer数据存入数据库
	 * @return 操作结果
	 */
	public boolean saveCPIOffers(List<CPIOfferInfo> offers);
	
}
