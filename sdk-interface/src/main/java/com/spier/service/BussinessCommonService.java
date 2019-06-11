package com.spier.service;

import java.util.List;
import java.util.Map;

import com.spier.common.bean.db.ScriptInfo;
import com.spier.common.bean.db.task.TaskInfo;

/**
 * 业务方面的公共方法
 * @author Administrator
 */
public interface BussinessCommonService {
	/**
	 * 根据国家名称获取简称
	 * @param str
	 * @return 可能为null
	 */
	public String getCountryAbb(String str);
	/**
	 * 根据国家简称和运营商字符串信息获取运营商名称
	 * @param mcc 国家简称
	 * @param opTxt 运营商字符串信息
	 * @return 可能为null
	 */
	public String getOperatorNameByCountryAbbAndOpText(String abb, String opTxt) ;
	/**
	 * 根据任务查找所有脚本
	 * @param tasks
	 * @return 可能为null
	 */
	public Map<String, ScriptInfo> getScriptsByTasks(List<TaskInfo> tasks) ;
}
