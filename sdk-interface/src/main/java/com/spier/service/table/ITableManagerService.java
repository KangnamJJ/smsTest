/**
 * 
 */
package com.spier.service.table;

/**
 * 表管理服务
 * @author GHB
 * @version 1.0
 * @date 2019.1.11
 */
public interface ITableManagerService {

	/**
	 * 检查表是否存在
	 * @param tabName 表名称
	 * @return 
	 */
	public boolean checkTableExists(String tabName);
	
	/**
	 * 创建表 
	 * @param tabName 表名称
	 * @param sqlCmd sql命令
	 * @return 操作是否成功
	 */
	public boolean createTable(String tabName, String sqlCmd);
	
	/**
	 * 删除表
	 * @param tabName 表名称
	 * @return 操作是否成功
	 */
	public boolean deleteTable(String tabName);
	
	/**
	 * 清空表
	 * @param tabName 表名称
	 * @return 操作是否成功
	 */
	public boolean clearTable(String tabName);
}
