package com.spier.job;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.common.bean.db.CPICampaignInfo;
import com.spier.common.bean.db.CPIMaterialInfo;
import com.spier.common.bean.db.CPIOfferInfo;
import com.spier.common.bean.net.CPIOfferBeans;
import com.spier.common.config.GlobalConfig;
import com.spier.common.http.HttpCPIOfferRequest;
import com.spier.common.http.HttpCtx;
import com.spier.common.http.HttpMode;
import com.spier.service.job.ICPICampaignInfoService;
import com.spier.service.job.ICPIMaterialInfoService;
import com.spier.service.job.ICPIOfferService;

/**
 * 与上游同步Offer获取任务
 * @author GHB
 * @version 1.0
 * @date 2019.2.13
 */
@Component
public class OffersCarrier {

	// 每5分钟取一次
	private static final String M_TRIGGER_PATTERN = "0 */10 * * * ? ";
	
	// 暂时写死，后面要改成可以在管理后台设置
	private static final String M_TOKEN = "cdd86e6d1b0ee7d81591e81ef3b34d79";
	private static final int M_AFF_ID = 28;
	
	@Reference
	private ICPICampaignInfoService mCPICampaignInfoService;
	
	@Reference
	private ICPIOfferService mOfferService;
	
	@Reference
	private ICPIMaterialInfoService mCPIMaterialService;
	
	@Scheduled(cron = M_TRIGGER_PATTERN)
	public void collectOffers() {
		CPIOfferBeans.Request requestData = new CPIOfferBeans.Request();
		requestData.mAffiliateId = M_AFF_ID;
		requestData.mToken = M_TOKEN;
		
		HttpCtx ctx = HttpCtx.getDefault();
		ctx.setHttpMode(HttpMode.Get);
		
		HttpCPIOfferRequest request = new HttpCPIOfferRequest(requestData);
		request.access(ctx);
		
		if(!request.isSucceed()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"请求CPI Offers失败：{0}", request.getErrorStr()));
			return;
		}
		
		if(StringUtils.isEmpty(request.getHtml())) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "请求CPI Offers成功，但是无数据返回。");
			return;
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("收到的CPI数据：{0}", request.getHtml()));
		
		// 反序列化
		CPIOfferBeans.Response response = CPIOfferBeans.Response.fromJsonStr(request.getHtml());
		if(null == response) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "响应反序列化失败，请求CPI Offers失败！");
			return;
		}
		
		if(response.mStatus != GlobalConfig.M_TASK_REQUEST_STATUS_SUCCEED) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"CPI Offers请求失败，状态码【{0}】", response.mStatus));
			return;
		}
		
		if(response.mCount <= 0) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"任务数量不合法【{0}】", response.mCount));
			return;
		}
		
		// 更新offer信息，返回值是每一个Campaign的offerids，经过升序排列的
		List<String> offers = updateOffers(response);
		// 更新icon素材，key是根据offers的id组合成的offersid，与后续的Campaign中的相同。value是素材的id组合，与后续的Campaign中相同
		Map<String, String> icons = updateIcons(response);
		// 更新materials素材，key是根据offers的id组合成的offersid，与后续的Campaign中的相同。value是素材的id组合，与后续的Campaign中相同
		Map<String, String> materials = updateMaterials(response);
		// 更新Campaign
		updateCampaigns(response, offers, icons, materials);
		// 清理未出现过的campaigns+offers+素材
		cleanNotInResponse(response);
		
		Logger.getAnonymousLogger().log(Level.INFO, "更新CPI Offers信息操作完成！");
	}
	
	private List<String> updateOffers(CPIOfferBeans.Response response) {
		List<String> res = new ArrayList<String>();
		if(response.mCampaigns == null || response.mCampaigns.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, "响应中无offer信息，无需更新");
			return res;
		}
		
		int campaignCount = Math.max(response.mCount, response.mCampaigns.size());
		for(int i = 0; i < campaignCount; i++) {
			CPIOfferBeans.Campaign camp = response.mCampaigns.get(i);
			if(null == camp) {
				continue;
			}
			
			if(StringUtils.isEmpty(camp.mPkgName)) {
				Logger.getAnonymousLogger().log(Level.WARNING, "campaign缺失包名，offers不予入库！");
				continue;
			}
			
			if(null == camp.mOffers || camp.mOffers.isEmpty()) {
				Logger.getAnonymousLogger().log(Level.WARNING, MessageFormat.format(
						"campaign[{0}]无offer，跳过此campaign的offer更新！", camp.mApplicaionName));
				continue;
			}
			
			List<CPIOfferInfo> dbOffers = convertOffersFromNet2DB(camp.mOffers);
			if(dbOffers.isEmpty()) {
				Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
						"campaign[{0}]的offers转换失败，跳过此campaign的offer更新！", camp.mApplicaionName));
				continue;
			}
			
			if(!mOfferService.saveCPIOffers(dbOffers)) {
				Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
						"campaign[{0}]的offers保存失败，跳过此campaign的offer更新！", camp.mApplicaionName));
				continue;
			}
			
			List<String> idList = new ArrayList<String>();
			for(CPIOfferInfo info : dbOffers) {
				if(null != info) {
					idList.add(Integer.toString(info.getOfferId()));
				}
			}
			// 生成id列表字符串
			String ids = generateIds(idList);
			res.add(ids);
			
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
					"campaign[{0}]的offer保存成功！", camp.mApplicaionName));
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, "offers操作结束！");
		
		return res;
	}
	
	private List<CPIOfferInfo> convertOffersFromNet2DB(List<CPIOfferBeans.OfferInfo> netOffers) {
		List<CPIOfferInfo> res = new ArrayList<CPIOfferInfo>();
		
		if(null == netOffers || netOffers.isEmpty()) {
			return res;
		}
		
		for(CPIOfferBeans.OfferInfo netOffer : netOffers) {
			if(null == netOffer) {
				continue;
			}
			
			CPIOfferInfo info = new CPIOfferInfo();
			info.setAllowNetworkTypes(generateIds(netOffer.mAllowNetworkTypes));
			info.setCap(netOffer.mCap);
			info.setCategories(generateIds(netOffer.mCategories));
			info.setCountries(generateIds(netOffer.mCountries));
			info.setCurrency(netOffer.mCurrency);
			info.setDesc(netOffer.mDesc);
			info.setFlow(netOffer.mFlow);
			info.setForbiddenNetworkTypes(generateIds(netOffer.mForbiddenNetworkTypes));
			info.setMarket(netOffer.mMarket);
			info.setIncent(netOffer.mIncent);
			info.setNeedDeviceInfo(netOffer.mNeedDeviceInfo);
			info.setOfferId(netOffer.mOfferId);
			info.setOfferName(netOffer.mOfferName);
			info.setPayout(netOffer.mPayout);
			info.setPayoutType(netOffer.mPayoutType);
			info.setPlatforms(generateIds(netOffer.mPlatforms));
			info.setPreviewLink(netOffer.mPreviewLink);
			info.setSubAffBlacklist(generateIds(netOffer.mSubAffBlacklist));
			info.setTrackLink(netOffer.mTrackLink);
			info.setVersion(netOffer.mVersion);
			
			res.add(info);
		}
		
		return res;
	}
	
	private Map<String, String> updateIcons(CPIOfferBeans.Response response) {
		Map<String, String> res = new HashMap<String, String>();
		
		if(response.mCampaigns == null || response.mCampaigns.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, "响应中无campaign信息，无需更新");
			return res;
		}
		
		int campaignCount = Math.max(response.mCount, response.mCampaigns.size());
		for(int i = 0; i < campaignCount; i ++) {
			CPIOfferBeans.Campaign camp = response.mCampaigns.get(i);
			if(null == camp) {
				continue;
			}
			
			if(StringUtils.isEmpty(camp.mPkgName)) {
				Logger.getAnonymousLogger().log(Level.WARNING, "包名缺失，素材不予入库！");
				continue;
			}
			
			// 计算offersid
			List<String> offersIdList = new ArrayList<String>();
			for(CPIOfferBeans.OfferInfo info : camp.mOffers) {
				if(null == info) {
					continue;
				}
				
				offersIdList.add(Integer.toString(info.mOfferId));
			}
			
			String offersId = generateIds(offersIdList);
			if(StringUtils.isEmpty(offersId)) {
				Logger.getAnonymousLogger().log(Level.WARNING, MessageFormat.format(
						"campaign[{0}]无offersId，跳过Icons设置！", camp.mApplicaionName));
				continue;
			}
			
			// 保存icon素材
			List<CPIMaterialInfo> iconsList = convertMaterialsFromNet2DB(camp.mIcons);
			if(null == iconsList || iconsList.isEmpty()) {
				Logger.getAnonymousLogger().log(Level.WARNING, MessageFormat.format(
						"campaign[{0}]无Icons信息，跳过Icons设置！", camp.mApplicaionName));
				continue;
			}
			
			if(!mCPIMaterialService.addOrUpdateBatch(iconsList)) {
				Logger.getAnonymousLogger().log(Level.WARNING, MessageFormat.format(
						"campaign[{0}]Icons设置失败！", camp.mApplicaionName));
				continue;
			}
			
			// 记录
			List<String> materialIdList = new ArrayList<String>();
			for(CPIMaterialInfo material : iconsList) {
				if(null != material) {
					materialIdList.add(Integer.toString(material.getIndex()));
				}
			}
			String materialIds = generateIds(materialIdList);
			res.put(offersId, materialIds);
			
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
						"campaign[{0}]Icons设置完成！", camp.mApplicaionName));
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, "icons操作完毕");
		
		return res;
	}
	
	private Map<String, String> updateMaterials(CPIOfferBeans.Response response) {
		Map<String, String> res = new HashMap<String, String>();
		
		if(response.mCampaigns == null || response.mCampaigns.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, "响应中无offer信息，无需更新");
			return res;
		}
		
		int campaignCount = Math.max(response.mCount, response.mCampaigns.size());
		for(int i = 0; i < campaignCount; i++) {
			CPIOfferBeans.Campaign camp = response.mCampaigns.get(i);
			if(null == camp) {
				continue;
			}
			
			if(StringUtils.isEmpty(camp.mPkgName)) {
				Logger.getAnonymousLogger().log(Level.WARNING, "包名缺失，素材不予入库！");
				continue;
			}
			
			// 计算offersid
			List<String> offersIdList = new ArrayList<String>();
			for(CPIOfferBeans.OfferInfo info : camp.mOffers) {
				if(null == info) {
					continue;
				}
				
				offersIdList.add(Integer.toString(info.mOfferId));
			}
			
			String offersId = generateIds(offersIdList);
			if(StringUtils.isEmpty(offersId)) {
				Logger.getAnonymousLogger().log(Level.WARNING, MessageFormat.format(
						"campaign[{0}]无offerid，停止设置素材！", camp.mApplicaionName));
				continue;
			}
			
			// 保存material素材
			List<CPIMaterialInfo> iconsList = convertMaterialsFromNet2DB(camp.mMaterials);
			if(null == iconsList || iconsList.isEmpty()) {
				Logger.getAnonymousLogger().log(Level.WARNING, MessageFormat.format(
						"campaign[{0}]无素材信息，停止设置素材！", camp.mApplicaionName));
				continue;
			}
			
			// 添加
			if(!mCPIMaterialService.addOrUpdateBatch(iconsList)) {
				Logger.getAnonymousLogger().log(Level.WARNING, MessageFormat.format(
						"campaign[{0}]设置素材失败！", camp.mApplicaionName));
				continue;
			}
			
			// 记录
			List<String> materialIdList = new ArrayList<String>();
			for(CPIMaterialInfo material : iconsList) {
				if(null != material) {
					materialIdList.add(Integer.toString(material.getIndex()));
				}
			}
			String materialIds = generateIds(materialIdList);
			res.put(offersId, materialIds);
			
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
					"campaign[{0}]设置素材完成！", camp.mApplicaionName));
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, "素材操作完毕");
		
		return res;
	}
	
	private List<CPIMaterialInfo> convertMaterialsFromNet2DB(List<CPIOfferBeans.ImgMaterial> materials) {
		List<CPIMaterialInfo> res = new ArrayList<CPIMaterialInfo>();
		
		if(null == materials || materials.isEmpty()) {
			return res;
		}
		
		for(CPIOfferBeans.ImgMaterial material : materials) {
			if(null == material) {
				continue;
			}
			
			CPIMaterialInfo matDb = new CPIMaterialInfo();
			matDb.setPixel(material.mPixel);
			matDb.setUrl(material.mURL);
			
			res.add(matDb);
		}
		
		return res;
	}
	
	private void updateCampaigns(CPIOfferBeans.Response response, 
			List<String> offersIds, Map<String, String> iconsIds, Map<String, String> materialsIds) {
		if(null == response) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "response为null，停止添加Campaigns");
			return;
		}
		
		if(null == response.mCampaigns || response.mCampaigns.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "无Campaigns，停止添加Campaign");
			return;
		}
		
		if(null == offersIds || offersIds.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "没有offerIds，停止添加Campaigns");
			return;
		}
		
		// 转换数据
		List<CPICampaignInfo> campListDB = new ArrayList<CPICampaignInfo>();
		int campaignCount = Math.max(response.mCount, response.mCampaigns.size());
		for(int i = 0; i < campaignCount; i++) {
			CPIOfferBeans.Campaign campNet = response.mCampaigns.get(i);
			if(null == campNet) {
				continue;
			}
			
			if(StringUtils.isEmpty(campNet.mPkgName)) {
				Logger.getAnonymousLogger().log(Level.WARNING, "包名缺失，campaign不予入库！");
				continue;
			}
			
			// 查看此Campaign对应的offers有没有更新成功
			List<String> offerIds = new ArrayList<String>();
			for(CPIOfferBeans.OfferInfo offer : campNet.mOffers) {
				if(null != offer) {
					offerIds.add(Integer.toString(offer.mOfferId));
				}
			}
			String offersId = generateIds(offerIds);
			if(!offersIds.contains(offersId)) {
				Logger.getAnonymousLogger().log(Level.WARNING, MessageFormat.format(
						"未找到Campaign【{0} -- {1}】对应的offersId，停止录入Campaign！", 
						campNet.mApplicaionName, campNet.mPkgName));
				continue;
			}
			
			String iconsId = null;
			if(null != iconsIds) {
				iconsId = iconsIds.get(offersId);
			}
			if(StringUtils.isEmpty(iconsId)) {
				Logger.getAnonymousLogger().log(Level.WARNING, MessageFormat.format(
						"Campaign【{0} -- {1}】对应的icons的id为空！", 
						campNet.mApplicaionName, campNet.mPkgName));
			}
			
			String matsId = null;
			if(null != materialsIds) {
				matsId = materialsIds.get(offersId);
			}
			if(StringUtils.isEmpty(matsId)) {
				Logger.getAnonymousLogger().log(Level.WARNING, MessageFormat.format(
						"Campaign【{0} -- {1}】对应的matsId的id为空！", 
						campNet.mApplicaionName, campNet.mPkgName));
			}
			
			CPICampaignInfo campDB = new CPICampaignInfo();
			campDB.setCampaign(campNet.mApplicaionName);
			campDB.setDesc(campNet.mDesc);
			campDB.setPkgName(campNet.mPkgName);
			campDB.setOfferIds(offersId);
			campDB.setIconIds(iconsId);
			campDB.setMaterialIds(matsId);
			
			campListDB.add(campDB);
		}
		if(campListDB.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "未生成DB使用的Campaign列表，停止更新！");
			return;
		}
		
		if(!mCPICampaignInfoService.addOrUpdateCampaignsBatch(campListDB)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "更新Campaign列表失败！");
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, "更新Campaign列表成功！");
	}
	
	private void cleanNotInResponse(CPIOfferBeans.Response response) {
		if(null == response) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "response为null，停止清理！");
			return;
		}
		
		// 清理offers
		cleanOffersNoInResponse(response);
		
		// TODO 以后添加清理操作，清理掉本次请求中未出现的campaign以及未使用的offers和素材
	}
	
	private void cleanOffersNoInResponse(CPIOfferBeans.Response response) {
		if(null == response) {
			return;
		}
		
		List<Integer> offerIdsInResonse = new ArrayList<Integer>();
		int campaignCount = Math.max(response.mCount, response.mCampaigns.size());
		for(int i = 0; i < campaignCount; i++) {
			CPIOfferBeans.Campaign campNet = response.mCampaigns.get(i);
			if(null == campNet) {
				continue;
			}
			
			if(campNet.mOffers == null || campNet.mOffers.isEmpty()) {
				continue;
			}
			
			for(CPIOfferBeans.OfferInfo offer : campNet.mOffers) {
				offerIdsInResonse.add(offer.mOfferId);
			}
		}
		
		// TODO 还未完成
	}
	
	private String generateIds(List<String> ids) {
		String res = "";
		
		if(null == ids || ids.isEmpty()) {
			return res;
		}
		
		Collections.sort(ids);
		
		for(String id : ids) {
			if(StringUtils.isEmpty(id)) {
				continue;
			}
			
			if(!StringUtils.isEmpty(res)) {
				res += ",";
			}
			
			res += id;
		}
		
		return res;
	}
}
