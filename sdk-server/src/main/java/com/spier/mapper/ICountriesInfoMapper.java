/**
 * 国家信息
 * @author GHB
 * @version 1.0
 * @date 2019.1.10
 */
package com.spier.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spier.common.bean.db.CountryInfo;

/**
 * 国家信息映射类
 * @author GHB
 * @version 1.0
 * @date 2019.1.10
 */
public interface ICountriesInfoMapper {

	/**
	 * 添加国家信息
	 * @param tabName 表名称
	 * @param info
	 */
	public void addCountry(@Param("tableName") String tabName, @Param("info") CountryInfo info);
	
	/**
	 * 通过简称查询国家信息
	 * @param abb
	 * @return 可能为null
	 */
	public CountryInfo getCountryByAbb(@Param("tableName") String tabName, @Param("abb") String abb);
	
	/**
	 * 通过国家查询其简称
	 * @param tabName
	 * @param country
	 * @return 可能为null
	 */
	public String getCountryAbb(@Param("tableName") String tabName, @Param("country") String country);
	
	/**
	 * 统计共有多少条数据
	 * @param tabName
	 * @return
	 */
	public int countCountriesAmount(@Param("tableName") String tabName);
	
	/**
	 * 获取所有的
	 * @return
	 */
	public List<CountryInfo> getAll(@Param("tableName") String tabName);
}
