package com.spier.service;

import com.spier.common.bean.db.SimInfo;

/**
 * 手机卡信息服务接口
 * @author GHB
 * @version 1.0
 * @date 2018.12.30
 */
public interface ISimInfoService extends IAutoInitTable {

	/**
	 * 添加手机信息记录，添加成功后info的mId字段会更新
	 * @param info
	 * @return 是否成功
	 */
	public boolean addSimInfoRecord(SimInfo info);
	
	/**
	 * 根据记录序号查询手机卡信息
	 * @param ind 序号
	 * @return 可能为null
	 */
	public SimInfo findSimInfoRecordByInd(int ind);
	
	/**
	 * 根据手机号查询整条记录
	 * @param num
	 * @return 可能为null
	 */
	public SimInfo findSimInfoRecordByNumber(String num);
	
	/**
	 * 根据手机号码查询记录所在行的序列号
	 * @param num 手机号
	 * @return 找到：> 0；未找到：<= 0
	 */
	public int findRecordIndByNumber(String num);
	
	/**
	 * 根据记录序号删除对应的记录
	 * @param ind
	 */
	public void deleteSimInfoRecord(int ind);
	
	/**
	 * 根据记录序号更新记录
	 * @param ind 序号
	 * @param info
	 */
	public boolean updateInfoByInd(int ind, SimInfo info); 
	
}
