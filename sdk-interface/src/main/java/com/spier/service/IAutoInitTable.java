package com.spier.service;

/**
 * 自动初始化表接口，用于定义某个表的创建、删除、清空、查询是否存在等操作
 * @author GHB
 * @version 1.0
 * @date 2019.1.11
 */
public interface IAutoInitTable {

	/**
	 * 获取表名称，不能为null
	 * @return
	 */
	public String getTableName();
	
	/**
	 * 获取创建表的SQL
	 * @return 不能为null
	 */
	public String getCreateTableSql();
}
