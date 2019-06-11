package com.spier.service.file;

import java.util.List;

import com.spier.common.bean.db.file.NormalFileInfo;
import com.spier.service.IAutoInitTable;

/**
 * 普通文件访问服务
 * @author GHB
 * @version 1.0
 * @date 2018.12.29
 */
public interface INormalFileService extends IAutoInitTable {

	/**
	 * 插入普通文件信息到数据库
	 * @param info
	 */
	public void insertNormalFileInfo(NormalFileInfo info);
	
	/**
	 * 根据文件id查询文件的本地路径
	 * @param id 文件id
	 * @return 可能为null
	 */
	public String findFilePathById(String id);
	
	/**
	 * 根据文件的序列号查询文件信息
	 * @param ind 文件序列号
	 * @return 可能为null
	 */
	public NormalFileInfo findFileRecordByInd(int ind);
	/**
	 * 通过文件id查询文件信息记录
	 * @param id 文件id
	 * @return 可能为null
	 */
	public NormalFileInfo findFileRecordById(String id);
	
	/**
	 * 通过文件id检查文件记录是否存在
	 * @param id 文件id
	 * @return 
	 */
	public boolean checkFileRecordExistsById(String id);
	
	/**
	 * 通过序列号检查文件是否已经存在
	 * @param ind 文件序列号
	 * @return
	 */
	public boolean checkFileRecordExistsByInd(int ind);
	
	/**
	 * 更新文件记录
	 * @param ind 信息在表中的序号
	 * @param info 要更新的信息记录
	 */
	public void updateNormalFileInfoByInd(int ind, NormalFileInfo info);
	
	/**
	 * 根据id删除记录
	 * @param id
	 */
	public void deleteRecordById(String id);
	
	/**
	 * 获取所有文件记录信息
	 * @return
	 */
	public List<NormalFileInfo> getAllFilesInfo();
}
