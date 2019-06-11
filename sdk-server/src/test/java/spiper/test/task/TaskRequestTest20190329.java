/*package spiper.test.task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.SDKServerStart;
import com.spier.common.bean.db.task.TaskExecResultInfo;
import com.spier.common.bean.db.task.TaskInfo;
import com.spier.service.task.ITaskInfoService;
import com.spier.service.task.ITaskStatisticsInfoService;

import net.billing.sdk.controllers.TaskStatisticsOperations;
import net.billing.sdk.utils.SpringContextUtil;

*//**
 * 用于任务获取测试用例
 *//*
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class TaskRequestTest20190329 {
	@Reference
    private ITaskInfoService taskInfoService;

    @Reference
    private ITaskStatisticsInfoService iTaskStatisticsInfoService;

    @Test
    public void test() {
        TaskStatisticsOperations taskStatisticsOperations =
                (TaskStatisticsOperations) SpringContextUtil.getApplicationContext().getBean("taskStatisticsOperations");
        Class calsses = taskStatisticsOperations.getClass();
        try {
            // 根据用户ID获取所有执行过的任务
            List<TaskExecResultInfo> userTasks = iTaskStatisticsInfoService.getTasksByUser("1");
            Method method = calsses.getDeclaredMethod("getUserSucceedTasks", List.class);
            method.setAccessible(true);
            List<TaskExecResultInfo> userSucceedTasks = (List<TaskExecResultInfo>) method.invoke(taskStatisticsOperations, userTasks);

            // 获取所有任务列表
            List<TaskInfo> allTasks = taskInfoService.getAllTasks();
            method = calsses.getDeclaredMethod("filterTasksByShortCode", List.class, List.class);
            method.setAccessible(true);
            List<TaskInfo> taskInfos = (List<TaskInfo>) method.invoke(taskStatisticsOperations, allTasks, userSucceedTasks);
            if (CollectionUtils.isEmpty(taskInfos)) {
                System.out.println("赞无任务获取");
                return;
            }
            for (TaskInfo taskInfo : taskInfos) {
                System.out.println(String.format("获取到的任务有ID=%s", taskInfo.getTaskId()));
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
*/