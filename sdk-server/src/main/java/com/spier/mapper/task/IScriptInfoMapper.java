package com.spier.mapper.task;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spier.common.bean.db.ScriptInfo;

/**
 * 脚本信息数据库操作映射接口
 * @author GHB
 * @version 1.0
 * @date 2019.1.5
 */
public interface IScriptInfoMapper {

	/**
	 * 脚本信息入库
	 * @param info
	 */
	public void insertRecord(@Param("tableName") String tabName, @Param("info") ScriptInfo info);
	
	/**
	 * 根据索引号删除脚本信息
	 * @param ind
	 */
	public void deleteRecordByInd(@Param("tableName") String tabName, @Param("ind") int ind);
	
	/**
	 * 根据脚本id删除脚本信息
	 * @param scriptId
	 */
	public void deleteRecordByScriptId(@Param("tableName") String tabName, @Param("scriptId") String scriptId);
	
	/**
	 * 根据索引号查询记录
	 * @param ind
	 * @return 可能为null
	 */
	public ScriptInfo findRecordByInd(@Param("tableName") String tabName, @Param("ind") int ind);
	
	/**
	 * 根据脚本id查询所有记录
	 * @param scriptId 
	 * @return 可能为null
	 */
	public List<ScriptInfo> findRecordByScriptId(@Param("tableName") String tabName, @Param("scriptId") String scriptId);
	
	/**
	 * 根据记录索引号更新记录
	 * @param ind
	 * @param info 要更新的数据
	 */
	public void updateRecordByInd(@Param("tableName") String tabName, @Param("ind") int ind, @Param("info") ScriptInfo info);
	
	/**
	 * 根据脚本id更新所有记录
	 * @param scriptId 脚本id
	 * @param info 要更新的数据
	 */
	public void updateRecordsByScriptId(@Param("tableName") String tabName, @Param("scriptId") String scriptId, @Param("info") ScriptInfo info);
	
	/**
	 * 获取所有信息
	 * @return 可能为null
	 */
	public List<ScriptInfo> getAll(@Param("tableName") String tabName);
	
	/**
	 * 根据国家、运营商、网络类型查询所有脚本
	 * @param tabName
	 * @param countryAbb
	 * @param op
	 * @param netEnv
	 * @return 可能为null
	 */
	public List<String> findScriptIdsByKeyConds(@Param("tableName") String tabName, @Param("abb") String countryAbb, 
			@Param("op") String op, @Param("net") int netEnv);
	
	/**
	 * 根据国家和运营商查找所有脚本id
	 * @param tabName
	 * @param countryAbb
	 * @param op
	 * @return 可能为null
	 */
	public List<String> findScriptIdsByCountryAbbAndOp(@Param("tableName") String tabName, @Param("abb") String countryAbb,
			@Param("op") String op);

	/**
	 * 根据国家和运营商查找所有脚本id
	 * @param tabName
	 * @param countryAbb
	 * @param op
	 * @return 可能为null
	 */
	public List<String> getScriptInfosByCondition(@Param("tableName") String tabName, @Param("abb") String countryAbb,
													  @Param("op") String op, @Param("net") int netEnv);
}
