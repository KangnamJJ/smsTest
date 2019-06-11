/**
 * 
 */
package com.spier.mapper.spot;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.spier.entity.SpotInfo;

/**
 * 埋点数据映射接口
 * @author GHB
 * @version 1.0
 * @date 2019.1.18
 */
@Mapper
public interface ISpotInfoMapper {

	/**
	 * 添加记录
	 * @param tabName
	 * @param spot
	 */
	public void addRecord(@Param("tableName") String tabName, @Param("spot") SpotInfo spot);
	
	/**
	 * 更新记录
	 * @param tabName
	 * @param spot
	 */
	public void updateRecord(@Param("tableName") String tabName, @Param("spot") SpotInfo spot);
	
	/**
	 * 根据关键条件获取埋点列表
	 * @param tabName
	 * @param uid
	 * @param chanNo
	 * @return 可能为null
	 */
	public List<SpotInfo> getSpotsByKeyUser(@Param("tableName") String tabName, @Param("uid") String uid,
			@Param("chanNo") String chanNo);

	/**
	 * 根据条件获取所有数据
	 * @param args
	 * @return
	 */
	public List<SpotInfo> getSpotsByCondition(Map<String, Object> args);

	/**
	 * 取一页数据
	 * @param tabName
	 * @param from
	 * @param pageLimit
	 * @return 可能为null
	 */
	public List<SpotInfo> getSpotsLimit(@Param("tableName") String tabName, 
			@Param("from") int from, @Param("pageLimit") int pageLimit);
	
	/**
	 * 获取数量
	 * @param tabName
	 * @return 可能为null
	 */
	public Integer getSpotsAmount(@Param("tableName") String tabName);
	
	/**
	 * 根据关键信息查询埋点
	 * @param tabName
	 * @param uid
	 * @param chanNo
	 * @param fid
	 * @return 可能为null
	 */
	public SpotInfo getSpotByKeyConds(@Param("tableName") String tabName, @Param("uid") String uid,
			@Param("chanNo") String chanNo, @Param("fid") String fid);
	
	/**
	 * 删除
	 * @param tabName
	 * @param ind
	 */
	public void deleteByInd(@Param("tableName") String tabName, @Param("ind") int ind);
}
