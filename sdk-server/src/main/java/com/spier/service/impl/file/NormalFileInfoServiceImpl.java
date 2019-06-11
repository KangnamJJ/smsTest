package com.spier.service.impl.file;

import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.spier.common.bean.db.file.NormalFileInfo;
import com.spier.common.config.RediskeyConstants;
import com.spier.common.utils.Objects;
import com.spier.config.cache.JedisClusterService;
import com.spier.mapper.file.INormalFileInfoMapper;
import com.spier.service.file.INormalFileService;

/**
 * 普通文件访问服务实现类
 * @author GHB
 * @version 1.0
 * @date 2018.12.29
 */
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class NormalFileInfoServiceImpl implements INormalFileService {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(NormalFileInfoServiceImpl.class);
	@Autowired
	private JedisClusterService jedisClusterService;
	@Autowired
	private INormalFileInfoMapper mMapper;
	
	@Override
	public void insertNormalFileInfo(NormalFileInfo info) {
		if(null == info) {
			Logger.getAnonymousLogger().log(Level.WARNING, "info为null，无法执行插入操作");
			return;
		}
		
		mMapper.insertFileInfo(getTableName(), info);
	}

	@Override
	public String findFilePathById(String id) {
		if(StringUtils.isEmpty(id)) {
			Logger.getAnonymousLogger().log(Level.WARNING, "id为空，无法执行查询操作！");
			return null;
		}
		/*
		NormalFileInfo record = mMapper.findFileRecordById(getTableName(), id);
		if(null == record) {
			return null;
		}
		return record.getFilePath();
		*/
		NormalFileInfo record = getFileRecordById(id);
		return Objects.isEmpty(record) ? null : record.getFilePath();
	}

	private NormalFileInfo getFileRecordById(String id) {
		NormalFileInfo record = null;
		try {
			String key = RediskeyConstants.NORAML_FILE_INFO_KEY + id ; 
			record = jedisClusterService.getValue(key,NormalFileInfo.class);
			//缓存中获取不到，查询数据库，并保存在缓存中
			if(Objects.isEmpty(record)) {
				record = mMapper.findFileRecordById(getTableName(), id);
				if(Objects.isEmpty(record)) {
					logger.error("根据文件id{}查询，无数据",id);
					return null;
				}
				jedisClusterService.saveOrUpdate(key, record);
			}
		} catch (Exception e) {
			logger.error("根据文件id{}查询报错",id,e);
		}
		return record;
	}
	
	
	private NormalFileInfo getFileRecordByInd(int ind) {
		NormalFileInfo record = null;
		try {
			String key = RediskeyConstants.NORAML_FILE_INFO_KEY + ind ; 
			record = jedisClusterService.getValue(key,NormalFileInfo.class);
			//缓存中获取不到，查询数据库，并保存在缓存中
			if(Objects.isEmpty(record)) {
				record = mMapper.findFileRecordByInd(getTableName(), ind);
				if(Objects.isEmpty(record)) {
					logger.error("根据文件序列号ind{}查询，无数据",ind);
					return null;
				}
				jedisClusterService.saveOrUpdate(key, record);
			}
		} catch (Exception e) {
			logger.error("根据文件序列号ind{}查询报错",ind,e);
		}
		return record;
	}

	@Override
	public boolean checkFileRecordExistsById(String id) {
		if(StringUtils.isEmpty(id)) {
			Logger.getAnonymousLogger().log(Level.WARNING, "id为空，无法执行查询操作！");
			return false;
		}
		
		NormalFileInfo record = getFileRecordById(id);
		//NormalFileInfo record = mMapper.findFileRecordById(getTableName(), id);
		
		return record != null;
	}
	
	@Override
	public boolean checkFileRecordExistsByInd(int ind) {
		//return mMapper.findFileRecordByInd(getTableName(), ind) != null;
		NormalFileInfo fileRecordByInd = getFileRecordByInd(ind);
        return fileRecordByInd !=null ; 	
	}

	@Override
	public void updateNormalFileInfoByInd(int ind, NormalFileInfo info) {
		if(null == info) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "info为空， 无法执行更新操作！");
			return;
		}
		
		if(ind < 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, 
					MessageFormat.format("ind[{0}]不合法，无法执行更新操作！", ind));
			return;
		}
		
		mMapper.updateFileInfoByInd(getTableName(), ind, info);
	}

	@Override
	public List<NormalFileInfo> getAllFilesInfo() {
		//return mMapper.getAll(getTableName());
		List<NormalFileInfo> res = null;
		try {
			String normalFileInfo_key = RediskeyConstants.NORAML_FILE_INFO_KEY ; 
			String normalFileInfoStr = jedisClusterService.get(normalFileInfo_key);
			res = JSON.parseObject(normalFileInfoStr, new TypeReference<List<NormalFileInfo>>(){});
			//缓存中获取不到，查询数据库，并保存在缓存中
			if(res == null || res.size()<0) {
				res = mMapper.getAll(getTableName());
				if(res == null || res.size()<0) {
					logger.error("获取所有文件记录信息,无数据");
					return null;
				}
				jedisClusterService.saveOrUpdate(normalFileInfo_key, res);
			}
		} catch (Exception e) {
			logger.error("获取所有文件记录信息报错",e);
		}
		return res;
	}

	@Override
	public NormalFileInfo findFileRecordByInd(int ind) {
		if(ind < 0) {
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("ind[{0}]不合法，无法查询！", ind));
			return null;
		}
		//return mMapper.findFileRecordByInd(getTableName(), ind);
		NormalFileInfo fileRecordByInd = getFileRecordByInd(ind);
		return fileRecordByInd;
	}

	@Override
	public NormalFileInfo findFileRecordById(String id) {
		if(StringUtils.isEmpty(id)) {
			Logger.getAnonymousLogger().log(Level.INFO, "id为空，无法查询！");
			return null;
		}
		NormalFileInfo record = getFileRecordById(id);
		return record;
		//return mMapper.findFileRecordById(getTableName(), id);
	}

	@Override
	public void deleteRecordById(String id) {
		if(StringUtils.isEmpty(id)) {
			Logger.getAnonymousLogger().log(Level.INFO, "id为空，无法删除！");
			return;
		}
		
		mMapper.deleteFileInfoById(getTableName(), id);
	}

	private static final String M_TAB_NAME = "normal_file_info";
	
	@Override
	public String getTableName() {
		return M_TAB_NAME;
	}

	private static final String M_SQL_TAB1_CREATE = 
			"_id bigint(20) NOT NULL AUTO_INCREMENT, " +
	          "file_id varchar(255) NOT NULL, " +
	          "file_desc varchar(255), " +
	          "file_ver int, " +
	          "file_hash varchar(255), " +
	          "file_path varchar(255), " +
	          "file_url varchar(255), " +
	          "PRIMARY KEY (_id)";
	
	@Override
	public String getCreateTableSql() {
		return M_SQL_TAB1_CREATE;
	}
}
