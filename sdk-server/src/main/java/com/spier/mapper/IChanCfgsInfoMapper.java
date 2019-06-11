package com.spier.mapper;

import org.apache.ibatis.annotations.Param;

import com.spier.common.bean.db.ChanCfgsInfo;

/**
 * 渠道配置信息DB层映射类
 * @author GHB
 * @version 1.0
 * @date 2019.2.20
 */
public interface IChanCfgsInfoMapper {

	/**
	 * 根据渠道号获取配置信息
	 * @param tabName
	 * @param chanNo
	 * @return 可能为null
	 */
	public ChanCfgsInfo getByChanNo(@Param("tableName") String tabName, @Param("chan") String chanNo);
}
