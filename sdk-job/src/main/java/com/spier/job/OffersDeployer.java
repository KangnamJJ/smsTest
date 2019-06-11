package com.spier.job;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.common.bean.db.CPICampaignInfo;
import com.spier.common.bean.db.CPIOfferInfo;
import com.spier.common.bean.db.task.SyncTaskInfo;
import com.spier.common.utils.BussinessCommonUtil;
import com.spier.service.ISyncTaskInfoService;
import com.spier.service.job.ICPICampaignInfoService;
import com.spier.service.job.ICPIOfferService;

/**
 * 根据同步到的campaign信息，生成对应的task。每天凌晨5点一次
 * @author GHB
 * @version 1.0
 * @date 2019.2.16
 */
@Component
public class OffersDeployer {

	// 每天凌晨5点
	private static final String M_TRIGGER_PATTERN = "0 0 5 * * ? ";
//	private static final String M_TRIGGER_PATTERN = "* */30 * * * ? ";
	
	@Reference
	private ICPICampaignInfoService mCPICampaignInfoService;
	
	@Reference
	private ICPIOfferService mCPIOfferService;
	
	@Reference
	private ISyncTaskInfoService mSyncTaskService;
	
	/**
	 * 部署任务
	 */
	@Scheduled(cron = M_TRIGGER_PATTERN)
	public void deployOffers() {
		Logger.getAnonymousLogger().log(Level.INFO, "开始部署同步任务……");
		
		// 取出所有的campaign
		List<CPICampaignInfo> allCampaigns = mCPICampaignInfoService.getAll();
		if(allCampaigns.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, "无campaign，停止部署同步任务！");
			return;
		}
		
		// 根据campaign生成SyncTask列表。在转换过程中，会根据offer的合法性，修改列表中的内容
		List<SyncTaskInfo> syncTaskList = convert2SyncTasks(allCampaigns);
		if(syncTaskList.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, "未生成同步任务列表，停止部署同步任务！");
			return;
		}
		
		// 更新synctask表
		if(!updateSyncTaskTable(syncTaskList)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "更新同步任务表失败！");
			return;
		}
		
		// 删除synctask表中，本集合未出现的task
		clearOutofDateTasks(syncTaskList);
		
		Logger.getAnonymousLogger().log(Level.INFO, "同步任务表已更新完毕！");
	}
	
	private List<SyncTaskInfo> convert2SyncTasks(List<CPICampaignInfo> list) {
		List<SyncTaskInfo> res = new ArrayList<SyncTaskInfo>();
		
		if(list == null || list.isEmpty()) {
			return res;
		}
		
		for(Iterator<CPICampaignInfo> it = list.iterator(); it.hasNext();) {
			CPICampaignInfo campNet = it.next();
			if(null == campNet) {
				continue;
			}
			
			String offerIdsStr = campNet.getOfferIds();
			if(StringUtils.isEmpty(offerIdsStr)) {
				Logger.getAnonymousLogger().log(Level.WARNING, MessageFormat.format(
						"campaign[{0}]中未发现任何offer！", campNet.getPkgName()));
				it.remove();
				continue;
			}
			
			List<String> offerIds = BussinessCommonUtil.getInstance().deserializeIdsStrign(offerIdsStr);
			if(offerIds.isEmpty()) {
				Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
						"campaign[{0}]中未计算出id列表！", campNet.getPkgName()));
				it.remove();
				continue;
			}
			
			List<Integer> ids = new ArrayList<Integer>();
			for(String idStr : offerIds) {
				if(StringUtils.isEmpty(idStr)) {
					continue;
				}
				
				try {
					ids.add(Integer.parseInt(idStr));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(ids.isEmpty()) {
				Logger.getAnonymousLogger().log(Level.SEVERE, "由字符串id转换int失败，无法入库！");
				continue;
			}
			
			List<CPIOfferInfo> offersList = mCPIOfferService.getOffersByIds(ids);
			if(offersList.isEmpty()) {
				Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
						"数据库中未查出任何与campaign[{0}]相关的offer", campNet.getPkgName()));
				it.remove();
				continue;
			}
			
			for(CPIOfferInfo offer : offersList) {
				if(null == offer) {
					continue;
				}
				
				SyncTaskInfo syncTask = new SyncTaskInfo(offer);
				syncTask.setCampaign(campNet.getCampaign());
				syncTask.setPkgName(campNet.getPkgName());
				syncTask.setDesc(campNet.getDesc());
				syncTask.setIconsIds(campNet.getIconIds());
				syncTask.setMaterialsIds(campNet.getMaterialIds());
				
				res.add(syncTask);
			}
		}
		
		return res;
	}
	
	private boolean updateSyncTaskTable(List<SyncTaskInfo> tasks) {
		if(null == tasks) {
			return false;
		}
		
		if(tasks.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, "列表为空，无需更新");
			return true;
		}
		
		mSyncTaskService.updateOrInsert(tasks);
		
		return true;
	}
	
	private void clearOutofDateTasks(List<SyncTaskInfo> tasks) {
		if(null == tasks) {
			return;
		}
		
		List<SyncTaskInfo> tasksInDB = mSyncTaskService.getAll();
		if(tasksInDB.isEmpty()) {
			return;
		}
		
		List<Integer> ids2Delete = new ArrayList<Integer>();
		for(SyncTaskInfo taskInDB : tasksInDB) {
			if(null == taskInDB) {
				continue;
			}
			
			boolean found = false;
			for(SyncTaskInfo taskInList : tasks) {
				if(null == taskInList) {
					continue;
				}
				
				if(taskInDB.getOfferId() == taskInList.getOfferId()) {
					found = true;
					break;
				}
			}
			
			if(!found) {
				ids2Delete.add(taskInDB.getOfferId());
			}
		}
		
		if(!ids2Delete.isEmpty()) {
			mSyncTaskService.deleteByIdsBatch(ids2Delete);
		}
	}
}
