package spiper.test.db;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.SDKServerStart;
import com.spier.common.bean.db.task.SyncTaskExecResult;
import com.spier.common.utils.CompareUtil;
import com.spier.service.task.ISyncTaskStatisticsService;

/**
 * 同步任务统计服务测试用例
 * @author GHB
 * @version 1.0
 * @date 2019.2.18
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class SyncTaskStatisticsTest  {

	@Reference
	private ISyncTaskStatisticsService mService;
	
	@Test
	public void testAll() {
		// 全新插入
		SyncTaskExecResult info1 = new SyncTaskExecResult();
		info1.setAppName("asfsdff");
		info1.setChanNo("bsafsdf");
		info1.setOfferId("33");
		info1.setTaskState(1);
		info1.setUserId("xvxcv");
		boolean succeed = mService.insertOrUpdate(info1);
		Assert.isTrue(succeed, "第一次插入是否正确");
		
		SyncTaskExecResult info2 = new SyncTaskExecResult();
		info2.setAppName("hjkkj");
		info2.setChanNo("hkhjkhjk");
		info2.setOfferId("55");
		info2.setTaskState(0);
		info2.setUserId("xvxcv");
		succeed = mService.insertOrUpdate(info2);
		Assert.isTrue(succeed, "第二次插入是否正确");
		
		List<SyncTaskExecResult> testList = new ArrayList<SyncTaskExecResult>();
		testList.add(info1);
		testList.add(info2);
		
		List<SyncTaskExecResult> dbList = mService.getAll();
		Assert.isTrue(CompareUtil.areListsEqual(testList, dbList, true), "纯插入操作后结果是否正确");
		
		// 更新操作
		info1.setOfferId("44");
		succeed = mService.insertOrUpdate(info1);
		dbList = mService.getAll();
		Assert.isTrue(CompareUtil.areListsEqual(testList, dbList, true), "更新操作后结果是否正确");
		
		// 用户信息一样，包名不同，应该表现为插入操作
		SyncTaskExecResult info3 = new SyncTaskExecResult();
		info3.setAppName("234234234");
		info3.setChanNo("hkhjkhjk");
		info3.setOfferId("55");
		info3.setTaskState(0);
		info3.setUserId("xvxcv");
		succeed = mService.insertOrUpdate(info3);
		Assert.isTrue(succeed, "第三次插入是否正确");
		testList.add(info3);
		dbList = mService.getAll();
		Assert.isTrue(CompareUtil.areListsEqual(testList, dbList, true), "更新操作后结果是否正确");
		
		clear(testList);
	}
	
	private void clear(List<SyncTaskExecResult> list) {
		List<String> ids = new ArrayList<String>();
		for(SyncTaskExecResult task : list) {
			ids.add(task.getOfferId());
		}
		
		mService.deleteBatch(ids);
	}
}
