package com.spier.service.spot;

import java.util.List;
import java.util.Map;

import com.spier.entity.SpotInfo;
import com.spier.service.IAutoInitTable;

/**
 * 埋点信息服务接口
 * @author GHB
 * @version 1.0
 * @date 2019.1.18
 */
public interface ISpotInfoService extends IAutoInitTable {

	/**
	 * 为客户端测试添加，不检查是否存在
	 * @param spot
	 */
	public boolean addRecordForClientTest(SpotInfo spot);
	
	/**
	 * 添加或更新记录
	 * @param spot
	 * @return 操作是否成功
	 */
	public boolean addOrUpdateRecord(SpotInfo spot);
	
	/**
	 * 根据用户特征信息获取记录
	 * @param uid
	 * @param chanNo
	 * @return
	 */
	public List<SpotInfo> getSpotsByUser(String uid, String chanNo);

	/**
	 * 根据用户特征信息获取记录
	 * @param args
	 * @return
	 */
	public List<SpotInfo> getSpotsByCondition(Map<String, Object> args);

	/**
	 * 取一页数据
	 * @param from 从第几条开始
	 * @param pageLimit 一页多少条
	 * @return 不为null
	 */
	public List<SpotInfo> getSpotsLimit(int from, int pageLimit);
	
	/**
	 * 获取埋点数量
	 * @return
	 */
	public int getSpotsAmount();

	/**
	 * 根据条件获取埋点数量
	 * @param args
	 * @return
	 */
	public int getSpotsAmountByConditon(Map<String, Object> args);

	/**
	 * 根据关键信息查询记录
	 * @param uid
	 * @param chanNo
	 * @param fid
	 * @return 可能为null
	 */
	public SpotInfo getSpotByKeyConds(String uid, String chanNo, String fid);
	
	/**
	 * 
	 * @param uid
	 * @param chanNo
	 * @param fid
	 */
	public void deleteByInd(int ind);
}
