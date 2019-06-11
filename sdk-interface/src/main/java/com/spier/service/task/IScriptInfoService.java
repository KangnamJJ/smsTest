package com.spier.service.task;

import java.util.List;

import com.spier.common.bean.db.ScriptInfo;
import com.spier.service.IAutoInitTable;

/**
 * 脚本信息服务接口
 * @author GHB
 * @version 1.0
 * @date 2019.1.5
 */
public interface IScriptInfoService extends IAutoInitTable {

	/**
	 * 获取所有信息
	 * @return 不为null
	 */
	public List<ScriptInfo> getAllInfo();
	
	/**
	 * 根据序列号删除某条记录
	 * @param ind
	 */
	public void deleteRecordByInd(int ind);
	
	/**
	 * 根据脚本id删除某条记录
	 * @param id
	 */
	public void deleteRecordByScriptId(String id);
	
	/**
	 * 插入脚本问信息
	 * @param info
	 * @return 操作是否成功
	 */
	public boolean insertScriptInfo(ScriptInfo info);
	
	/**
	 * 通过序列号查询脚本文件信息
	 * @param ind
	 * @return 可能为null
	 */
	public ScriptInfo findScriptInfoByInd(int ind);
	
	/**
	 * 通过脚本id查询所有符合条件的脚本信息
	 * @param id 
	 * @return 不为null
	 */
	public List<ScriptInfo> findScriptInfosByScriptId(String id);
	
	/**
	 * 根据序列号更新脚本信息
	 * @param ind 序列号
	 * @param info 脚本信息
	 * @return 操作结果
	 */
	public boolean updateScriptInfoByInd(int ind, ScriptInfo info);
	
	/**
	 * 根据脚本id更新脚本信息
	 * @param scriptId 脚本id
	 * @param info 脚本信息
	 * @return 操作结果
	 */
	public boolean updateScriptInfoByScriptId(String scriptId, ScriptInfo info);
	
	/**
	 * 根据关键条件查询符合此条件的脚本id
	 * @param countryAbb
	 * @param op
	 * @param netEnv
	 * @return 不为null
	 */
	public List<String> getScriptIdsByKeyConds(String countryAbb, String op, int netEnv);
	
	/**
	 * 根据国家简称和运营商查询脚本id
	 * @param countryAbb
	 * @param op
	 * @return 不为null
	 */
	public List<String> getScriptIdsByCountryAbbAndOp(String countryAbb, String op);

	/**
	 * 根据国家简称和运营商和网络环境查询脚本信息
	 * @param countryAbb
	 * @param op
	 * @return 不为null
	 */
	public List<String> getScriptInfosByCondition(String countryAbb, String op, int netEnv);
}
