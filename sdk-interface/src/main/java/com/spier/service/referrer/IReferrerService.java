package com.spier.service.referrer;

import java.util.List;

import com.spier.common.bean.db.referrer.ReferrerBeans;
import com.spier.common.bean.db.referrer.ReferrerInfo;
import com.spier.service.IAutoInitTable;

/**
 * 广告渠道数据服务接口
 * @author GHB
 * @version 1.0
 * @date 2019.3.7
 */
public interface IReferrerService extends IAutoInitTable {

	/**
	 * 新增渠道新增上报
	 * @param chan
	 * @param request
	 */
	public void addReferrer(String ip, String chan, ReferrerBeans.ReferrerRequest request);
	
	/**
	 * 获取未处理的转化数据
	 * @param maxLimit 本批次获取最大数量
	 * @return 不为null
	 */
	public List<ReferrerInfo> getFreshConversions(int maxLimit);
	
	/**
	 * 记录转化下发状态
	 * @param inds
	 * @param state
	 */
	public void markReferrerNotifiedState(List<Integer> inds, int state);
	
	/**
	 * 根据关键条件查询记录
	 * @param chan
	 * @param pkg
	 * @param uid
	 * @return 不为null
	 */
	public List<ReferrerInfo> getRecordsByKeys(String chan, String pkg, String uid);
	
	/**
	 * 根据序列号删除
	 * @param ind
	 */
	public void deleteByInd(int ind);
}
