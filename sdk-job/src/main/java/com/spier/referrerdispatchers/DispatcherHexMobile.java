package com.spier.referrerdispatchers;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.spier.common.bean.db.referrer.ReferrerInfo;

/**
 * HexMobile的转化分发器
 * @author GHB
 * @version 1.0
 * @date 2019.3.15
 */
public class DispatcherHexMobile extends AbsConversionDispatcher {

	@Override
	public String getConversionIdReplacePart() {
		return "<conversion_id>";
	}

	private static final String M_REPLACEPART_CONV_ID = "{conversion_id}";
	private static final String M_REPLACEPART_PAYOUT = "{payout_amount}";
	
	/* (non-Javadoc)
	 * @see net.billing.sdk.tasks.referrerdispatchers.IConversionDispatcher#beforeDispatching(net.billing.sdk.beans.db.ReferrerInfo)
	 */
	@Override
	public boolean beforeDispatching(ReferrerInfo referrerInfo) {
		if(null == referrerInfo) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"【{0}】：referrerInfo为null，停止预处理！", getChan()));
			return false;
		}
		
		// 从Referrer中取出conversionid
		String convId = absortConvIdFromReferrer(referrerInfo.mReferrer);
		if(StringUtils.isEmpty(convId)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"【{0}】：从转化数据【{1}】中提取转化id出错，无法下发转化！", 
					"DispatcherHexMobile", referrerInfo.mReferrer));
			return false;
		}
		addReplacement(M_REPLACEPART_CONV_ID, convId);
		
		// TODO 设置payout，暂时写死。后面放到渠道设置里
		String payout = "0.1USD";
		addReplacement(M_REPLACEPART_PAYOUT, payout);
		
		return true;
	}

}
