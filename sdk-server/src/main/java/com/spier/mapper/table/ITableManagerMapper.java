package com.spier.mapper.table;

import org.apache.ibatis.annotations.Param;

/**
 * 表管理映射接口类
 * @author GHB
 * @version 1.0
 * @Date 2019.1.11
 */
public interface ITableManagerMapper {

	/**
	 * 检查表是否存在
	 * @param tabName 表名称
	 * @return 可能为null
	 */
	public Boolean checkTableExists(@Param("tableName") String tabName);
	
	/**
	 * 删除表
	 * @param tabName 表名称
	 */
	public void dropTable(@Param("tableName") String tabName);
	
	/**
	 * 创建表
	 * @param tabName 表名称
	 * @param sqlCmd 创建表的SQL命令
	 */
	public void createTable(@Param("tableName") String tabName, @Param("sqlcmd") String sqlCmd);
	
	/**
	 * 清空表
	 * @param tabName 表名称
	 */
	public void clearTable(@Param("tableName") String tabName);
}
