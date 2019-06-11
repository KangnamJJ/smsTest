package com.spier.service.job;

import java.util.List;

import com.spier.common.bean.db.CPICampaignInfo;
import com.spier.service.IAutoInitTable;

/**
 * CPI Campaign信息服务接口
 * @author GHB
 * @version 1.0
 * @date 2019.2.14
 */
public interface ICPICampaignInfoService extends IAutoInitTable {

	/**
	 * 批量插入或更新
	 * @param campaigns
	 * @return 操作结果
	 */
	public boolean addOrUpdateCampaignsBatch(List<CPICampaignInfo> campaigns);
	
	/**
	 * 根据id批量删除
	 * @param ids
	 * @return 操作结果
	 */
	public boolean deleteCampaignsByIdBatch(List<String> ids);
	
	/**
	 * 获取所有的
	 * @return 不为null
	 */
	public List<CPICampaignInfo> getAll();
}
