package com.spier.mapper.job;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spier.common.bean.db.CPIMaterialInfo;

/**
 * CPI素材信息映射接口
 * @author GHB
 * @version 1.0
 * @date 2019.2.14
 */
public interface ICPIMaterialInfoMapper {

	/**
	 * 批量更新或升级
	 * @param tabName
	 * @param list
	 */
	public void addOrUpdateBatch(@Param("tableName") String tabName, @Param("list") List<CPIMaterialInfo> list);
	
	/**
	 * 获取全部
	 * @param tabName
	 * @return 可能为null
	 */
	public List<CPIMaterialInfo> getAll(@Param("tableName") String tabName);
	
	/**
	 * 批量删除
	 * @param tabName
	 * @param list
	 */
	public void deleteByIdBatch(@Param("tableName") String tabName, @Param("list") List<Integer> list);
}
