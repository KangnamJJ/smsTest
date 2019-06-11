package spiper.test;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.SDKServerStart;
import com.spier.common.bean.db.referrer.ReferrerBeans.ReferrerRequest;
import com.spier.common.bean.db.referrer.ReferrerInfo;
import com.spier.common.config.ReferrerConfig;
import com.spier.service.referrer.IReferrerService;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class ReferrerInfoTest {

	@Reference
	private IReferrerService mReferrerService;
	
	@Test
	public void testAll() {
		for(int i = 0; i < 10; i++) {
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("第【{0}】次测试开始……", i));
			
			// 增加3条记录
			MyReferrerRequest request1 = new MyReferrerRequest();
			request1.mAppName = "absddcs";
			request1.mReferrer = "vdfsdfsdf";
			request1.mUID = "asfsfsdfsdf";
			request1.mChan = "asfsdfsdfsdf";
			request1.mIP = "12:12:12";
			mReferrerService.addReferrer(request1.mIP, request1.mChan, request1);
			MyReferrerRequest request2 = new MyReferrerRequest();
			request2.mAppName = "absddcs";
			request2.mReferrer = "vdfsdfsdf";
			request2.mUID = "xvxvxcvxcv";
			request2.mChan = "asfsdfsdfsdf";
			request2.mIP = "12:12:12";
			mReferrerService.addReferrer(request2.mIP, request2.mChan, request2);
			MyReferrerRequest request3 = new MyReferrerRequest();
			request3.mAppName = "absddcs";
			request3.mReferrer = "vdfsdfsdf";
			request3.mUID = "ljljkljkljk";
			request3.mChan = "asfsdfsdfsdf";
			request3.mIP = "12:12:12";
			mReferrerService.addReferrer(request3.mIP, request3.mChan, request3);
			List<ReferrerInfo> recordsInDB = mReferrerService.getFreshConversions(1);
			Assert.isTrue(recordsInDB != null, "查询记录不应该为null");
			Assert.isTrue(recordsInDB.size() == 1, "记录条目应该为1");
			recordsInDB = mReferrerService.getFreshConversions(2);
			Assert.isTrue(recordsInDB.size() == 2, "记录条目应该为2");
			recordsInDB = mReferrerService.getFreshConversions(3);
			Assert.isTrue(recordsInDB.size() == 3, "记录条目应该为3");
			recordsInDB = mReferrerService.getFreshConversions(100);
			Assert.isTrue(recordsInDB.size() == 3, "记录条目应该为3");
			
			List<ReferrerInfo> request1Info = mReferrerService.getRecordsByKeys(request1.mChan, request1.mAppName, request1.mUID);
			Assert.isTrue(request1Info != null && request1Info.size() == 1, "取数据应该正确");
			List<ReferrerInfo> request2Info = mReferrerService.getRecordsByKeys(request2.mChan, request2.mAppName, request2.mUID);
			Assert.isTrue(request2Info != null && request2Info.size() == 1, "取数据应该正确");
			List<ReferrerInfo> request3Info = mReferrerService.getRecordsByKeys(request3.mChan, request3.mAppName, request3.mUID);
			Assert.isTrue(request3Info != null && request3Info.size() == 1, "取数据应该正确");
			
			List<Integer> succeedSet = new ArrayList<Integer>();
			succeedSet.add(request1Info.get(0).mInd);
			List<Integer> failedSet = new ArrayList<Integer>();
			failedSet.add(request3Info.get(0).mInd);
			mReferrerService.markReferrerNotifiedState(succeedSet, ReferrerConfig.M_NOTIFIED_SUCCEED_VALUE);
			mReferrerService.markReferrerNotifiedState(failedSet, ReferrerConfig.M_NOTIFIED_FAILED_VALUE);
			
			ReferrerInfo succeedInfo = request1Info.get(0);
			succeedInfo.setNotified(ReferrerConfig.M_NOTIFIED_SUCCEED_VALUE);
			ReferrerInfo failedInfo = request3Info.get(0);
			failedInfo.setNotified(ReferrerConfig.M_NOTIFIED_FAILED_VALUE);
			
			// 比较数据
			List<ReferrerInfo> succeedInfoInDB = mReferrerService.getRecordsByKeys(
					request1Info.get(0).mChan, request1Info.get(0).mApp, request1Info.get(0).mUID);
			List<ReferrerInfo> failedInfoInDB = mReferrerService.getRecordsByKeys(
					request3Info.get(0).mChan, request3Info.get(0).mApp, request3Info.get(0).mUID);
			Assert.isTrue(succeedInfoInDB != null && succeedInfoInDB.size() == 1, "记录成功的记录只有一条");
			Assert.isTrue(failedInfoInDB != null && failedInfoInDB.size() == 1, "记录失败的记录只有一条");
			Assert.isTrue(succeedInfo.equals(succeedInfoInDB.get(0)), "修改状态后的数据应该正确");
			Assert.isTrue(failedInfo.equals(failedInfoInDB.get(0)), "修改状态后的数据应该正确");
			
			// 清理
			mReferrerService.deleteByInd(request1Info.get(0).mInd);
			mReferrerService.deleteByInd(request2Info.get(0).mInd);
			mReferrerService.deleteByInd(request3Info.get(0).mInd);
			
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("第【{0}】次测试通过！", i));
		}
		
	}
	
	private static class MyReferrerRequest extends ReferrerRequest {
		public String mChan;
		public String mIP;
	}
}
