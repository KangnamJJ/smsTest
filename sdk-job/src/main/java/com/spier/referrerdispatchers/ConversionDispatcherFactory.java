package com.spier.referrerdispatchers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.common.bean.db.ChannelInfo;
import com.spier.service.channel.IChannelInfoService;

/**
 * 转化分发器工厂
 * @author GHB
 * @version 1.0
 * @date 2019.3.16
 */
public class ConversionDispatcherFactory {
	public static ConversionDispatcherFactory mInstance;
	private static final Object mInstanceLock = new Object();
	// 渠道号
    private static List<String> mChans;
    
    private Map<String, IConversionDispatcher> mDispatchers;
	
    @Reference
	private IChannelInfoService mChanInfoService;

	/**
	 * 获取实例
	 * @return 不为null
	 */
	public static ConversionDispatcherFactory getInstance() {
		if(null == mInstance) {
			synchronized (mInstanceLock) {
				if(null == mInstance) {
					mInstance = new ConversionDispatcherFactory();
				}
			}
		}
		
		return mInstance;
	}
	
	private ConversionDispatcherFactory() {
		initChanInfo();
		
		try {
			buildDispatchers();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initChanInfo() {
		mChans = new ArrayList<String>();
		mChans.add(ReferrerConfig.M_CHAN_NO_HEXMOBILE);
	}
	
	private void buildDispatchers() {
		mDispatchers = new HashMap<String, IConversionDispatcher>();
		
		List<ChannelInfo> allChans = mChanInfoService.getAll();
		if(allChans.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "无任何渠道信息，停止创建转化分发器！");
			return;
		}
		
		for(ChannelInfo channel : allChans) {
			if(null == channel) {
				continue;
			}
			
			if(StringUtils.equals(ReferrerConfig.M_CHAN_NO_HEXMOBILE, channel.getChannelNo())) {
				buildDispatcherForHexMobile(channel.getChannelNo(), channel.getPostback());
				Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
						"已为渠道【{0}】创建转化分发器", channel.getChanDesc()));
			}
		}
	}
	
	private void buildDispatcherForHexMobile(String chanNo, String postback) {
		DispatcherHexMobile dispatcher = new DispatcherHexMobile();
		dispatcher.setChan(chanNo);
		dispatcher.setPostback(postback);
		
		mDispatchers.put(ReferrerConfig.M_CHAN_NO_HEXMOBILE, dispatcher);
	}
	
	/**
	 * 获取分发器
	 * @param chan 渠道号
	 * @return 可能为null
	 */
	public IConversionDispatcher getDispatcher(String chan) {
		IConversionDispatcher res = null;
		if(StringUtils.isEmpty(chan)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "渠道为空，无法查找分发器！");
			return res;
		}
		
		synchronized (mInstanceLock) {
			res = mDispatchers.get(chan);
		}
		
		return res;
	}
}
