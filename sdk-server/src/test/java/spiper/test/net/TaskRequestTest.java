package spiper.test.net;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.spier.SDKServerStart;
import com.spier.common.bean.net.task.TaskReportBeans;
import com.spier.common.bean.net.task.TaskRequestBeans;
import com.spier.common.config.GlobalConfig;
import com.spier.common.http.HttpCtx;
import com.spier.common.http.HttpMode;

/**
 * 测试任务请求。本测试用例有几个测试方法，一次只能运行一个。否则会受到上一次运行结果的影响。
 * 另外在运行测试前，要核对用户数据，并清理任务记录。运行后也应该清理。
 * @author GHB
 * @version 1.0
 * @date 2019.3.14
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SDKServerStart.class)
@EnableAutoConfiguration
public class TaskRequestTest {

	private static final String M_CH_NO = "20190307170421";
	private static final String M_RK = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCuJKwIPSrulqt/MV2dyaZeqJgOfU3RihdtlP9xOypOTHKMyA25FIElo+Qd0d+74yb+PPN02K/V1UMqi4XA/I4DXf+A7euFXd70ofspb1ZHfi0pALoNOXoaeGfzPJxajBO74dWL3msj3JviDIk+wJeqWnUeJZRkc4lQqdJHELAdLQIDAQAB";
	
//	@Test
	public void testSucceedInterval() {
		
		TaskRequestBeans.Request requestData = new TaskRequestBeans.Request();
		
		prepareUser(requestData);
		
		prepareUserLogin(requestData);
		
		prepareTaskData(requestData);
		
		makeSucceedRequests(requestData);
	}
	
	private void prepareUser(TaskRequestBeans.Request requestData) {
		if(null == requestData) {
			return;
		}
		
		requestData.setAppName("com.sp.sdktestproject");
		requestData.setUID("9j+Gwjjd7uDVo8O+RanBv3vbu8Y=");
	}
	
	private void prepareUserLogin(TaskRequestBeans.Request requestData) {
		if(null == requestData) {
			return;
		}
		
		requestData.setSessionId("20190307175324GhLOB");
	}
	
	private void prepareTaskData(TaskRequestBeans.Request requestData) {
		if(null == requestData) {
			return;
		}
		
		requestData.setCountry("GR");
		requestData.setNetworkEnv(101);
		requestData.setOperator("Cosmote");
		requestData.setTaskType(GlobalConfig.M_TASK_TYPE_ONLINE);
	}
	
	private void makeSucceedRequests(TaskRequestBeans.Request requestData) {
		if(null == requestData) {
			return;
		}
		
		// 取任务
		HttpCtx ctx = HttpCtx.getDefault();
		ctx.setHttpMode(HttpMode.Post);
		
		SafeHttpRequestTask request = new SafeHttpRequestTask(M_CH_NO, M_RK);
		request.setRequestObj(requestData);
		request.access(ctx);
		Assert.isTrue(request.isSucceed(), "请求任务应该成功");
		
		TaskRequestBeans.Response taskInfo = request.getResponseObj();
		Assert.isTrue(taskInfo != null, "请求任务不应该返回null");
		
		// 上报成功
		TaskReportBeans.Request reportData = new TaskReportBeans.Request();
		reportData.setAppName(requestData.getAppName());
		reportData.setCountry(requestData.getCountry());
		reportData.setOp(requestData.getOperator());
		reportData.setPhoneNumber("1234567");
		reportData.setSessionId(requestData.getSessionId());
		reportData.setSucceed(GlobalConfig.M_TASK_EXEC_RESULT_SUCCEED);
		reportData.setTaskFlowId(taskInfo.getTaskFlowId());
		reportData.setTaskId(taskInfo.getTaskId());
		reportData.setUserId(requestData.getUID());
		
		SafeHttpReportTaskRes report = new SafeHttpReportTaskRes(M_CH_NO, M_RK);
		report.setRequestObj(reportData);
		report.access(ctx);
		Assert.isTrue(report.isSucceed(), "上报任务执行成功应该为成功");
		
		// 再次请求任务
		request = new SafeHttpRequestTask(M_CH_NO, M_RK);
		request.setRequestObj(requestData);
		request.access(ctx);
		Assert.isTrue(request.getResponseObj() == null, "成功后再次请求任务应该没有任务");
	}
	
	/**
	 * 此方法测试，需要将任务列表中任务清空，只保留一个常规任务
	 */
	@Test
	public void testFailedInterval() {
		TaskRequestBeans.Request requestData = new TaskRequestBeans.Request();
		
		prepareUser(requestData);
		
		prepareUserLogin(requestData);
		
		prepareTaskData(requestData);
		
		makeFailedRequests(requestData);
	}
	
	private void makeFailedRequests(TaskRequestBeans.Request requestData) {
		if(null == requestData) {
			return;
		}
		
		// 取任务
		HttpCtx ctx = HttpCtx.getDefault();
		ctx.setHttpMode(HttpMode.Post);
		
		SafeHttpRequestTask request = new SafeHttpRequestTask(M_CH_NO, M_RK);
		request.setRequestObj(requestData);
		request.access(ctx);
		Assert.isTrue(request.isSucceed(), "请求任务应该成功");
		
		TaskRequestBeans.Response taskInfo = request.getResponseObj();
		Assert.isTrue(taskInfo != null, "请求任务不应该返回null");
		
		// 上报失败
		TaskReportBeans.Request reportData = new TaskReportBeans.Request();
		reportData.setAppName(requestData.getAppName());
		reportData.setCountry(requestData.getCountry());
		reportData.setOp(requestData.getOperator());
		reportData.setPhoneNumber("1234567");
		reportData.setSessionId(requestData.getSessionId());
		reportData.setSucceed(GlobalConfig.M_TASK_EXEC_RESULT_FAILED);
		reportData.setTaskFlowId(taskInfo.getTaskFlowId());
		reportData.setTaskId(taskInfo.getTaskId());
		reportData.setUserId(requestData.getUID());
		
		SafeHttpReportTaskRes report = new SafeHttpReportTaskRes(M_CH_NO, M_RK);
		report.setRequestObj(reportData);
		report.access(ctx);
		Assert.isTrue(report.isSucceed(), "上报任务执行失败应该为成功");
		
		// 马上请求任务会失败
		request = new SafeHttpRequestTask(M_CH_NO, M_RK);
		request.setRequestObj(requestData);
		request.access(ctx);
		Assert.isTrue(request.getResponseObj() == null, "应该无任务返回");
		
		// 这里停1分钟，手动修改失败时间，令失败超时（超时时间30分钟）
		Logger.getAnonymousLogger().log(Level.INFO, "已上报失败，请将change_time提前1个小时！");
		try {
			Thread.sleep(60 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// 再次请求任务应该成功
		request = new SafeHttpRequestTask(M_CH_NO, M_RK);
		request.setRequestObj(requestData);
		request.access(ctx);
		Assert.isTrue(request.isSucceed(), "失败超时后再次请求应该成功");
		Assert.isTrue(request.getResponseObj() != null, "失败超时后再次请求应该有任务返回");
		
		// 再次上报失败
		reportData.setTaskFlowId(request.getResponseObj().getTaskFlowId());
		report = new SafeHttpReportTaskRes(M_CH_NO, M_RK);
		report.setRequestObj(reportData);
		report.access(ctx);
		Assert.isTrue(report.isSucceed(), "上报任务执行失败应该为成功");
		
		// 这里这里停1分钟，手动修改失败时间，令失败超时（超时时间30分钟）
		Logger.getAnonymousLogger().log(Level.INFO, "已上报失败，请将change_time提前1个小时！");
		try {
			Thread.sleep(60 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// 再次请求任务应该成功
		request = new SafeHttpRequestTask(M_CH_NO, M_RK);
		request.setRequestObj(requestData);
		request.access(ctx);
		Assert.isTrue(request.isSucceed(), "失败超时后再次请求应该成功");
		Assert.isTrue(request.getResponseObj() != null, "失败超时后再次请求应该有任务返回");
		
		// 第三次上报失败
		reportData.setTaskFlowId(request.getResponseObj().getTaskFlowId());
		report = new SafeHttpReportTaskRes(M_CH_NO, M_RK);
		report.setRequestObj(reportData);
		report.access(ctx);
		Assert.isTrue(report.isSucceed(), "上报任务执行失败应该为成功");
		
		Logger.getAnonymousLogger().log(Level.INFO, "第三次失败任务测试……");
		
		// 这里这里停1分钟，手动修改失败时间，令失败超时（超时时间30分钟）
		Logger.getAnonymousLogger().log(Level.INFO, "已上报失败，请将change_time提前1个小时！");
		try {
			Thread.sleep(60 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// 第四次请求任务应该失败或者是其他任务
		request = new SafeHttpRequestTask(M_CH_NO, M_RK);
		request.setRequestObj(requestData);
		request.access(ctx);
		Assert.isTrue(!request.isSucceed(), "失败超时后再次请求应该失败");
		Assert.isTrue(request.getResponseObj() == null, "失败超时后再次请求应该没有任务返回");
	}
}
