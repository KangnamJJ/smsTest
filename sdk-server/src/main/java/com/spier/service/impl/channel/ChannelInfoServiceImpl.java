package com.spier.service.impl.channel;

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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.spier.common.bean.db.ChannelInfo;
import com.spier.common.config.RediskeyConstants;
import com.spier.common.utils.Objects;
import com.spier.config.cache.JedisClusterService;
import com.spier.mapper.IChannelInfoMapper;
import com.spier.service.channel.IChannelInfoService;

//Transactional: 表示数据库隔离级别为如果当前有就使用当前，如果没有就创建新的事务，
//隔离级别为：读已提交，也就是数据在写入的时候是无法被读的，只有提交后才能让其他事务读取，防止数据库发生脏读

/**
 * 渠道信息服务的实现类
 * @author GHB
 * @version 1.0
 * @date 2018.12.21
 */
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class ChannelInfoServiceImpl implements IChannelInfoService {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(ChannelInfoServiceImpl.class);
	
	@Autowired
	private IChannelInfoMapper mChannelInfoMapper;		//用AutoWired注入DB层
	
	@Autowired
	private JedisClusterService jedisClusterService;
	
	@Transactional(readOnly=true)	//数据库的读取方式为：只读
	@Override
	public List<ChannelInfo> getAll() {
		List<ChannelInfo> res = null;
		try {
			String key = RediskeyConstants.CHANNEL_INFO_KEY ; 
			String channelInfoStr = jedisClusterService.get(key);
			res = JSON.parseObject(channelInfoStr, new TypeReference<List<ChannelInfo>>(){});
			//缓存中获取不到，查询数据库，并保存在缓存中
			if(res == null || res.size()<0) {
				res = mChannelInfoMapper.getAll(M_TAB_NAME);
				if(res == null || res.size()<0) {
					logger.error("查询数据库取出所有的渠道数据，无数据");
					return new ArrayList<ChannelInfo>();
				}
				//缓存60分钟
				jedisClusterService.saveOrUpdate(key,3600, res);
			}
		} catch (Exception e) {
			logger.error("查询数据库取出所有的渠道数据报错",e);
		}
		/*res = mChannelInfoMapper.getAll(M_TAB_NAME);
		if(null == res) {
			res = new ArrayList<ChannelInfo>();
		}
		*/
		return res;
	}

	@Override
	public ChannelInfo getChanInfoByChanNo(String chanNo) {
		ChannelInfo channelInfoStr = null;
		try {
			String key = RediskeyConstants.CHANNEL_INFO_KEY + chanNo; 
			channelInfoStr = jedisClusterService.getValue(key,ChannelInfo.class);
			//缓存中获取不到，查询数据库，并保存在缓存中
			if(Objects.isEmpty(channelInfoStr)) {
				channelInfoStr = mChannelInfoMapper.getChanInfoByChanNo(M_TAB_NAME, chanNo);
				if(Objects.isEmpty(channelInfoStr)) {
					logger.error("根据chanNo:{}查询渠道数据，无数据",chanNo);
					return null;
				}
				//缓存60分钟
				jedisClusterService.saveOrUpdate(key, channelInfoStr);
			}
		} catch (Exception e) {
			logger.error("根据chanNo:{}查询渠道数据报错",chanNo,e);
		}
		//return mChannelInfoMapper.getChanInfoByChanNo(M_TAB_NAME, chanNo);
		return channelInfoStr;
	}
	
	@Override
	public String getRSAPrivKB64StrByChanNo(String chanNo) {
		if(StringUtils.isEmpty(chanNo)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "chanNo为空，无法查询RSA私钥！");
			return null;
		}
		String rsaPrivKStrByChanNo = null;
		try {
			String key = RediskeyConstants.RSA_PRIVATE_KEY + chanNo ; 
			rsaPrivKStrByChanNo = jedisClusterService.get(key);
			//缓存中获取不到，查询数据库，并保存在缓存中
			if(StringUtils.isBlank(rsaPrivKStrByChanNo)) {
				rsaPrivKStrByChanNo = mChannelInfoMapper.getRSAPrivKStrByChanNo(M_TAB_NAME, chanNo);
				if(StringUtils.isBlank(rsaPrivKStrByChanNo)) {
					logger.error("根据渠道号{}查询数据库私钥，无数据",chanNo);
					return null;
				}
				jedisClusterService.saveOrUpdate(key, rsaPrivKStrByChanNo);
			}
		} catch (Exception e) {
			logger.error("根据渠道号{}查询数据库私钥报错",chanNo,e);
		}
		return rsaPrivKStrByChanNo;
	}

	@Override
	public boolean insertChanInfo(ChannelInfo info) {
		if(null == info) {
			Logger.getAnonymousLogger().log(Level.WARNING, "渠道信息为null，插入失败！");
			return false;
		}
		
		boolean res = true;
		
		try {
			mChannelInfoMapper.insertChanInfo(M_TAB_NAME, info);
		} catch(Exception e) {
			e.printStackTrace();
			res = false;
		}
		
		return res;
	}
	
	@Override
	public boolean updateChanInfoByChanNo(String chNo, ChannelInfo info) {
		if(StringUtils.isEmpty(chNo)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "渠道号为空，更新失败！");
			return false;
		}
		
		if(null == info) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "info为null，更新失败！");
			return false;
		}
		
		mChannelInfoMapper.updateChanInfoByChanNo(getTableName(), chNo, info);
		
		return true;
	}

	@Override
	public ChannelInfo getChanInfoByIndex(int ind) {
		if(ind <= 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, 
					MessageFormat.format("ind[{0}]无效，无法获取渠道信息", ind));
			return null;
		}
		//return mChannelInfoMapper.getChanInfoByIndex(M_TAB_NAME, ind);
		ChannelInfo channelInfoStr = null;
		try {
			String key = RediskeyConstants.CHANNEL_INFO_KEY + ind; 
			channelInfoStr = jedisClusterService.getValue(key,ChannelInfo.class);
			//缓存中获取不到，查询数据库，并保存在缓存中
			if(Objects.isEmpty(channelInfoStr)) {
				channelInfoStr = mChannelInfoMapper.getChanInfoByIndex(M_TAB_NAME, ind);
				if(Objects.isEmpty(channelInfoStr)) {
					logger.error("根据ind:{}查询渠道数据，无数据",ind);
					return null;
				}
				jedisClusterService.saveOrUpdate(key, channelInfoStr);
			}
		} catch (Exception e) {
			logger.error("根据ind:{}查询渠道数据报错",ind,e);
		}
		return channelInfoStr;
	}
	
	@Override
	public boolean deleteByIndex(int ind) {
		if(ind <= 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format("序列号【{0}】无效，无法删除！", ind));
			return false;
		}
		
		mChannelInfoMapper.deleteByIndex(M_TAB_NAME, ind);
		
		return true;
	}
	
	@Override
	public boolean deleteByChanNo(String chanNo) {
		if(StringUtils.isEmpty(chanNo)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "chan-number为空，无法删除！");
			return false;
		}
		
		mChannelInfoMapper.deleteByChNo(M_TAB_NAME, chanNo);
		
		return true;
	}
	
	@Override
	public boolean isTaskSwitchOn(String chanNo) {
		if(StringUtils.isEmpty(chanNo)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "渠道号为空，无法查询任务开关状态");
			return false;
		}
		
		Integer res = mChannelInfoMapper.getChanTaskSwitchState(getTableName(), chanNo);
		if(null == res) {
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("渠道【{0}】不存在，无法查询任务开关状态", chanNo));
			return false;
		}
		
		return res == ChannelInfo.M_TASK_SWITCH_ON;
	}
	
	private static final String M_TAB_NAME = "channel_info";
	
	@Override
	public String getTableName() {
		return M_TAB_NAME;
	}

	private static final String M_SQL_TAB_CREATE = "_id bigint(20) NOT NULL AUTO_INCREMENT, " +
	          "chan_no varchar(255) NOT NULL, " +
	          "chan_desc varchar(255) NOT NULL, " +
	          "task_switch int(5), " +
	          "s_user_max_tasks int(5), " +
	          "postback varchar(255), " +
	          "rsa_pub_key_b64 varchar(1024) NOT NULL, " +
	          "rsa_priv_key_b64 varchar(2048) NOT NULL, " +
	          "create_time timestamp NOT NULL, " +
	          "PRIMARY KEY (_id)";
	
	@Override
	public String getCreateTableSql() {
		return M_SQL_TAB_CREATE;
	}
}
