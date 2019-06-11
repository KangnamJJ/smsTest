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
import com.spier.common.bean.db.task.SyncTaskInfo;
import com.spier.common.utils.CompareUtil;
import com.spier.service.ISyncTaskInfoService;

/**
 * 同步任务测试用例
 * @author GHB
 * @version 1.0
 * @date 2019.2.18
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class SyncTaskInfoTest{

	@Reference
	private ISyncTaskInfoService mSyncTaskService;
	
	@Test
	public void testAll() {
		// 纯添加
		List<SyncTaskInfo> taskList = new ArrayList<SyncTaskInfo>();
		SyncTaskInfo info1 = new SyncTaskInfo();
		info1.setOfferId(26);
		info1.setAllowNetworkTypes("sdfsdf");
		info1.setCampaign("sfsdf");
		info1.setCap(200);
		info1.setCategories("sdfsdf");
		info1.setCountries("asdfasdfsdf,asfsdf");
		info1.setCurrency("vsfsdf");
		info1.setDesc("wrwerwerwer");
		info1.setFinishedCount(0);
		info1.setFlow("asdfsdfsdfsdfsdf");
		info1.setForbiddenNetworkTypes("safsdfsdfdsf");
		info1.setIconsIds("asfasdfsdfds");
		info1.setIncent("yes");
		info1.setMarket("asfsdfsdf");
		info1.setMaterialsIds("asfsdf,sdfsdf,sdf");
		info1.setNeedDeviceInfo("no");
		info1.setOfferName("vxcvxcv");
		info1.setPayout(0.7);
		info1.setPayoutType("cpi");
		info1.setPkgName("com.esers.sere");
		info1.setPlatforms("asfasdf");
		info1.setPreviewLink("asdfsdfsdf");
		info1.setSubAffBlacklist("vxcvxzcvcxv");
		info1.setSwitch(0);
		info1.setTrackLink("asfsdfsdfsdfsdfsdfsdf");
		info1.setVersion("1");
		taskList.add(info1);
		
		SyncTaskInfo info2 = new SyncTaskInfo();
		info2.setOfferId(27);
		info2.setAllowNetworkTypes("sdfsdf");
		info2.setCampaign("sfsdf");
		info2.setCap(200);
		info2.setCategories("sdfsdf");
		info2.setCountries("asdfasdfsdf,asfsdf");
		info2.setCurrency("vsfsdf");
		info2.setDesc("wrwerwerwer");
		info2.setFinishedCount(0);
		info2.setFlow("asdfsdfsdfsdfsdf");
		info2.setForbiddenNetworkTypes("safsdfsdfdsf");
		info2.setIconsIds("asfasdfsdfds");
		info2.setIncent("yes");
		info2.setMarket("asfsdfsdf");
		info2.setMaterialsIds("asfsdf,sdfsdf,sdf");
		info2.setNeedDeviceInfo("no");
		info2.setOfferName("vxcvxcv");
		info2.setPayout(0.7);
		info2.setPayoutType("cpi");
		info2.setPkgName("com.esers.sere");
		info2.setPlatforms("asfasdf");
		info2.setPreviewLink("asdfsdfsdf");
		info2.setSubAffBlacklist("vxcvxzcvcxv");
		info2.setSwitch(0);
		info2.setTrackLink("asfsdfsdfsdfsdfsdfsdf");
		info2.setVersion("1");
		taskList.add(info2);
		boolean succeed = mSyncTaskService.updateOrInsert(taskList);
		Assert.isTrue(succeed, "纯添加是否正确");
		List<SyncTaskInfo> tasksInDB = mSyncTaskService.getAll();
		Assert.isTrue(CompareUtil.areListsEqual(tasksInDB, taskList, true), "存取结果应该一致");
		
		// 测试更新插入
		SyncTaskInfo info3 = new SyncTaskInfo();
		info3.setOfferId(28);
		info3.setAllowNetworkTypes("sdfsdf");
		info3.setCampaign("sfsdf");
		info3.setCap(200);
		info3.setCategories("sdfsdf");
		info3.setCountries("asdfasdfsdf,asfsdf");
		info3.setCurrency("vsfsdf");
		info3.setDesc("wrwerwerwer");
		info3.setFinishedCount(0);
		info3.setFlow("asdfsdfsdfsdfsdf");
		info3.setForbiddenNetworkTypes("safsdfsdfdsf");
		info3.setIconsIds("asfasdfsdfds");
		info3.setIncent("yes");
		info3.setMarket("asfsdfsdf");
		info3.setMaterialsIds("asfsdf,sdfsdf,sdf");
		info3.setNeedDeviceInfo("no");
		info3.setOfferName("vxcvxcv");
		info3.setPayout(0.7);
		info3.setPayoutType("cpi");
		info3.setPkgName("com.esers.sere");
		info3.setPlatforms("asfasdf");
		info3.setPreviewLink("asdfsdfsdf");
		info3.setSubAffBlacklist("vxcvxzcvcxv");
		info3.setSwitch(0);
		info3.setTrackLink("asfsdfsdfsdfsdfsdfsdf");
		info3.setVersion("1");
		taskList.add(info3);
		info2.setCap(999);
		succeed = mSyncTaskService.updateOrInsert(taskList);
		Assert.isTrue(succeed, "更新插入是否成功");
		tasksInDB = mSyncTaskService.getAll();
		Assert.isTrue(CompareUtil.areListsEqual(tasksInDB, taskList, true), "存取结果应该一致");
		
		clear(taskList);
	}
	
	private void clear(List<SyncTaskInfo> tasks) {
		List<Integer> ids = new ArrayList<Integer>();
		
		for(SyncTaskInfo info : tasks) {
			if(null == info) {
				continue;
			}
			
			ids.add(info.mOfferId);
		}
		
		if(!ids.isEmpty()) {
			mSyncTaskService.deleteByIdsBatch(ids);
		}
	}
}
