package com.spier.listener;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import com.spier.common.bean.db.CountryInfo;
import com.spier.common.bean.db.OperatorInfo;
import com.spier.common.config.GlobalConfig;
import com.spier.service.BussinessCommonService;
import com.spier.service.IAutoInitTable;
import com.spier.service.ICountriesInfoService;
import com.spier.service.IOperatorsInfoService;
import com.spier.service.IPhoneInfoService;
import com.spier.service.IPkgCfgsInfoService;
import com.spier.service.ISimInfoService;
import com.spier.service.ISyncTaskInfoService;
import com.spier.service.IUserInfoService;
import com.spier.service.channel.IChanCfgsInfoService;
import com.spier.service.channel.IChannelInfoService;
import com.spier.service.file.INormalFileService;
import com.spier.service.job.ICPICampaignInfoService;
import com.spier.service.job.ICPIMaterialInfoService;
import com.spier.service.job.ICPIOfferService;
import com.spier.service.referrer.IReferrerService;
import com.spier.service.spot.ISpotInfoService;
import com.spier.service.table.ITableManagerService;
import com.spier.service.task.IScriptInfoService;
import com.spier.service.task.ISyncTaskStatisticsService;
import com.spier.service.task.ITaskInfoService;
import com.spier.service.task.ITaskStatisticsInfoService;


/**
 * 程序启动监听，在这里进行初始化工作
 * @author GHB
 * @version 1.0
 * @date 2019.1.10
 */
@Component
public class StartupListener implements ApplicationContextAware,
 ServletContextAware, InitializingBean, ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		Logger.getAnonymousLogger().log(Level.INFO, "afterPropertiesSet");
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		// TODO Auto-generated method stub
		Logger.getAnonymousLogger().log(Level.INFO, "setServletContext");
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		Logger.getAnonymousLogger().log(Level.INFO, 
				MessageFormat.format("程序启动，版本号：{0}", GlobalConfig.M_VERSION));
		
		// 初始化工具类
		//initCommonUtil();
		
		// 组件加载完成了，在这里进行初始化工作
		initPaths();
		
		// 加载数据文件
		initFromFiles();
		
		// 注册数据相关服务
		registerTableInfo();
		
		// 初始化表
		initTables();
	}
	
	/*private void initCommonUtil() {
		BussinessCommonUtil.getInstance().setCountriesInfoService(mCountriesInfoService);
		BussinessCommonUtil.getInstance().setOperatorInfoService(mOperatorsInfoService);
		BussinessCommonUtil.getInstance().setScriptInfoService(mScriptInfoService);
	}*/

	private String mResourcesDir;
	
	private void initPaths() {
		mResourcesDir = this.getClass().getClassLoader().getResource("").getPath()+"file";
	}
	
	private String getResourcesDir() {
		return mResourcesDir;
	}
	
	private String mCountriesFileName = "countries.txt";
	
	private String getCountriesFileName() {
		return mCountriesFileName;
	}
	
	private String mOperatorsFileName = "operators-tab.txt";
	
	private String getOperatorsFileName() {
		return mOperatorsFileName;
	}
	
	private void initFromFiles() {
		initCountriesFile();
		initOperatorsFile();
	}
	
	@Autowired
	private ITableManagerService mTableManagerService;
	
	@Autowired
	private ICountriesInfoService mCountriesInfoService;
	
	@Autowired
	private IOperatorsInfoService mOperatorsInfoService;
	
	@Autowired	
	private IChannelInfoService mChanInfoService;
	
	@Autowired	
	private INormalFileService mNormalFileService;
	
	@Autowired	
	private IPhoneInfoService mPhoneInfoService;
	
	@Autowired	
	private IScriptInfoService mScriptInfoService;
	
	@Autowired	
	private ISimInfoService mSimInfoService;
	
	@Autowired	
	private ITaskInfoService mTasksInfoService;
	
	@Autowired	
	private IUserInfoService mUserInfoService;
	
	@Autowired	
	private ITaskStatisticsInfoService mTaskStatisticsInfoService;
	
	@Autowired	
	private ISpotInfoService mSpotInfoService;
	
	@Autowired	
	private ICPIOfferService mCPIOfferInfoService;
	
	@Autowired	
	private ICPIMaterialInfoService mCPIMaterialService;
	
	@Autowired	
	private ICPICampaignInfoService mCPICampaignService;
	
	@Autowired	
	private ISyncTaskInfoService mSyncTaskService;
	
	@Autowired	
	private ISyncTaskStatisticsService mSyncTaskStatisticsService;
	
	@Autowired	
	private IChanCfgsInfoService mChanCfgsService;
	
	@Autowired	
	private IPkgCfgsInfoService mPkgCfgsService;
	
	@Autowired	
	private IReferrerService mReferrerService;
	
	private static final String M_ITEM_SPLITER = "---";
	private List<CountryInfo> mCountries = new ArrayList<CountryInfo>();
	
	private List<CountryInfo> getCountries() {
		return mCountries;
	}
	
	private void initCountriesFile() {
		Logger.getAnonymousLogger().log(Level.INFO, "开始加载国家配置文件……");
		
		// 检查文件是否存在，如果存在则加载进内存，插入数据库
		String countriesFile = getResourcesDir() + File.separator + getCountriesFileName();
		List<String> lines = null;
		try {
			lines = FileUtils.readLines(new File(countriesFile), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(null == lines) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "未从国家配置文件中读到内容！！");
			return;
		}
		
		List<CountryInfo> countries = new ArrayList<CountryInfo>();
		for(String line : lines) {
			String[] items = line.split(M_ITEM_SPLITER);
			if(items.length < 3) {
				Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("行格式有误：【{0}】", line));
				continue;
			}
			
			CountryInfo country = new CountryInfo();
			country.setAbbrevation(items[0]);
			country.setChiness(items[1]);
			country.setEnglish(items[2]);
			
			countries.add(country);
		}
		
		if(!countries.isEmpty()) {
			mCountries = countries;
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, "国家配置文件加载完成！");
	}
	
	private List<OperatorInfo> mOperators = new ArrayList<OperatorInfo>();
	
	private List<OperatorInfo> getOperators() {
		return mOperators;
	}
	
	private void initOperatorsFile() {
		Logger.getAnonymousLogger().log(Level.INFO, "开始加载运营商配置文件……");
		
		String countriesFile = getResourcesDir() + File.separator + getOperatorsFileName();
		List<String> lines = null;
		try {
			lines = FileUtils.readLines(new File(countriesFile), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(null == lines) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "未从国家配置文件中读到内容！！");
			return;
		}
		
		List<OperatorInfo> operators = new ArrayList<OperatorInfo>();
		for(String line : lines) {
			if(StringUtils.isEmpty(line)) {
				continue;
			}
			
			String[] slices = line.split(M_ITEM_SPLITER);
			if(slices.length != 3) {
				Logger.getAnonymousLogger().log(Level.WARNING, 
						MessageFormat.format("文本行【{0}】格式不对，无法解析运营商！", line));
				continue;
			}
			
			String opCode = slices[0];
			if(opCode.length() != 5) {
				Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
						"opCode【{0}】格式错误，无法解析MCC和MNC", opCode));
				continue;
			}
			String mcc = opCode.substring(0, 3);
			String mnc = opCode.substring(3);
			String op = slices[1];
			String countryAbb = slices[2];
			
			OperatorInfo info = new OperatorInfo();
			info.setCountryAbb(countryAbb);
			info.setOperatorName(op);
			info.setMCC(mcc);
			info.setMNC(mnc);
			
			operators.add(info);
		}
		
		if(!operators.isEmpty()) {
			mOperators = operators;
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, "运营商配置文件加载完成！");
	}
	
	private List<IAutoInitTable> mTablesInfo = new ArrayList<IAutoInitTable>();
	
	private void registerTableInfo() {
		mTablesInfo.add(mCountriesInfoService);
		mTablesInfo.add(mOperatorsInfoService);
		mTablesInfo.add(mChanInfoService);
		mTablesInfo.add(mNormalFileService);
		mTablesInfo.add(mPhoneInfoService);
		mTablesInfo.add(mScriptInfoService);
		mTablesInfo.add(mSimInfoService);
		mTablesInfo.add(mTasksInfoService);
		mTablesInfo.add(mUserInfoService);
		mTablesInfo.add(mTaskStatisticsInfoService);
		mTablesInfo.add(mSpotInfoService);
		mTablesInfo.add(mCPIOfferInfoService);
		mTablesInfo.add(mCPIMaterialService);
		mTablesInfo.add(mCPICampaignService);
		mTablesInfo.add(mSyncTaskService);
		mTablesInfo.add(mSyncTaskStatisticsService);
		mTablesInfo.add(mChanCfgsService);
		mTablesInfo.add(mPkgCfgsService);
		mTablesInfo.add(mReferrerService);
	}
	
	private List<IAutoInitTable> getTableInfo() {
		return mTablesInfo;
	}
	
	private void initTables() {
		// 创建表
		//createTables();
		
		// 初始化国家表
		initCountiresTab();
		
		// 初始化运营商表
		initOperatorsTab();
		
		// 最后清除内存中的数据
		clearMem();
	}
	
	private void createTables() {
		Logger.getAnonymousLogger().log(Level.INFO, "创建表……");
		
		for(IAutoInitTable table : getTableInfo()) {
			if(null == table) {
				continue;
			}
			
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("创建表【{0}】", table.getTableName()));
			
			// 检查数据库表，没有则创建
			if(!mTableManagerService.checkTableExists(table.getTableName())) {
				Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("表【{0}】不存在，开始创建……", table.getTableName()));
				
				// 创建表
				if(!mTableManagerService.createTable(
						table.getTableName(), table.getCreateTableSql())) {
					Logger.getAnonymousLogger().log(Level.SEVERE, 
							MessageFormat.format("表【{0}】创建失败！", table.getTableName()));
					continue;
				}
			}
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, "创建表完成！");
	}
	
	private void initCountiresTab() {
		Logger.getAnonymousLogger().log(Level.INFO, "初始化国家表……");
		
		if(getCountries().isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, "无数据，停止初始化国家表……");
			return;
		}
		
		// 更新表
		if(mCountriesInfoService.countCountries() != mCountries.size()) {
			// 清空表
			if(!mTableManagerService.clearTable(mCountriesInfoService.getTableName())) {
				Logger.getAnonymousLogger().log(Level.SEVERE, "国家表清空失败，无法初始化国家表");
				return;
			}
			
			// 插入
			for(CountryInfo country : getCountries()) {
				if(null == country) {
					continue;
				}
				
				mCountriesInfoService.addCountry(country);
			}
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, "国家表初始化完成");
	}
	
	private void initOperatorsTab() {
		Logger.getAnonymousLogger().log(Level.INFO, "初始化运营商表……");
		
		if(getOperators().isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, "无数据，停止初始化运营商表……");
			return;
		}

		int count = mOperatorsInfoService.getOperatorsAmount();
		int txtCount = getOperators().size();
		if(count != txtCount) {
			Logger.getAnonymousLogger().log(Level.INFO, "发现运营商信息有更新，更新运营商信息表……");
			// 清空表
			if(!mTableManagerService.clearTable(mOperatorsInfoService.getTableName())) {
				Logger.getAnonymousLogger().log(Level.SEVERE, "运营商表清空失败，无法更新运营商信息表！");
				return;
			}
			
			if(!mOperatorsInfoService.addOperatorsBatch(getOperators())) {
				Logger.getAnonymousLogger().log(Level.SEVERE, "运营商表更新失败！");
				return;
			}
			
			Logger.getAnonymousLogger().log(Level.INFO, "运营商信息更新完成！");
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, "运营商表初始化完成");
	}
	
	private void clearMem() {
		getCountries().clear();
		getOperators().clear();
	}

}
