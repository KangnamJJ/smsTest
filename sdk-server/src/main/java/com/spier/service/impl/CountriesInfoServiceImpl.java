package com.spier.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.spier.common.bean.db.CountryInfo;
import com.spier.common.config.RediskeyConstants;
import com.spier.config.cache.JedisClusterService;
import com.spier.mapper.ICountriesInfoMapper;
import com.spier.service.ICountriesInfoService;
import com.spier.service.impl.channel.ChannelInfoServiceImpl;

/**
 * 国家信息服务实现类
 * @author GHB
 * @version 1.0
 * @date 2019.1.10
 */
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class CountriesInfoServiceImpl implements ICountriesInfoService {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(CountriesInfoServiceImpl.class);

	private static final String M_TABLE_NAME = "countries_info";
	@Autowired
	private JedisClusterService jedisClusterService;
	@Autowired
	private ICountriesInfoMapper mCountriesInfoMapper;
	
	
	@Override
	public int countCountries() {
		return mCountriesInfoMapper.countCountriesAmount(M_TABLE_NAME);
	}

	@Override
	public void addCountry(CountryInfo country) {
		mCountriesInfoMapper.addCountry(M_TABLE_NAME, country);
	}

	@Override
	public List<CountryInfo> getAll() {
		List<CountryInfo> res = mCountriesInfoMapper.getAll(M_TABLE_NAME);
		if(null == res) {
			res = new ArrayList<CountryInfo>();
		}
		
		return res;
	}

	@Override
	public String getTableName() {
		return M_TABLE_NAME;
	}

	private static final String M_SQL_TAB1_CREATE = "_id bigint(20) NOT NULL AUTO_INCREMENT, " +
	          "abb varchar(255) NOT NULL, " +
	          "chiness varchar(255) NOT NULL, " +
	          "english varchar(255) NOT NULL, " +
	          "PRIMARY KEY (_id)";
	
	@Override
	public String getCreateTableSql() {
		return M_SQL_TAB1_CREATE;
	}

	@Override
	public boolean checkCountryExistsByAbb(String abb) {
		if(StringUtils.isEmpty(abb)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "国家简称为空，无法查询国家信息是否存在");
			return false;
		}
		
		return (mCountriesInfoMapper.getCountryByAbb(getTableName(), abb) != null);
	}

	@Override
	public String getCountryAbbByCountry(String country) {
		if(StringUtils.isEmpty(country)) {
			return country;
		}
		
		if(country.length() == 2) {
			return country.toUpperCase();
		}
		String res = null;
		try {
			String country_key = RediskeyConstants.COUNTRY_INFO_KEY + country ; 
			res = jedisClusterService.get(country_key);
			//缓存中获取不到，查询数据库，并保存在缓存中
			if(StringUtils.isBlank(res)) {
				res = mCountriesInfoMapper.getCountryAbb(getTableName(), country);
				if(StringUtils.isBlank(res)) {
					logger.error("根据country{}查询数据库，无数据",country);
					return country;
				}
				jedisClusterService.saveOrUpdate(country_key, res);
			}
		} catch (Exception e) {
			logger.error("根据country:{}查询数据库报错",country,e);
		}
		/*String res = mCountriesInfoMapper.getCountryAbb(getTableName(), country);
		if(res == null) {
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("未发现国家【{0}】的简称！", country));
			res = country;
		}*/
		return res;
	}

}
