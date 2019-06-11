package spiper.test.db;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.SDKServerStart;
import com.spier.common.bean.db.ChannelInfo;
import com.spier.common.bean.db.task.TaskExecResultInfo;
import com.spier.common.config.GlobalConfig;
import com.spier.common.utils.CompareUtil;
import com.spier.service.channel.IChannelInfoService;
import com.spier.service.task.ITaskInfoService;
import com.spier.service.task.ITaskStatisticsInfoService;

/**
 * 任务完成情况统计表测试类
 * @author GHB
 * @version 1.0
 * @date 2019.1.17
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class TaskStatisticsInfoTest {

	@Reference
	private ITaskStatisticsInfoService mTaskStatisticsService;
	
	@Reference
	private IChannelInfoService mChanInfoService;
	
	@Reference
	private ITaskInfoService mTaskInfoService;
	
	@Test
	public void testAll() {
		List<ChannelInfo> chans = mChanInfoService.getAll();
		Assert.isTrue(chans != null, "渠道信息不为null");
		Assert.isTrue(!chans.isEmpty(), "渠道信息不为空");
		
		// 测试新增
		TaskExecResultInfo info1 = new TaskExecResultInfo();
		info1.setAppName("abcdefg");
		info1.setChanId(chans.get(0).getChannelNo());
		info1.setPhoneNum("12345678");
		info1.setTaskFlowId("testtest");
		info1.setTaskId("tesetsestset");
		info1.setTaskRunningState(GlobalConfig.M_TASK_RUNNING_STATE_RUNNING);
		info1.setUserId("testuser");
		boolean succeed = mTaskStatisticsService.addOrUpdateTaskExecStateRecord(info1);
		Assert.isTrue(succeed, "新增成功");
		// 测试更新
		info1.setTaskRunningState(GlobalConfig.M_TASK_RUNNING_STATE_SUCCEED);
		succeed = mTaskStatisticsService.addOrUpdateTaskExecStateRecord(info1);
		Assert.isTrue(succeed, "更新是否成功");
		// 测试取数据
		TaskExecResultInfo info2 = mTaskStatisticsService.getTaskFlowInfoByFlowId(info1.getTaskFlowId());
		Assert.isTrue(info2 != null, "查询的数据不为null");
		Assert.isTrue(CompareUtil.objsEqual(info1, info2), "两次数据应该相同");
		
		List<TaskExecResultInfo> list = mTaskStatisticsService.getUserFinishedTaskList("testuser");
		Assert.isTrue(list != null, "列表不为null");
		Assert.isTrue(list.size() == 1, "已完成一个");
		
		mTaskStatisticsService.deleteTaskRecordByFlowId(info1.getTaskFlowId());
	}
}
