package com.spier.service;

import java.util.Map;

/**
 * 包配置信息服务接口类
 * @author GHB
 * @version 1.0
 * @date 2019.2.20
 */
public interface IPkgCfgsInfoService extends IAutoInitTable {

	/**
	 * 根据包名获取配置信息
	 * @param pkgName
	 * @return 不为null
	 */
	public Map<String, Boolean> getCfgsByPackageName(String pkgName);
}
