package com.spier.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.spier.common.config.GlobalConfig;

/**
 * 业务方面的公共工具
 * @author GHB
 * @version 1.0
 * @date 2019.1.18
 *
 */
public class BussinessCommonUtil {

	public static BussinessCommonUtil getInstance() {
		if(null == mInstance) {
			synchronized (mInstanceLock) {
				if(null == mInstance) {
					mInstance = new BussinessCommonUtil();
				}
			}
		}
		
		return mInstance;
	}
	
	private static BussinessCommonUtil mInstance = null;
	private static final Object mInstanceLock = new Object();
	
	private BussinessCommonUtil() {
		
	}
	
	/**
	 * 获取网络类型选项
	 * @return 不为null
	 */
	public Map<Integer, String> getNetEnvSelections() {
		Map<Integer, String> netStates = new HashMap<Integer, String>();
		netStates.put(GlobalConfig.M_NET_ENV_ALL, "全部");
		netStates.put(GlobalConfig.M_NET_ENV_MOBILE, "手机流量");
		netStates.put(GlobalConfig.M_NET_ENV_WIFI, "WiFi流量");
		
		return netStates;
	}
	
	public String getNetEnvNameByValue(int value) {
		String res = "";
		
		switch(value) {
			case GlobalConfig.M_NET_ENV_ALL:
				res = "全部";
			break;
			
			case GlobalConfig.M_NET_ENV_MOBILE:
				res = "手机流量";
			break;
			
			case GlobalConfig.M_NET_ENV_WIFI:
				res = "WiFi流量";
			break;
			
			default:
				res = Integer.toString(value);
			break;
		}
		
		return res;
	}
	
	/**
	 * 获取任务状态选项
	 * @return 不为null
	 */
	public Map<Integer, String> getTaskStateSelections() {
		Map<Integer, String> taskStates = new HashMap<Integer, String>();
		taskStates.put(GlobalConfig.M_TASK_STATE_PAUSE, getTaskStateString(GlobalConfig.M_TASK_STATE_PAUSE));
		taskStates.put(GlobalConfig.M_TASK_STATE_RUNNING, getTaskStateString(GlobalConfig.M_TASK_STATE_RUNNING));
		
		return taskStates;
	}
	
	public String getTaskStateString(int state) {
		String res = "未定义";
		switch(state) {
			case GlobalConfig.M_TASK_STATE_PAUSE:
				res = "停止";
			break;
			
			case GlobalConfig.M_TASK_STATE_RUNNING:
				res = "运行";
			break;
		}
		
		return res;
	}
	
	/**
	 * 获取埋点类型选项
	 * @return 不为null
	 */
	public Map<Integer, String> getSpotTypeSelections() {
		Map<Integer, String> res = new HashMap<Integer, String>();
		res.put(GlobalConfig.M_SPOT_TYPE_ERR, "错误信息");
		res.put(GlobalConfig.M_SPOT_TYPE_NORMAL, "运行");
		
		return res;
	}
	
	/**
	 * 获取任务类型选项
	 * @return 不为null
	 */
	public Map<Integer, String> getTaskTestSelections() {
		Map<Integer, String> res = new HashMap<Integer, String>();
		res.put(GlobalConfig.M_TASK_TYPE_TEST, "测试任务");
		res.put(GlobalConfig.M_TASK_TYPE_ONLINE, "常规任务");
		
		return res;
	}
	
	/**
	 * 获取结算类型
	 * @return 不为null
	 */
	public List<String> getPayoutTypes() {
		List<String> res = new ArrayList<String>();
		res.add(GlobalConfig.M_PAYOUT_TYPE_CPA);
		res.add(GlobalConfig.M_PAYOUT_TYPE_CPC);
		res.add(GlobalConfig.M_PAYOUT_TYPE_CPE);
		res.add(GlobalConfig.M_PAYOUT_TYPE_CPI);
		res.add(GlobalConfig.M_PAYOUT_TYPE_CPM);
		res.add(GlobalConfig.M_PAYOUT_TYPE_CPS);
		res.add(GlobalConfig.M_PAYOUT_TYPE_SMARTLINK);
		
		return res;
	}
	
	/**
	 * 将String List拼接序列化
	 * @param list 
	 * @param spliter 分隔符
	 * @return 不为null
	 */
	public String serializeStringList2String(List<String> list, String spliter) {
		String res = "";
		if(null == list || list.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, "list无元素，");
			return res;
		}
		
		if(StringUtils.isEmpty(spliter)) {
			Logger.getAnonymousLogger().log(Level.INFO, "spliter为空，无法序列化！");
			return res;
		}
		
		for(String item : list) {
			if(StringUtils.isEmpty(item)) {
				continue;
			}
			
			if(!StringUtils.isEmpty(res)) {
				res += spliter;
			}
			
			res += item;
		}
		
		return res;
	}
	
	/**
	 * 把字符串分割反序列化为String List。如果无法拆分，则列表里为原始字符串。如果返回空列表，表示出错。
	 * @param str
	 * @param spliter 分隔符
	 * @return 不为null
	 */
	public List<String> deserializeString2StringList(String str, String spliter) {
		List<String> res = new ArrayList<String>();
		
		if(StringUtils.isEmpty(str)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "原始字符串为空，无法拆分");
			return res;
		}
		
		if(StringUtils.isEmpty(spliter)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "分隔符为空格，无法拆分");
			return res;
		}
		
		String[] array = str.split(spliter);
		res = Arrays.asList(array);
		
		return res;
	}
	
	private static final String M_SPLITER_IDS = ",";
	
	/**
	 * 序列化id列表成字符串，逗号分割
	 * @param ids
	 * @return 不为null
	 */
	public String serializeIds(List<String> ids) {
		return serializeStringList2String(ids, M_SPLITER_IDS);
	}
	
	/**
	 * 反序列化id字符串成列表
	 * @param str
	 * @return 不为null
	 */
	public List<String> deserializeIdsStrign(String str) {
		return deserializeString2StringList(str, M_SPLITER_IDS);
	}
}
