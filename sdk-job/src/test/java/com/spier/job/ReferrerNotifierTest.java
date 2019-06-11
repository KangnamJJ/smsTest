package com.spier.job;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.SDKJobStart;
import com.spier.common.bean.db.referrer.ReferrerBeans.ReferrerRequest;
import com.spier.common.bean.db.referrer.ReferrerInfo;
import com.spier.common.config.ReferrerConfig;
import com.spier.common.utils.ReflectUtils;
import com.spier.referrerdispatchers.ConversionDispatcherFactory;
import com.spier.service.channel.IChannelInfoService;
import com.spier.service.referrer.IReferrerService;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKJobStart.class)
@EnableAutoConfiguration
public class ReferrerNotifierTest {

	@Reference
	private IReferrerService mReferrerService;
	
	@Reference
	private IChannelInfoService mChanInfoService;
	
	@Test
	public void testAll() {
		ReferrerNotifier notifier = new ReferrerNotifier();
		ReflectUtils.setFieldValue(notifier, "mReferrerService", mReferrerService);
		
		ReflectUtils.setFieldValue(ConversionDispatcherFactory.getInstance(), "mChanInfoService", mChanInfoService);
		ReflectUtils.callMethod(ConversionDispatcherFactory.getInstance(), 
				"buildDispatchers", new Object[]{}, new Class<?>[]{});
		
		String chan = ReferrerConfig.M_CHAN_NO_HEXMOBILE;
		
		// 构造数据
		ReferrerRequest request = new ReferrerRequest();
		request.mAppName = "a.b.c.d";
//		request.mReferrer = "utm_source=google&utm_medium=CPI&utm_campaign=20190314230133&conversion_id=12344321";		// 这两种方式都可以
		request.mReferrer = "utm_source%3Dgoogle%26utm_medium%3DCPI%26utm_campaign%3D20190314230133%26conversion_id%3D12344321";
		request.mUID = "asfsfsdfsdfsd";
		mReferrerService.addReferrer("192.168.1.36", chan, request);
		notifier.dispatchActiveInfo();
		
		// 检查数据是否已处理
		List<ReferrerInfo> solved = mReferrerService.getRecordsByKeys(chan, request.mAppName, request.mUID);
		Assert.isTrue(solved != null && solved.size() == 1, "处理成功的数据应该有一个");
		Assert.isTrue(solved.get(0).getNotifiedState() == ReferrerConfig.M_NOTIFIED_SUCCEED_VALUE, "成功通知的记录状态正确");
		
		// 构造一个渠道没有分发器的，这样会处理失败
		ReferrerRequest request1 = new ReferrerRequest();
		request1.mAppName = "a.b.c.d";
//		request.mReferrer = "utm_source=google&utm_medium=CPI&utm_campaign=20190314230133&conversion_id=12344321";		// 这两种方式都可以
		request1.mReferrer = "utm_source%3Dgoogle%26utm_medium%3DCPI%26utm_campaign%3D20190314230133%26conversion_id%3D12344321";
		request1.mUID = "asfsfsdfsdfsd";
		mReferrerService.addReferrer("192.168.1.36", "wrong channel", request1);
		notifier.dispatchActiveInfo();
		List<ReferrerInfo> failed = mReferrerService.getRecordsByKeys("wrong channel", request1.mAppName, request1.mUID);
		Assert.isTrue(failed != null && failed.size() == 1, "处理失败的数据应该有一个");
		Assert.isTrue(failed.get(0).getNotifiedState() == ReferrerConfig.M_NOTIFIED_FAILED_VALUE, "失败通知的记录状态正确");
		
		// 清理数据
		mReferrerService.deleteByInd(solved.get(0).mInd);
		mReferrerService.deleteByInd(failed.get(0).mInd);
		
		
	}
}
