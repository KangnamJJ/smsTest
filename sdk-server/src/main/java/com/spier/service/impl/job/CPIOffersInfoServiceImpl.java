package com.spier.service.impl.job;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.spier.common.bean.db.CPIOfferInfo;
import com.spier.mapper.job.ICPIOffersInfoMapper;
import com.spier.service.job.ICPIOfferService;

/**
 * CPI Offers信息服务实现类
 * @author GHB
 * @version 1.0
 * @date 2019.2.13
 */
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
@Service
public class CPIOffersInfoServiceImpl implements ICPIOfferService {

	@Autowired
	private ICPIOffersInfoMapper mMapper;
	
	@Override
	public boolean saveCPIOffers(List<CPIOfferInfo> offers) {
		if(null == offers || offers.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "无offer数据，停止新增/更新！");
			return false;
		}
		
		// 新增没有的
		mMapper.batchInsertOffersOrUpdate(getTableName(), offers);
		
		// 清理集合中没有的
//		clearOffersNotIn(offers);
		
		return true;
	}
	
	private boolean clearOffersNotIn(List<CPIOfferInfo> offers) {
		if(null == offers) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "offers为null，无法执行清理操作");
			return false;
		}
		
		if(offers.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.WARNING, "offers为空，将会清理所有数据！");
		}
		
		List<CPIOfferInfo> allOffers = mMapper.getAll(getTableName());
		if(allOffers.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, "数据库中没有offer，无需清除");
			return true;
		}
		
		List<Integer> offersToBeCleared = new ArrayList<Integer>();
		for(CPIOfferInfo offerInDB : allOffers) {
			if(null == offerInDB) {
				continue;
			}
			
			boolean exists = false;
			for(CPIOfferInfo offerInParam : offers) {
				if(null == offerInParam) {
					continue;
				}
				
				if(offerInDB.mOfferId == offerInParam.mOfferId) {
					exists = true;
					break;
				}
			}
			
			if(!exists) {
				offersToBeCleared.add(offerInDB.mOfferId);
			}
		}
		
		if(offersToBeCleared.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, "没有需要清理的offer");
			return true;
		}
		
		return batchDeleteByIds(offersToBeCleared);
	}
	
	@Override
	public void deleteById(int oid) {
		mMapper.deleteById(getTableName(), oid);
	}

	@Override
	public boolean batchDeleteByIds(List<Integer> ids) {
		if(null == ids) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "ids为null，无法批量删除！");
			return false;
		}
		
		if(ids.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, "ids为空，无需批量删除");
			return true;
		}
		
		mMapper.batchClearOffers(getTableName(), ids);
		
		return true;
	}
	
	@Override
	public List<CPIOfferInfo> getAll() {
		List<CPIOfferInfo> res = mMapper.getAll(getTableName());
		if(null == res) {
			res = new ArrayList<CPIOfferInfo>();
		}
		
		return res;
	}
	
	@Override
	public List<CPIOfferInfo> getOffersByIds(List<Integer> ids) {
		List<CPIOfferInfo> res = new ArrayList<CPIOfferInfo>();
		if(null == ids || ids.isEmpty()) {
			return res;
		}
		
		res = mMapper.getOffersByIds(getTableName(), ids);
		if(null == res) {
			res = new ArrayList<CPIOfferInfo>();
		}
		
		return res;
	}

	@Override
	public String getTableName() {
		return "cpi_offers";
	}

	private static final String M_SQL_TAB_CREATE = 
			"offer_id integer NOT NULL, " +
	          "offer_name varchar(255), " +
	          "offer_cate varchar(127), " +
	          "offer_desc varchar(511), " +
	          "version tinytext, " +
	          "incent tinytext, " +
	          "track_link varchar(255), " +
	          "preview_link varchar(255), " +
	          "flow varchar(127), " +
	          "market varchar(127), " +
	          "need_dev_info tinytext, " +
	          "currency tinytext, " +
	          "platforms varchar(63), " +
	          "countries varchar(63), " +
	          "payout double, " +
	          "payout_type varchar(63), " +
	          "cap int(12), " +
	          "allow_net_types varchar(63), " +
	          "forbid_net_types varchar(63), " +
	          "sub_aff_black_list varchar(255), " +
	          "create_time timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP, " +
	          "change_time timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP, " +
	          "PRIMARY KEY (offer_id)";
	
	@Override
	public String getCreateTableSql() {
		return M_SQL_TAB_CREATE;
	}

}
