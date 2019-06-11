package spiper.test.db;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.SDKServerStart;
import com.spier.common.bean.db.task.TaskInfo;
import com.spier.common.utils.CompareUtil;
import com.spier.mapper.task.ITaskInfoMapper;
import com.spier.service.task.ITaskInfoService;

/**
 * 任务信息服务测试类
 * @author GHB
 * @version 1.0
 * @date 2019.1.8
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class TaskInfoTest {

	@Reference
	private ITaskInfoService mTaskInfoService;
	
	@Autowired
	private ITaskInfoMapper mTaskInfoMapper;
	
	@Test
	public void testService() {
		for(int i = 0; i < 10; i++) {
			TaskInfo info = new TaskInfo();
			info.setScriptId("asdfsdfsdf");
			info.setTaskTotalCount(100);
			info.setTaskFinishedCount(10);
			info.setTaskDesc("abscef你好世界！123");
			info.setTaskId("testTask1");
			info.setTaskState(1);
			
			boolean succeed = mTaskInfoService.addRecord(info);
			Assert.isTrue(succeed, "添加正确的数据是否成功");
			
			TaskInfo infoExists = mTaskInfoService.findRecordByIndex(info.getInd());
			boolean equals = CompareUtil.objsEqual(info, infoExists);
			Assert.isTrue(equals, "查询到的数据和原数据相同");
			
			infoExists = mTaskInfoService.findRecordByTaskId(info.getTaskId());
			equals = CompareUtil.objsEqual(info, infoExists);
			Assert.isTrue(equals, "查询到的数据和原数据相同");
			
			mTaskInfoService.deleteRecordByIndex(info.getInd());
			
			// 测试更新
			succeed = mTaskInfoService.addRecord(info);
			Assert.isTrue(succeed);
			info.setTaskDesc("这是更新后的数据");
			succeed = mTaskInfoService.updateRecordByIndex(info.getInd(), info);
			infoExists = mTaskInfoService.findRecordByIndex(info.getInd());
			Assert.isTrue(CompareUtil.objsEqual(infoExists, info));
			infoExists = mTaskInfoService.findRecordByTaskId(info.getTaskId());
			Assert.isTrue(CompareUtil.objsEqual(infoExists, info));
			
			List<String> scriptIds = new ArrayList<String>();
			scriptIds.add(info.getScriptId());
			List<TaskInfo> tasks = mTaskInfoMapper.findTaskListByScriptIds(mTaskInfoService.getTableName(), scriptIds);
			Assert.isTrue(tasks != null);
			Assert.isTrue(tasks.size() == 1, "返回1个任务");
			
			mTaskInfoService.deleteRecordByIndex(info.getInd());
		}
	}
}
