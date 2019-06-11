package com.spier.service;

import java.util.List;

import com.spier.common.bean.db.CountryInfo;

/**
 * 国家信息服务接口
 * @author GHB
 * @version 1.0
 * @date 2019.1.10
 */
public interface ICountriesInfoService extends IAutoInitTable {

	/**
	 * 统计有多少条记录
	 * @return
	 */
	public int countCountries();
	
	/**
	 * 添加信息
	 * @param country
	 */
	public void addCountry(CountryInfo country);
	
	/**
	 * 获取所有的
	 * @return 不为null
	 */
	public List<CountryInfo> getAll();
	
	/**
	 * 通过国家简称查询国家信息是否存在
	 * @param abb
	 * @return
	 */
	public boolean checkCountryExistsByAbb(String abb);
	
	/**
	 * 根据国家名称查询国家简称
	 * @param country
	 * @return 不为null
	 */
	public String getCountryAbbByCountry(String country);
}
