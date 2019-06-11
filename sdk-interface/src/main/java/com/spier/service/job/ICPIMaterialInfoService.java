package com.spier.service.job;

import java.util.List;

import com.spier.common.bean.db.CPIMaterialInfo;
import com.spier.service.IAutoInitTable;

/**
 * CPI Offer素材DB服务接口
 * @author GHB
 * @version 1.0
 * @date 2019.2.14
 */
public interface ICPIMaterialInfoService extends IAutoInitTable {

	/**
	 * 批量插入或更新
	 * @param items
	 * @return 操作结果
	 */
	public boolean addOrUpdateBatch(List<CPIMaterialInfo> items);
	
	/**
	 * 获取全部
	 * @return 不为null
	 */
	public List<CPIMaterialInfo> getAll();
	
	/**
	 * 根据id批量删除
	 * @param ids
	 * @return 操作结果
	 */
	public boolean deleteByIdBatch(List<Integer> ids);
}
