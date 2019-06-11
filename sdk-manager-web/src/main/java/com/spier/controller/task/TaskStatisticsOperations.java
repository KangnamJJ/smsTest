package com.spier.controller.task;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.common.bean.ReturnEntity;
import com.spier.common.bean.db.ChannelInfo;
import com.spier.common.bean.db.ScriptInfo;
import com.spier.common.bean.db.SimInfo;
import com.spier.common.bean.db.UserInfo;
import com.spier.common.bean.db.task.TaskExecResultInfo;
import com.spier.common.bean.db.task.TaskInfo;
import com.spier.common.bean.net.task.TaskReportBeans;
import com.spier.common.bean.net.task.TaskRequestBeans;
import com.spier.common.bean.net.task.TaskRequestBeans.SimpleScriptInfo;
import com.spier.common.config.ErrorCodes;
import com.spier.common.config.GlobalConfig;
import com.spier.common.utils.AnalyseResult;
import com.spier.common.utils.DateTimeUtil;
import com.spier.common.utils.RandomUtil;
import com.spier.service.BussinessCommonService;
import com.spier.service.IPhoneInfoService;
import com.spier.service.ISimInfoService;
import com.spier.service.IUserInfoService;
import com.spier.service.channel.IChannelInfoService;
import com.spier.service.saferequest.ISafeRequestParserService;
import com.spier.service.saferequest.ISessionManageService;
import com.spier.service.task.IScriptInfoService;
import com.spier.service.task.ITaskInfoService;
import com.spier.service.task.ITaskStatisticsInfoService;

/**
 * 客户端任务请求、任务结果上报等操作的控制器
 * @author GHB
 * @version 1.0
 * @date 2019.1.16
 * 修改记录：<br>
 * 修改人：GHB<br>
 * 修改时间：2019.4.16<br>
 * 修改内容：取任务逻辑增加了一天内3次失败限制。<br>
 * 版本号：1.1<br>
 * <br>
 */
@Controller
public class TaskStatisticsOperations {

	@Autowired
	private ISafeRequestParserService mParserService;
	
	@Reference	
	private IUserInfoService mUserInfoService;
	
	@Reference	
	private ISessionManageService mSessionManageService;
	
	@Reference	
	private ITaskInfoService mTaskInfoService;
	
	@Reference	
	private ITaskStatisticsInfoService mTaskStatisticsInfoService;
	
	@Reference	
	private IScriptInfoService mScriptInfoService;
	
	@Reference
	private ISimInfoService mSimInfoService;
	
	@Reference	
	private IChannelInfoService mChanInfoService;
	
	@Reference	
	private IPhoneInfoService mPhoneInfoService;
	
	@Reference	
	private BussinessCommonService bussinessCommonService;
	
	@RequestMapping(value="/tasks/request", method={RequestMethod.POST})
	@ResponseBody
	public String onTaskRequestCalled(HttpServletRequest request) {
		AnalyseResult<TaskRequestBeans.Request, TaskRequestBeans.Response> requestData =
				mParserService.deserializeRequestFirstLayer(
						request, TaskRequestBeans.Request.class, true);
		// 如果失败，直接发送响应
		if(!requestData.mIsSucceed) {
			TaskRequestBeans.Response responseObj = new TaskRequestBeans.Response();
			responseObj.setErrorInfo("请求数据解析失败！");
			AnalyseResult<Object, TaskRequestBeans.Response> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, responseObj);
			return response.mResult;
		}
		
		// 未解析到有效数据
		if(null == requestData.mRequestObj) {
			TaskRequestBeans.Response responseObj = new TaskRequestBeans.Response();
			responseObj.setErrorInfo("请求数据解析成功，但无请求数据！");
			AnalyseResult<Object, TaskRequestBeans.Response> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, responseObj);
			return response.mResult;
		}
		
		// 查询用户是否已经注册
		UserInfo user = mUserInfoService.findRecordByKWs(
				requestData.mRequestObj.getUID(), requestData.mRequestObj.getAppName(), requestData.mChanId);
		if(null == user) {
			TaskRequestBeans.Response responseObj = new TaskRequestBeans.Response();
			responseObj.setErrorInfo("unregister");
			AnalyseResult<Object, TaskRequestBeans.Response> response = 
					mParserService.generateResponseStr(ErrorCodes.M_ERR_CODE_USER_UNREGISTER, requestData.mChanId, responseObj);
			return response.mResult;
		}
		
		// 查询用户是否已经登陆
		ReturnEntity retRst = mSessionManageService.checkSessionEstablished(
				requestData.mRequestObj.getUID(), requestData.mRequestObj.getAppName(), 
				requestData.mChanId, requestData.mRequestObj.getSessionId());
		if(!retRst.mIsSucceed) {
			TaskRequestBeans.Response responseObj = new TaskRequestBeans.Response();
			responseObj.setErrorInfo("unlogin");
			AnalyseResult<Object, TaskRequestBeans.Response> response = 
					mParserService.generateResponseStr(
							retRst.mErrorCode, requestData.mChanId, responseObj);
			return response.mResult;
		}
		
		// 取渠道信息
		ChannelInfo channel = mChanInfoService.getChanInfoByChanNo(requestData.mChanId);
		if(null == channel) {
			String msg = MessageFormat.format(
					"the channel[{0}] of user[{1}] not found", requestData.mChanId, user.getName());
			Logger.getAnonymousLogger().log(Level.SEVERE, msg);
			
			TaskRequestBeans.Response responseObj = new TaskRequestBeans.Response();
			responseObj.setErrorInfo(msg);
			
			AnalyseResult<Object, TaskRequestBeans.Response> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_WRONG_CHAN_ID, requestData.mChanId, responseObj);
			return response.mResult;
		}
		
		// 记录此用户已经取过任务
		
		
		// 检查此用户所在渠道任务开关是否已经打开
		if(!mChanInfoService.isTaskSwitchOn(requestData.mChanId)) {
			String msg = MessageFormat.format("the channel[{0}] is paused", requestData.mChanId);
			Logger.getAnonymousLogger().log(Level.INFO, msg);
			
			TaskRequestBeans.Response responseObj = new TaskRequestBeans.Response();
			responseObj.setErrorInfo(msg);
			
			AnalyseResult<Object, TaskRequestBeans.Response> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_CHAN_SWITCH_OFF, requestData.mChanId, responseObj);
			
			return response.mResult;
		}
		
		// 将提交的国家和运营商转换为统一格式
		String countryAbb = bussinessCommonService.getCountryAbb(requestData.mRequestObj.getCountry());
		if(StringUtils.isEmpty(countryAbb)) {
			String msg = MessageFormat.format(
					"no country abb found by the text[{0}]", requestData.mRequestObj.getCountry());
			Logger.getAnonymousLogger().log(Level.SEVERE, msg);
			
			TaskRequestBeans.Response responseObj = new TaskRequestBeans.Response();
			responseObj.setErrorInfo(msg);
			
			AnalyseResult<Object, TaskRequestBeans.Response> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_COUNTRY_INVALID, requestData.mChanId, responseObj);
			return response.mResult;
		}
		if(!StringUtils.equals(countryAbb, requestData.mRequestObj.getCountry())) {
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
					"国家简称【{0}】-->【{1}】", requestData.mRequestObj.getCountry(), countryAbb));
			requestData.mRequestObj.setCountry(countryAbb);
		}
		
		// 查询规范的运营商名称
		String opName = bussinessCommonService.getOperatorNameByCountryAbbAndOpText(countryAbb, requestData.mRequestObj.getOperator());
		if(StringUtils.isEmpty(opName)) {
			String msg = MessageFormat.format(
					"no carrier found by the text[{0}]", requestData.mRequestObj.getOperator());
			Logger.getAnonymousLogger().log(Level.SEVERE, msg);
			
			TaskRequestBeans.Response responseObj = new TaskRequestBeans.Response();
			responseObj.setErrorInfo(msg);
			
			AnalyseResult<Object, TaskRequestBeans.Response> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_OP_NAME_INVALID, requestData.mChanId, responseObj);
			return response.mResult;
		}
		if(!StringUtils.equals(opName, requestData.mRequestObj.getOperator())) {
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
					"运营商名称【{0}】-->【{1}】", requestData.mRequestObj.getOperator(), opName));
			requestData.mRequestObj.setOperator(opName);
		}
		
		// 检查此用户是否还有未完成的任务，暂时不理睬
		if(mTaskStatisticsInfoService.doesUserHaveUnfinishedTask(requestData.mRequestObj.getUID())) {
			Logger.getAnonymousLogger().log(Level.WARNING, 
					MessageFormat.format("用户【{0}】仍有任务未完成！", requestData.mRequestObj.getUID()));
		}
		
		// 记录用户国家和运营商信息
		if(requestData.mRequestObj.getNetworkEnv() == GlobalConfig.M_NET_ENV_MOBILE) {
			if(!recordUserCountryAndOperator(requestData.mRequestObj.getUID(), countryAbb, opName)) {
				Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
						"用户【{0}】更新国家和运营商信息失败！", requestData.mRequestObj.getUID()));
			}
		}
		
		// 先查找优先获取的任务
		TaskResult newTask = getTaskByPreferIds(requestData.mRequestObj.getPreferTaskIds());
		if(!newTask.mIsSucceed) {
			// 根据任务类型拿任务——常规任务、测试任务
			newTask = getSuitableTask(requestData.mRequestObj, channel);
		}
		if(!newTask.mIsSucceed) {
			String msg = MessageFormat.format("no suitable task found: {0}", newTask.mErrorInfo);
			Logger.getAnonymousLogger().log(Level.INFO, msg);
			
			TaskRequestBeans.Response responseObj = new TaskRequestBeans.Response();
			responseObj.setErrorInfo(msg);
			
			AnalyseResult<Object, TaskRequestBeans.Response> response = 
					mParserService.generateResponseStr(
							newTask.mErrorCode, requestData.mChanId, responseObj);
			return response.mResult;
		}
		
		// 取任务组织响应数据
		TaskRequestBeans.Response responseData = generateTaskResponse(newTask.mTask);
		if(null == responseData) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "生成任务响应数据失败！");
			
			TaskRequestBeans.Response responseObj = new TaskRequestBeans.Response();
			responseObj.setErrorInfo("failed to generate task info");
			
			AnalyseResult<Object, TaskRequestBeans.Response> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_GEN_TASK, requestData.mChanId, responseObj);
			return response.mResult;
		}
		
		// 记录任务情况
		if(!saveTaskExecState(responseData, requestData.mRequestObj, requestData.mChanId)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "failed to update task state");
			
			TaskRequestBeans.Response responseObj = new TaskRequestBeans.Response();
			responseObj.setErrorInfo("failed to update task state");
			
			AnalyseResult<Object, TaskRequestBeans.Response> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_CHANGE_TASK_STATE, requestData.mChanId, responseObj);
			return response.mResult;
		}
		
		// 下发任务
		AnalyseResult<Object, TaskRequestBeans.Response> response = 
				mParserService.generateResponseStr(
						ErrorCodes.M_ERR_CODE_SUCCEED, requestData.mChanId, responseData);
		return response.mResult;
	}
	
	private boolean recordUserCountryAndOperator(String uid, String country, String op) {
		if(StringUtils.isEmpty(uid)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "uid为空，停止记录用户所在国家和运营商");
			return false;
		}
		
		return mPhoneInfoService.updateCountryAndOp(uid, country, op);
	}
	
	private TaskResult getTaskByPreferIds(List<String> ids) {
		TaskResult res = TaskResult.getDefault();
		
		if(null == ids || ids.isEmpty()) {
			res.mIsSucceed = false;
			return res;
		}
		
		List<TaskInfo> tasks = mTaskInfoService.getTaskByIds(ids);
		if(tasks == null || tasks.isEmpty()) {
			return res;
		}
		
		for(TaskInfo info : tasks) {
			if(null == info) {
				continue;
			}
			
			if(info.getTaskFinishedCount() >= info.getTaskTotalCount()) {
				continue;
			}
			
			res = TaskResult.getDefault();
			res.mTask = info;
			res.mIsSucceed = true;
			
			break;
		}
		
		return res;
	}
	
	private TaskResult getSuitableTask(TaskRequestBeans.Request request, ChannelInfo channel) {
		TaskResult res = TaskResult.getDefault();
		
		if(null == request) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "request为null，无法查询线上任务！");
			res.mErrorCode = ErrorCodes.M_ERR_CODE_NO_OUTLAYER_REQUEST;
			res.mErrorInfo = "request为null，无法查询线上任务！";
			res.mIsSucceed = false;
			return res;
		}
		
		switch(request.getTaskType()) {
			case GlobalConfig.M_TASK_TYPE_TEST:
				res = getTestTask(request);
			break;
			
			case GlobalConfig.M_TASK_TYPE_ONLINE:
				res = getOnlineTask(request, channel);
			break;
		}
		
		return res;
	}
	
	private TaskResult getTestTask(TaskRequestBeans.Request request) {
		TaskResult res = TaskResult.getDefault();
		
		res.mErrorInfo = "根据国家、运营商、网络类型未找到匹配的测试任务！";
		res.mIsSucceed = false;
		res.mErrorCode = ErrorCodes.M_ERR_CODE_NO_SUITABLE_TASK;
		
		if(null == request) {
			res.mErrorInfo = "request为null，无法查询测试任务！";
			return res;
		}
		
		List<TaskInfo> tasks = mTaskInfoService.getTasksByTaskType(GlobalConfig.M_TASK_TYPE_TEST);
		if(tasks.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, "无测试任务！");
			res.mErrorInfo = "后台无测试任务！";
			return res;
		}
		
		for(TaskInfo task : tasks) {
			if(null == task) {
				continue;
			}
			
			List<ScriptInfo> scripts = mScriptInfoService.findScriptInfosByScriptId(task.getScriptId());
			if(scripts.isEmpty()) {
				continue;
			}
			
			for(ScriptInfo script : scripts) {
				if(null == script) {
					continue;
				}
				
				if(!StringUtils.equals(script.getCountryAbb(), request.getCountry())) {
					Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
							"国家【{0}】---【{1}】不匹配，跳过测试任务【{2}】！", 
							script.getCountryAbb(), request.getCountry(), script.getScriptId()));
					continue;
				}
				
				if(!StringUtils.equals(script.getOperator(), request.getOperator())) {
					Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
							"运营商【{0}】---【{1}】不匹配，跳过测试任务【{2}】！", 
							script.getOperator(), request.getOperator(), script.getScriptId()));
					continue;
				}
				
				if(script.getNetEnv() != request.getNetworkEnv()) {
					if(script.getNetEnv() != GlobalConfig.M_NET_ENV_ALL) {
						Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
								"网络类型【{0}】---【{1}】不匹配，跳过测试任务！", 
								script.getNetEnv(), request.getNetworkEnv(), script.getScriptId()));
						continue;
					}
				}
				
				res.mErrorCode = ErrorCodes.M_ERR_CODE_SUCCEED;
				res.mIsSucceed = true;
				res.mErrorInfo = "";
				res.mTask = task;
				break;
			}
		}
		
		return res;
	}
	
	private TaskResult getOnlineTask(TaskRequestBeans.Request request, ChannelInfo channel) {
		TaskResult res = TaskResult.getDefault();
		
		if(null == request) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "request为null，无法查询线上任务！");
			res.mErrorCode = ErrorCodes.M_ERR_CODE_NO_OUTLAYER_REQUEST;
			res.mErrorInfo = "request为null，无法查询线上任务！";
			res.mIsSucceed = false;
			return res;
		}
		
		List<String> testTaskId = getTestTaskId();
		
		// 获取此用户所有任务
		List<TaskExecResultInfo> userTasks = getNormalTasksByUser(request.getUID(), testTaskId);
		// 获取成功完成的任务
		List<TaskExecResultInfo> userSucceedTasks = getUserSucceedTasks(userTasks);
		// 获取用户失败的任务
		List<TaskExecResultInfo> userFailedTasks = getUserFailedTasks(userTasks);
		// 获取用户最近完成的任务
		TaskExecResultInfo userRecentSucceedTask = getUserRecentTask(userSucceedTasks);
		
		// 检查此用户是否已经达到任务上限
		if(userSucceedTasks.size() >= channel.getSingleUserMaxTasks()) {
			Logger.getAnonymousLogger().log(Level.INFO, "该用户已经达到任务上限！");
			res.mIsSucceed = false;
			res.mErrorCode = ErrorCodes.M_ERR_CODE_USER_MAX_TASKS;
			res.mErrorInfo = MessageFormat.format(
					"this user's succeed tasks exceed max limit[{0}]", channel.getSingleUserMaxTasks());
			return res;
		}
		
		// 检查此用户是否已经达到任务上限
//		List<TaskExecResultInfo> userFinishedTasks = 
//				mTaskStatisticsInfoService.getUserFinishedTaskList(request.getUID());
//		if(userFinishedTasks.size() >= channel.getSingleUserMaxTasks()) {
//			Logger.getAnonymousLogger().log(Level.INFO, "该用户已经达到任务上限！");
//			res.mIsSucceed = false;
//			res.mErrorCode = ErrorCodes.M_ERR_CODE_USER_MAX_TASKS;
//			res.mErrorInfo = "该用户已经达到任务上限！";
//			return res;
//		}
		
		// 根据国家、运营商、网络类型查询任务表，获取可用任务列表
		List<TaskInfo> tasksList = getUncompletedTasksByKeyInfo(request.getCountry(), 
				request.getOperator(), request.getNetworkEnv(), userSucceedTasks);
		if(tasksList.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, "根据国家、运营商、网络类型、已完成任务数量未查到适合的任务列表！");
			
			res.mErrorCode = ErrorCodes.M_ERR_CODE_NO_SUITABLE_TASK;
			res.mErrorInfo = "no suitable task found according to country, carrier and network type";
			res.mIsSucceed = false;
			return res;
		}
		
		// 根据任务成功时间筛选任务：
		// 1天之内，有一个成功了则不再做任务了, <!--Timo 2019/3/29 这块需求实现被注释掉了-->
		List<TaskInfo> suitableTasks = filterTasksBySucceedInterval(tasksList, userRecentSucceedTask);
		if(suitableTasks.isEmpty()) {
			res.mErrorCode = ErrorCodes.M_ERR_CODE_NO_SUITABLE_TASK;
			res.mErrorInfo = "no suitable task found according to succeed tasks";
			res.mIsSucceed = false;
			return res;
		}
		
		// 根据用户最近一次任务失败时间筛选任务：
		// 不同任务之间，前一个任务失败了，30分钟内不给任务
		// 同一任务一天之内最多重试3次
		// 一天失败最多3次不给任务了
		suitableTasks = filterTasksByFailedInterval(suitableTasks, userFailedTasks);
		if(suitableTasks.isEmpty()) {
			res.mErrorCode = ErrorCodes.M_ERR_CODE_NO_SUITABLE_TASK;
			res.mErrorInfo = "no suitable task found according to failed tasks";
			res.mIsSucceed = false;
			return res;
		}
		
		// 去掉同一个短号的任务
		suitableTasks = filterTasksByShortCode(suitableTasks, userSucceedTasks);
		if(suitableTasks.isEmpty()) {
			res.mErrorCode = ErrorCodes.M_ERR_CODE_NO_SUITABLE_TASK;
			res.mErrorInfo = "no suitable task found according to short code";
			res.mIsSucceed = false;
			return res;
		}
		
		// 优先选择没做过的任务（没失败过的），如果都做过了，则从列表中随机挑选
		TaskInfo selectedTask = siftingSuitableTask(suitableTasks, userFailedTasks);
		if(null == selectedTask) {
			res.mErrorCode = ErrorCodes.M_ERR_CODE_NO_SUITABLE_TASK;
			res.mErrorInfo = "no suitable task found by the final sifting";
			res.mIsSucceed = false;
			return res;
		}
		
		res.mIsSucceed = true;
		res.mErrorCode = ErrorCodes.M_ERR_CODE_SUCCEED;
		res.mTask = selectedTask;
		
		return res;
	}
    //获取test任务的taksid
	private List<String> getTestTaskId() {
		List<TaskInfo> testTasks = mTaskInfoService.getTasksByTaskType(GlobalConfig.M_TASK_TYPE_TEST);
		List<String> testTaskId = new ArrayList<>();
		if(testTasks != null && testTasks.size()>0) {
			for (TaskInfo taskInfo : testTasks) {
				testTaskId.add(taskInfo.getTaskId());
			}
		}
		return testTaskId;
	}
	
	private List<TaskInfo> getUncompletedTasksByKeyInfo(String country, String op, int netEnv, 
			List<TaskExecResultInfo> finished) {
		List<TaskInfo> res = mTaskInfoService.getTaskListByKeyInfo(country, op, netEnv);
		if(res.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "根据国家、运营商、网络类型未查到合适的任务！");
			return res;
		}
		
		// 根据任务总数和完成数筛选还未完成的任务，并去除未启动的任务
		List<TaskInfo> tmpList = new ArrayList<TaskInfo>();
		for(TaskInfo info : res) {
			if(null == info) {
				continue;
			}
			
			// 任务总数已完成
			if(info.getTaskFinishedCount() >= info.getTaskTotalCount()) {
				continue;
			}
			
			boolean hasThisTaskFinished = false;
			for(TaskExecResultInfo fTask : finished) {
				if(null == fTask) {
					continue;
				}
				
				if(StringUtils.equals(fTask.getTaskId(), info.getTaskId())) {
					hasThisTaskFinished = true;
					break;
				}
			}
			// 这个任务已经做过了
			if(hasThisTaskFinished) {
				continue;
			}
			
			// 检查这个任务是否在运行
			if(info.getTaskState() == GlobalConfig.M_TASK_STATE_PAUSE) {
				continue;
			}
			
			tmpList.add(info);
		}
		
		res = tmpList;
		
		return res;
	}
	/**
	 * 
	 * @param uid 
	 * @param onlineTasks  
	 * @return
	 */
	private List<TaskExecResultInfo> getNormalTasksByUser(String uid, List<String> testTasks) {
		List<TaskExecResultInfo> res = new ArrayList<>();
		if(StringUtils.isEmpty(uid)) {
			return res;
		}
		res = mTaskStatisticsInfoService.getTasksByUser(uid);
		for(Iterator<TaskExecResultInfo> it = res.iterator(); it.hasNext();) {
			TaskExecResultInfo info = it.next();
			if(null == info) {
				continue;
			}
			if(testTasks.contains(info.getTaskId())){
			   it.remove();
			} 
		}
		return res;
	}
	
	private List<TaskExecResultInfo> getUserSucceedTasks(List<TaskExecResultInfo> tasks) {
		List<TaskExecResultInfo> res = new ArrayList<TaskExecResultInfo>();
		
		if(null == tasks || tasks.isEmpty()) {
			return res;
		}
		
		for(TaskExecResultInfo info : tasks) {
			if(null == info) {
				continue;
			}
			
			if(GlobalConfig.isTaskExecSucceed(info.getTaskRunningState())) {
				res.add(info);
			}
		}
		
		return res;
	}
	
	private List<TaskExecResultInfo> getUserFailedTasks(List<TaskExecResultInfo> tasks) {
		List<TaskExecResultInfo> res = new ArrayList<TaskExecResultInfo>();
		
		if(null == tasks || tasks.isEmpty()) {
			return res;
		}
		
		for(TaskExecResultInfo info : tasks) {
			if(null == info) {
				continue;
			}
			
			if(info.getTaskRunningState() == GlobalConfig.M_TASK_EXEC_RESULT_FAILED) {
				res.add(info);
			}
		}
		
		return res;
	}
	
	private TaskExecResultInfo getUserRecentTask(List<TaskExecResultInfo> tasks) {
		TaskExecResultInfo res = null;
		
		if(null == tasks || tasks.isEmpty()) {
			return res;
		}
		
		for(TaskExecResultInfo execInfo : tasks) {
			if(null == execInfo) {
				continue;
			}
			
			if(null == execInfo.getStateChangedTime()) {
				continue;
			}
			
			if(null == res) {
				res = execInfo;
				continue;
			}
			
			if(res.getStateChangedTime().before(execInfo.getStateChangedTime())) {
				res = execInfo;
			}
		}
		
		return res;
	}
	
	// 成功任务时间间隔：1天
	private static final long M_MIN_SUCCEED_TASK_INTERVAL = 1;
	
	private List<TaskInfo> filterTasksBySucceedInterval(List<TaskInfo> tasks, TaskExecResultInfo recentSucceedTask) {
		List<TaskInfo> res = new ArrayList<TaskInfo>();
		
		if(null == tasks || tasks.isEmpty()) {
			return res;
		}
		
		if(null == recentSucceedTask) {
			return tasks;
		}
		
		if(null == recentSucceedTask.getStateChangedTime()) {
			return tasks;
		}
		
		for(TaskInfo info : tasks) {
			if(info == null) {
				continue;
			}
			
			// 这里去掉这个逻辑，以后有必要再加
//			if(DateTimeUtil.addDate(recentSucceedTask.getStateChangedTime(), M_MIN_SUCCEED_TASK_INTERVAL, "08:00:00")
//					.before(new Timestamp(System.currentTimeMillis()))) {
//				res.add(info);
//			}
			res.add(info);
		}
		
		return res;
	}
	
	private static final long M_MIN_FAILURE_INTERVAL_IN_MINUTE = 30;
	// 当天失败错误上限
	private static final int M_MAX_FAILAURE_TIMES_TODAY = 3;
	// 用户失败错误上限
	private static final int M_MAX_FAILURE_TIMES_LIFETIME = 50;
	
	private List<TaskInfo> filterTasksByFailedInterval(List<TaskInfo> tasks, List<TaskExecResultInfo> failedTasks) {
		List<TaskInfo> res = new ArrayList<TaskInfo>();
		
		if(null == tasks || tasks.isEmpty()) {
			return res;
		}
		
		if(null == failedTasks || failedTasks.isEmpty()) {
			return tasks;
		}
		
		// 最近一次失败，30分钟内不给任务
		TaskExecResultInfo recentFailedTask = getUserRecentTask(failedTasks);
		if(null != recentFailedTask) {
			if(DateTimeUtil.addMinute(recentFailedTask.getStateChangedTime(), M_MIN_FAILURE_INTERVAL_IN_MINUTE)
					.after(new Timestamp(System.currentTimeMillis()))) {
				Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
						"最近失败时间【{0}】，失败超时时间【{1}】", 
						recentFailedTask.getStateChangedTime(), 
						DateTimeUtil.addMinute(recentFailedTask.getStateChangedTime(), M_MIN_FAILURE_INTERVAL_IN_MINUTE)));
				Logger.getAnonymousLogger().log(Level.INFO, "最近一次任务失败不超过30分钟，不派发任务");
				return res;
			}
		}
		
		// 一天内失败次数过多就不给任务了
		int failedCountToday = 0;
		for(TaskExecResultInfo info : failedTasks) {
			if(null == info) {
				continue;
			}
			
			if(info.getStateChangedTime().before(
					DateTimeUtil.addDate(new Timestamp(System.currentTimeMillis()), 1, "00:00:00"))) {
				failedCountToday++;
			}
			
			if(failedCountToday >= M_MAX_FAILAURE_TIMES_TODAY) {
				Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
						"用户【{0}】当天失败任务已经达到上限，不派发任务", info.getUserId()));
				break;
			}
		}
		if(failedCountToday >= M_MAX_FAILAURE_TIMES_TODAY) {
			return res;
		}
		
		// 去掉一天失败次数超过上限的任务
		for(TaskInfo task : tasks) {
			if(null == task) {
				continue;
			}
			
			String userName = "";
			boolean exceedMaxTimes = false;
			int count = 0;
			for(TaskExecResultInfo failed : failedTasks) {
				if(null == failed) {
					continue;
				}
				
				if(!StringUtils.equals(task.getTaskId(), failed.getTaskId())) {
					continue;
				}
				
				userName = failed.getUserId();
				
				count++;
				
				if(count >= M_MAX_FAILURE_TIMES_LIFETIME) {
					exceedMaxTimes = true;
					break;
				}
			}
			
			if(exceedMaxTimes) {
				Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
						"用户【{0}】的任务【{1}】失败次数已经超过当天限制，跳过此任务！", 
						userName, task.getTaskId()));
				continue;
			}
			
			res.add(task);
		}
		if(res.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, "用户所有任务失败次数都超过上限！");
			return res;
		}
		
		return res;
	}
	
	private List<TaskInfo> filterTasksByShortCode(List<TaskInfo> tasks, List<TaskExecResultInfo> succeedTasks) {
		List<TaskInfo> res = new ArrayList<TaskInfo>();
		
		if(null == tasks || tasks.isEmpty()) {
			return res;
		}
		
		if(null == succeedTasks || succeedTasks.isEmpty()) {
			return tasks;
		}
		
		List<ScriptInfo> scripts = mScriptInfoService.getAllInfo();
		List<TaskInfo> allTasks = mTaskInfoService.getAllTasks();
		
		// 取成功任务的短号
		List<String> allSucceedTaskShortcodes = new ArrayList<String>();
		for(TaskExecResultInfo execRst : succeedTasks) {
			if(null == execRst) {
				continue;
			}
			
			TaskInfo task = null;
			for(TaskInfo info : allTasks) {
				if(null == info) {
					continue;
				}
				
				if(StringUtils.equals(info.getTaskId(), execRst.getTaskId())) {
					task = info;
					break;
				}
			}
			if(null == task) {
				continue;
			}
			
			// 取短号
			String shortCode = null;
			for(ScriptInfo script : scripts) {
				if(null == script) {
					continue;
				}
				
				if(StringUtils.equals(task.getScriptId(), script.getScriptId())) {
					shortCode = script.getShortCode();
					break;
				}
			}
			if(StringUtils.isEmpty(shortCode)) {
				continue;
			}
			
			if(!allSucceedTaskShortcodes.contains(shortCode)) {
				allSucceedTaskShortcodes.add(shortCode);
			}
		}
		
		// 过滤短号相同的任务
		for(TaskInfo task : tasks) {
			if(null == task) {
				continue;
			}
			
			// 取短号
			String shortCode = null;
			for(ScriptInfo script : scripts) {
				if(null == script) {
					continue;
				}
				
				if(StringUtils.equals(script.getScriptId(), task.getScriptId())) {
					shortCode = script.getShortCode();
					break;
				}
			}
			
			// 为了兼容以前的数据，如果脚本没有录入shortcode，则放行。
			if(StringUtils.isEmpty(shortCode)) {
				res.add(task);
				continue;
			}
			
			if(!allSucceedTaskShortcodes.contains(shortCode)) {
				res.add(task);
				continue;
			}
		}
		
		return res;
	}
	
	private TaskInfo siftingSuitableTask(List<TaskInfo> suitableTasks, List<TaskExecResultInfo> failedTasks) {
		TaskInfo res = null;
		
		if(null == suitableTasks || suitableTasks.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, "suitableTasks is null or empty, no suitable task sifted.");
			return res;
		}
		
		// 去掉失败过的
		List<TaskInfo> toBeSiftedList = new ArrayList<TaskInfo>(suitableTasks);
		if(null != failedTasks && failedTasks.isEmpty()) {
			for(Iterator<TaskInfo> it = toBeSiftedList.iterator(); it.hasNext();) {
				TaskInfo t = it.next();
				if(null == t) {
					continue;
				}
				
				boolean failed = false;
				for(TaskExecResultInfo failedInfo : failedTasks) {
					if(null == failedInfo) {
						continue;
					}
					
					if(StringUtils.equals(failedInfo.getTaskId(), t.getTaskId())) {
						failed = true;
						break;
					}
				}
				
				if(failed) {
					Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
							"任务【{0}】已经失败过，移除", t.getTaskId()));
					it.remove();
				}
			}
		}
		
		// 已经没有未做过的，只能在现有的随便挑了
		if(toBeSiftedList.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.INFO, "经过筛选，可选任务列表已空，从失败任务中随机派发");
			toBeSiftedList = new ArrayList<TaskInfo>(suitableTasks);
		} else {
			Logger.getAnonymousLogger().log(Level.INFO, "还有未失败任务，优先派发！");
		}
		
		// 随机
		int ind = RandomUtils.nextInt(0, toBeSiftedList.size());
		res = toBeSiftedList.get(ind);
		
		return res;
	}
	
	private TaskRequestBeans.Response generateTaskResponse(TaskInfo task) {
		TaskRequestBeans.Response res = new TaskRequestBeans.Response();
		res.setTaskId(task.getTaskId());
		res.setTaskFlowId(System.currentTimeMillis() + RandomUtil.getRandomStr(5));
		res.setScriptId(task.getScriptId());
		
		List<ScriptInfo> scriptList = mScriptInfoService.findScriptInfosByScriptId(task.getScriptId());
		if(scriptList.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"根据脚本id【{0}】未找到对应的脚本记录，无法生成任务！", task.getScriptId()));
			return null;
		}
		
		SimpleScriptInfo info = new SimpleScriptInfo();
		info.setHashCode(scriptList.get(0).getHash());
		info.setVersionCode(scriptList.get(0).getVersionCode());
		
		res.setScriptInfo(info);
		
		return res;
	}
	
	private boolean saveTaskExecState(TaskRequestBeans.Response dataToExec, TaskRequestBeans.Request request, String chanId) {
		if(null == dataToExec) {
			return false;
		}
		
		if(null == request) {
			return false;
		}
		
		TaskExecResultInfo execRecord = new TaskExecResultInfo();
		execRecord.setAppName(request.getAppName());
		execRecord.setChanId(chanId);
		execRecord.setPhoneNum(request.getPhoneNumber());
		execRecord.setTaskFlowId(dataToExec.getTaskFlowId());
		execRecord.setTaskId(dataToExec.getTaskId());
		execRecord.setTaskRunningState(GlobalConfig.M_TASK_RUNNING_STATE_RUNNING);
		execRecord.setUserId(request.getUID());
		
		return mTaskStatisticsInfoService.addOrUpdateTaskExecStateRecord(execRecord);
	}
	
	@RequestMapping(value="/tasks/report", method={RequestMethod.POST})
	@ResponseBody
	public String onTaskResultReport(HttpServletRequest request) {
		AnalyseResult<TaskReportBeans.Request, TaskReportBeans.Response> requestData = 
				mParserService.deserializeRequestFirstLayer(
						request, TaskReportBeans.Request.class, true);
		// 如果失败，直接发送响应
		if(!requestData.mIsSucceed) {
			AnalyseResult<Object, TaskReportBeans.Response> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 未解析到有效数据
		if(null == requestData.mRequestObj) {
			AnalyseResult<Object, TaskReportBeans.Response> response = 
					mParserService.generateResponseStr(
							requestData.mErrCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 查询用户是否已经注册
		UserInfo user = mUserInfoService.findRecordByKWs(
				requestData.mRequestObj.getUserId(), requestData.mRequestObj.getAppName(), requestData.mChanId);
		if(null == user) {
			AnalyseResult<Object, TaskReportBeans.Response> response = 
					mParserService.generateResponseStr(ErrorCodes.M_ERR_CODE_USER_UNREGISTER, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 查询用户是否已经登陆
		ReturnEntity retRst = mSessionManageService.checkSessionEstablished(
				requestData.mRequestObj.getUserId(), requestData.mRequestObj.getAppName(), 
				requestData.mChanId, requestData.mRequestObj.getSessionId());
		if(!retRst.mIsSucceed) {
			AnalyseResult<Object, TaskReportBeans.Response> response = 
					mParserService.generateResponseStr(
							retRst.mErrorCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 统一国家简称和运营商信息
		String countryAbb = bussinessCommonService.getCountryAbb(requestData.mRequestObj.getCountry());
		if(!StringUtils.equals(countryAbb, requestData.mRequestObj.getCountry())) {
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
					"国家简称【{0}】-->【{1}】", requestData.mRequestObj.getCountry(), countryAbb));
			requestData.mRequestObj.setCountry(countryAbb);
			
		}
		String opName = bussinessCommonService.getOperatorNameByCountryAbbAndOpText(countryAbb, requestData.mRequestObj.getOp());
		if(!StringUtils.equals(opName, requestData.mRequestObj.getOp())) {
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
					"运营商名称【{0}】-->【{1}】", requestData.mRequestObj.getOp(), opName));
			requestData.mRequestObj.setOp(opName);
		}
		
		// 检查任务id是否存在
		TaskInfo taskInfo = mTaskInfoService.findRecordByTaskId(requestData.mRequestObj.getTaskId());
		if(null == taskInfo) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"任务id【{0}】没有对应的任务信息，不接受任务上报。", requestData.mRequestObj.getTaskId()));
			AnalyseResult<Object, TaskReportBeans.Response> response = 
					mParserService.generateResponseStr(
							retRst.mErrorCode, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 检查任务流水是否存在
		TaskExecResultInfo taskFlow = mTaskStatisticsInfoService.getTaskFlowInfoByFlowId(requestData.mRequestObj.getTaskFlowId());
		if(null == taskFlow) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"流水号为【{0}】没有对应的流水信息，不接受任务上报。", requestData.mRequestObj.getTaskFlowId()));
			AnalyseResult<Object, TaskReportBeans.Response> response = 
					mParserService.generateResponseStr(
							ErrorCodes.M_ERR_CODE_INVALID_FLOW_ID, requestData.mChanId, null);
			return response.mResult;
		}
		
		// 更新任务信息表
		if(GlobalConfig.isTaskExecSucceed(requestData.mRequestObj.getSucceed())) {
			if(!updateTaskInfoFinishedCount(taskInfo)) {
				Logger.getAnonymousLogger().log(Level.WARNING, "更新任务信息表失败！");
			}
		}
		
		// 更新任务流水统计表
		boolean succeed = updateTaskFlowState(taskFlow, requestData.mRequestObj, requestData.mChanId);
		if(!succeed) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"更新任务信息流水信息【{0}】失败！", taskFlow.getTaskFlowId()));
		}
		
		// 更新用户表
		succeed = updateUserPhoneNum(user, requestData.mRequestObj.getPhoneNumber());
		if(!succeed) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "更新用户手机号码失败！");
		}
		
		
		// 更新用户手机号、国家、运营商信息
		succeed = mergeSimInfo(requestData.mRequestObj, user);
		if(!succeed) {
			Logger.getAnonymousLogger().log(Level.WARNING, "sim卡信息融合失败！");
		}
		
		AnalyseResult<Object, TaskReportBeans.Response> response = 
				mParserService.generateResponseStr(
						ErrorCodes.M_ERR_CODE_SUCCEED, requestData.mChanId, null);
		return response.mResult;
	}
	
	private boolean updateTaskInfoFinishedCount(TaskInfo task) {
		if(null == task) {
			return false;
		}
		
		task.setTaskFinishedCount(task.getTaskFinishedCount() + 1);
		
		return mTaskInfoService.recordTaskFinishedCount(task.getTaskId(), task.getTaskFinishedCount());
	}
	
	private boolean updateTaskFlowState(TaskExecResultInfo flowInfo, TaskReportBeans.Request request, String chanId) {
		if(null == flowInfo) {
			return false;
		}
		
		if(null == request) {
			return false;
		}
		
		// 状态多了，由客户端定义，此处的判定不需要了
//		if(!GlobalConfig.isTaskExecResultValid(request.getSucceed())) {
//			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
//					"任务状态【{0}】为异常值，无法更新任务执行状态！", request.getSucceed()));
//			return false;
//		}
		
		if(StringUtils.isEmpty(flowInfo.getAppName())) {
			flowInfo.setAppName(request.getAppName());
		}
		
		if(StringUtils.isEmpty(flowInfo.getChanId())) {
			flowInfo.setChanId(chanId);
		}
		
		if(StringUtils.isEmpty(flowInfo.getPhoneNum())) {
			flowInfo.setPhoneNum(request.getPhoneNumber());
		}
		
		if(StringUtils.isEmpty(flowInfo.getUserId())) {
			flowInfo.setUserId(request.getUserId());
		}
		
		flowInfo.setTaskRunningState(convTaskRstReport2DB(request.getSucceed()));
		
		return mTaskStatisticsInfoService.addOrUpdateTaskExecStateRecord(flowInfo);
	}
	
	private boolean updateUserPhoneNum(UserInfo user, String phoneNum) {
		if(null == user) {
			return false;
		}
		
		if(StringUtils.isEmpty(phoneNum)) {
			return false;
		}
		
		if(StringUtils.equals(phoneNum, user.getPhoneNumber())) {
			return true;
		}
		
		user.setPhoneNumber(phoneNum);
		
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("用户【{0}】添加手机号码【{0}】", phoneNum));
		
		return mUserInfoService.updateInfoRecordByInd(user.getId(), user);
	}
	
	private boolean mergeSimInfo(TaskReportBeans.Request request, UserInfo user) {
		if(null == request) {
			return false;
		}
		
		if(null == user) {
			return false;
		}
		
		
		String phoneNum = (StringUtils.isEmpty(request.getPhoneNumber()) ? user.getPhoneNumber() : request.getPhoneNumber());
		if(StringUtils.isEmpty(phoneNum)) {
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format(
					"用户【{0}】没有手机号码，不更新sim信息 表！", user.getName()));
			return false;
		}
		
		boolean res = false;
		int ind = mSimInfoService.findRecordIndByNumber(phoneNum);
		if(ind <= 0) {
			SimInfo simcard = new SimInfo();
			simcard.setCountry(request.getCountry());
			simcard.setOperator(request.getOp());
			simcard.setNumber(phoneNum);
			res = mSimInfoService.addSimInfoRecord(simcard);
		} else {
			SimInfo simcard = mSimInfoService.findSimInfoRecordByInd(ind);
			if(!StringUtils.isEmpty(request.getCountry())) {
				simcard.setCountry(request.getCountry());
			}
			if(!StringUtils.isEmpty(request.getOp())) {
				simcard.setOperator(request.getOp());
			}
			res = mSimInfoService.updateInfoByInd(ind, simcard);
		}
		
		return res;
	}
	
	private int convTaskRstReport2DB(int rst) {
		int res = GlobalConfig.M_TASK_RUNNING_STATE_RUNNING;
		switch(rst) {
			case GlobalConfig.M_TASK_EXEC_RESULT_FAILED:
				res = GlobalConfig.M_TASK_RUNNING_STATE_FAILED;
			break;
			
			case GlobalConfig.M_TASK_EXEC_RESULT_SUCCEED:
				res = GlobalConfig.M_TASK_RUNNING_STATE_SUCCEED;
			break;
			
			default:
				Logger.getAnonymousLogger().log(Level.WARNING, MessageFormat.format(
						"收到的任务执行结果【{0}】不满足转换条件，任务将保持原有状态！", rst));
			break;
		}
		
		return res;
	}
	
	private static class TaskResult extends ReturnEntity {
		public TaskInfo mTask;
		
		public static TaskResult getDefault() {
			TaskResult res = new TaskResult();
			res.mTask = null;
			res.mIsSucceed = false;
			res.mErrorInfo = "no initialized!";
			res.mErrorCode = ErrorCodes.M_ERR_CODE_SUCCEED;
			
			return res;
		}
	}
}
