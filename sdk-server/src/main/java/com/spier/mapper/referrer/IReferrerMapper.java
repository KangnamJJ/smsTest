package com.spier.mapper.referrer;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spier.common.bean.db.referrer.ReferrerInfo;

/**
 * 推广渠道信息映射
 * @author GHB
 * @version 1.0
 * @date 2019.3.7
 */
public interface IReferrerMapper {

	/**
	 * 新增推广信息
	 * @param tabName
	 * @param chan
	 * @param ref
	 */
	public void addRef(@Param("tabName") String tabName, @Param("chan") String chan, @Param("ref") ReferrerInfo ref);
	
	/**
	 * 获取未通知的记录
	 * @param tableName
	 * @param max 最多条数
	 * @param val 未通知对应的状态
	 * @return 可能为null
	 */
	public List<ReferrerInfo> getRecordsNotNotifiedLimit(@Param("tabName") String tableName, 
			@Param("limit") int max, @Param("val") int val);
	
	/**
	 * 标记通知下发状态
	 * @param tabName
	 * @param inds 所以已处理的转化的序号集合
	 * @param val 要标记的状态
	 */
	public void markRecordNotifyState(@Param("tabName") String tabName, @Param("inds") List<Integer> inds, @Param("val") int val);
	
	/**
	 * 根据渠道号、包名、用户名查询记录
	 * @param tabName
	 * @param chan
	 * @param pkg
	 * @param uid
	 * @return 可能为null
	 */
	public List<ReferrerInfo> getRecordByChanPkgUser(@Param("tabName") String tabName, @Param("chan") String chan, 
			@Param("pkg") String pkg, @Param("uid") String uid);
	
	/**
	 * 根据序列号删除记录
	 * @param tabName
	 * @param ind
	 */
	public void deleteByInd(@Param("tabName") String tabName, @Param("ind") int ind);
}
