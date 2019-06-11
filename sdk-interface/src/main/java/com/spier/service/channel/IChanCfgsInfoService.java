package com.spier.service.channel;

import java.util.Map;

import com.spier.service.IAutoInitTable;

/**
 * 渠道配置信息服务接口类
 * @author GHB
 * @version 1.0
 * @date 2019.2.20
 */
public interface IChanCfgsInfoService extends IAutoInitTable {

	/**
	 * 根据渠道号查询配置信息
	 * @param chanNo
	 * @return 不为null
	 */
	public Map<String, Boolean> getCfgsByChanNo(String chanNo);
}
