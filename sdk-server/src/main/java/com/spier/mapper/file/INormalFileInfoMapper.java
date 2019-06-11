package com.spier.mapper.file;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spier.common.bean.db.file.NormalFileInfo;

/**
 * 普通文件信息mapper
 * @author GHB
 * @version 1.0
 * @date 2018.12.29
 */
public interface INormalFileInfoMapper {
	/**
	 * 插入普通文件信息数据
	 * @param info
	 */
	public void insertFileInfo(@Param("tableName") String tabName, @Param("info") NormalFileInfo info);
	
	/**
	 * 根据文件id查找对应的记录
	 * @param id
	 * @return 可能为null
	 */
	public NormalFileInfo findFileRecordById(@Param("tableName") String tabName, @Param("id") String id);
	
	/**
	 * 通过序列号查询一条记录
	 * @param ind 序列号
	 * @return 可能为null
	 */
	public NormalFileInfo findFileRecordByInd(@Param("tableName") String tabName, @Param("ind") int ind);
	
	/**
	 * 更新文件信息
	 * @param info
	 */
	public void updateFileInfoByInd(@Param("tableName") String tabName, @Param("ind") int ind, @Param("info") NormalFileInfo info);
	
	/**
	 * 根据id删除文件记录
	 * @param id
	 */
	public void deleteFileInfoById(@Param("tableName") String tabName, @Param("id") String id);
	
	/**
	 * 获取所有信息
	 * @return
	 */
	public List<NormalFileInfo> getAll(@Param("tableName") String tabName);
}
