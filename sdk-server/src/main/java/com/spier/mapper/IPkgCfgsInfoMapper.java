package com.spier.mapper;

import org.apache.ibatis.annotations.Param;

import com.spier.common.bean.db.PackageCfgsInfo;

/**
 * 包配置信息DB层映射类
 * @author GHB
 * @version 1.0
 * @date 2019.2.20
 */
public interface IPkgCfgsInfoMapper {

	/**
	 * 根据包名查询配置信息
	 * @param tabName
	 * @param pkgName
	 * @return 可能为null
	 */
	public PackageCfgsInfo getCfgsByPackageName(@Param("tableName") String tabName, @Param("pkgName") String pkgName);
}
