package com.spier.job;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.common.bean.db.referrer.ReferrerInfo;
import com.spier.common.config.ReferrerConfig;
import com.spier.referrerdispatchers.ConversionDispatcherFactory;
import com.spier.referrerdispatchers.IConversionDispatcher;
import com.spier.service.referrer.IReferrerService;

/**
 * 激活转化通知任务，每5分钟运行一次
 * @author GHB
 * @version 1.0
 * @date 2019.3.8
 */
@Component
public class ReferrerNotifier {

	private static final String M_TRIGGER_PATTERN = "0 */5 * * * ? ";
	
	@Reference
	private IReferrerService mReferrerService;
	
	private static final int M_MAX_AMOUNT = 100;
	
	/**
	 * 给渠道分发激活信息
	 * TODO 要增加一个状态：处理结果（成功/失败，无论成功还是失败，都算处理过了。）；增加一列：处理说明。服务先停用。
	 */
	@Scheduled(cron = M_TRIGGER_PATTERN)
	public void dispatchActiveInfo() {
		Logger.getAnonymousLogger().log(Level.INFO, "开始下发激活转化……");
		
		List<Integer> succeedDispatchConvs = new ArrayList<Integer>();
		List<Integer> failedDispatchConvs = new ArrayList<Integer>();
		List<ReferrerInfo> freshConvers = new ArrayList<ReferrerInfo>();
		do {
			succeedDispatchConvs.clear();
			failedDispatchConvs.clear();
			
			freshConvers = mReferrerService.getFreshConversions(M_MAX_AMOUNT);
			if(freshConvers.isEmpty()) {
				continue;
			}
			
			for(ReferrerInfo conv : freshConvers) {
				if(null == conv) {
					continue;
				}
				
				boolean succeed = dispatchConversion(conv);
				if(succeed) {
					succeedDispatchConvs.add(conv.getInd());
				} else {
					failedDispatchConvs.add(conv.getInd());
				}
			}
			
			if(!succeedDispatchConvs.isEmpty()) {
				markConversionDispatchedSucceed(succeedDispatchConvs);
				
				
			}
			
			if(!failedDispatchConvs.isEmpty()) {
				markConversionDispathedFailed(failedDispatchConvs);
			}
		} while(!freshConvers.isEmpty());
		
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
				"成功下发【{0}】个转化。", succeedDispatchConvs.size()));
		
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
				"失败下发【{0}】个转化。", failedDispatchConvs.size()));
	}
	
	private boolean dispatchConversion(ReferrerInfo conv) {
		if(null == conv) {
			return false;
		}
		
		IConversionDispatcher dispatcher = ConversionDispatcherFactory.getInstance().getDispatcher(conv.getChan());
		if(null == dispatcher) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"未找到渠道【{0}】对应的转化分发器，无法分发！", conv.getChan()));
			return false;
		}
		
		boolean succeed = dispatcher.dispatch(conv);
		if(!succeed) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"对渠道【{0}】分发转化失败！", conv.getChan()));
			return false;
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("对渠道【{0}】分发转化成功", conv.getChan()));
		
		return true;
	}
	
	private void markConversionDispatchedSucceed(List<Integer> inds) {
		markConversionDispatchState(inds, ReferrerConfig.M_NOTIFIED_SUCCEED_VALUE);
	}
	
	private void markConversionDispathedFailed(List<Integer> inds) {
		markConversionDispatchState(inds, ReferrerConfig.M_NOTIFIED_FAILED_VALUE);
	}
	
	private void markConversionDispatchState(List<Integer> inds, int state) {
		if(inds == null || inds.isEmpty()) {
			return;
		}
		
		mReferrerService.markReferrerNotifiedState(inds, state);
	}
}
