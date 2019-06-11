package com.spier.controller.task;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.common.bean.db.CountryInfo;
import com.spier.common.bean.db.OperatorInfo;
import com.spier.common.bean.db.ScriptInfo;
import com.spier.common.bean.db.task.TaskInfo;
import com.spier.common.utils.BussinessCommonUtil;
import com.spier.service.BussinessCommonService;
import com.spier.service.ICountriesInfoService;
import com.spier.service.IOperatorsInfoService;
import com.spier.service.channel.IChannelInfoService;
import com.spier.service.task.IScriptInfoService;
import com.spier.service.task.ITaskInfoService;

/**
 * 任务管理相关操作的控制器
 * @author GHB
 * @version 1.0
 * @date 2019.1.8
 */
@Controller
public class TasksOperations {

	private static final String M_PAGE_TASKS_HOME = "taskshome";
	private static final String M_PAGE_TASKS_EDIT = "taskedit";
	
	@Reference
	private ITaskInfoService mTaskInfoService;
	
	@Reference
	private IChannelInfoService mChanInfoService;
	
	@Reference
	private ICountriesInfoService mCountriesInfoService;
	
	@Reference
	private IOperatorsInfoService mOperatorsInfoService;
	
	@Reference
	private IScriptInfoService mScriptsInfoService;
	
	@Reference	
	private BussinessCommonService bussinessCommonService;
	
	@RequestMapping(value = "tasks/home", method = RequestMethod.GET)
	public String onTasksHomeLoaded(Model model, HttpServletRequest request) {
		return procHomeLoaded(model);
	}
	
	@RequestMapping(value = "tasks/edit", method = {RequestMethod.GET, RequestMethod.POST}, produces ="text/json;charset=UTF-8")
	public String onTaskEditLoaded(Model model, HttpServletRequest request) {
		String action = request.getParameter("action");
		
		boolean isDeleteRecord = false;
		boolean isAddRecord = false;
		boolean isCommitRecord = false;
		boolean isEdit = false;
		
		if(StringUtils.equals(action, "commit")) {
			isCommitRecord = true;
		} else if(StringUtils.equals(action, "delete")) {
			isDeleteRecord = true;
		} else if(StringUtils.equals(action, "add")) {
			isAddRecord = true;
		} else if(StringUtils.equals(action, "edit")) {
			isEdit = true;
		}
		
		String res = M_PAGE_TASKS_EDIT;
		
		if(isCommitRecord) {
			res = procCommitTaskInfo(model, request);
		} else if(isAddRecord) {
			res = procAdd(model, request);
		} else if(isDeleteRecord) {
			res = procDelete(model, request);
		} else if(isEdit) {
			res = procEdit(model, request);
		}
		
		return res;
	}
	
	private String procHomeLoaded(Model model) {
		List<TaskInfo> allTasks = mTaskInfoService.getAllTasks();
		if(null == allTasks || allTasks.isEmpty()) {
			model.addAttribute("msg", "还没有任务，请点击按钮新增任务");
			return M_PAGE_TASKS_HOME;
		}
		
		model.addAttribute("taskList", allTasks);
		model.addAttribute("taskStates", BussinessCommonUtil.getInstance().getTaskStateSelections());
		model.addAttribute("netStates", BussinessCommonUtil.getInstance().getNetEnvSelections());
		model.addAttribute("scripts", bussinessCommonService.getScriptsByTasks(allTasks));
		model.addAttribute("testSlections", BussinessCommonUtil.getInstance().getTaskTestSelections());
		
		return M_PAGE_TASKS_HOME;
	}
	
	private String procDelete(Model model, HttpServletRequest request) {
		String index = request.getParameter("ind");
		int ind = -1;
		try {
			ind = Integer.parseInt(index);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			model.addAttribute("msg", "ind[{0}]转换整形失败，无法删除记录");
			return M_PAGE_TASKS_EDIT;
		}
		
		mTaskInfoService.deleteRecordByIndex(ind);
		
		model.addAttribute("msg", "记录已删除！");
		
		return M_PAGE_TASKS_EDIT;
	}
	
	private String procAdd(Model model, HttpServletRequest request) {
		// 渠道号
//		List<ChannelInfo> chanList = mChanInfoService.getAll();
//		model.addAttribute("chanList", chanList);
//		
//		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("共有【{0}】条渠道数据", chanList.size()));
//		for(ChannelInfo info : chanList) {
//			if(info == null) {
//				Logger.getAnonymousLogger().log(Level.INFO, "java：单元为空！");
//			}
//		}
		
		// 任务类型
		model.addAttribute("testSlections", BussinessCommonUtil.getInstance().getTaskTestSelections());
		
		// 国家
		List<CountryInfo> countrires = mCountriesInfoService.getAll();
		model.addAttribute("countries", countrires);
		
		// 运营商
		List<OperatorInfo> operators = mOperatorsInfoService.getAll();
		Map<String, List<OperatorInfo>> opMap = getOperatorsMap(operators);
		model.addAttribute("operators", opMap);
		
		// 所有脚本
		List<ScriptInfo> scripts = mScriptsInfoService.getAllInfo();
		model.addAttribute("scripts", scripts);
		
		// 结算类型
		List<String> payoutTypes = BussinessCommonUtil.getInstance().getPayoutTypes();
		model.addAttribute("payoutTypes", payoutTypes);
		
		return M_PAGE_TASKS_EDIT;
	}
	
	private String procEdit(Model model, HttpServletRequest request) {
		String indStr = request.getParameter("ind");
		if(StringUtils.isEmpty(indStr)) {
			model.addAttribute("msg", "请求参数中无序列号，无法编辑任务");
			return M_PAGE_TASKS_EDIT;
		}
		
		int ind = -1;
		try {
			ind = Integer.parseInt(indStr);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if(ind <= 0) {
			model.addAttribute("msg", MessageFormat.format("请求参数中的序列号【{0}】无效，无法编辑任务", ind));
			return M_PAGE_TASKS_EDIT;
		}
		
		// 任务类型
		model.addAttribute("testSlections", BussinessCommonUtil.getInstance().getTaskTestSelections());
		
		// 取任务信息
		TaskInfo task = mTaskInfoService.findRecordByIndex(ind);
		if(null == task) {
			model.addAttribute("msg", MessageFormat.format("未发现序列号为【{0}】的任务，无法编辑任务", ind));
			return M_PAGE_TASKS_EDIT;
		}
		// 将任务信息发到页面
		model.addAttribute("task", task);
		// 将国家信息传递到页面
		List<CountryInfo> countrires = mCountriesInfoService.getAll();
		model.addAttribute("countries", countrires);
		// 运营商
		List<OperatorInfo> operators = mOperatorsInfoService.getAll();
		Map<String, List<OperatorInfo>> opMap = getOperatorsMap(operators);
		model.addAttribute("operators", opMap);
		// 所有脚本
		List<ScriptInfo> scripts = mScriptsInfoService.getAllInfo();
		model.addAttribute("scripts", scripts);
		// 结算类型
		List<String> payoutTypes = BussinessCommonUtil.getInstance().getPayoutTypes();
		model.addAttribute("payoutTypes", payoutTypes);
		
		return M_PAGE_TASKS_EDIT;
	}
	
	private String procCommitTaskInfo(Model model, HttpServletRequest request) {
		// 国家存简称，脚本那边也是
		String taskId = request.getParameter("task_id");
		String taskDesc = request.getParameter("task_desc");
//		String chanId = request.getParameter("task_chan");
		String taskState = request.getParameter("task_state");
		String taskTotalCount = request.getParameter("task_total_count");
		String taskFinishedCount = request.getParameter("task_finished_count");
		String taskScriptId = request.getParameter("script_id");
		String isTest = request.getParameter("task_test");
		String payoutType= request.getParameter("pay_type");
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("收到脚本ID：{0}", taskScriptId));

		TaskInfo info = new TaskInfo();
		info.setTaskId(taskId);
		info.setTaskDesc(taskDesc);
//		info.setTaskChanNo(chanId);
		info.setTaskState(taskState);
		info.setTaskTotalCount(taskTotalCount);
		info.setTaskFinishedCount(taskFinishedCount);
		info.setScriptId(taskScriptId);
		info.setTaskForTest(isTest);
		info.setTaskType(payoutType);
		if(!info.isInfoValid()) {
			model.addAttribute("msg", "提交的信息有误，新建任务失败！");
			return M_PAGE_TASKS_EDIT;
		}
		
		Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("{0}", info));
		
		// 检查脚本信息是否正确
		List<ScriptInfo> existScripts = mScriptsInfoService.findScriptInfosByScriptId(info.getScriptId());
		if(null == existScripts || existScripts.isEmpty()) {
			model.addAttribute("msg", MessageFormat.format("脚本id为【{0}】的脚本不存在！", info.getScriptId()));
			return M_PAGE_TASKS_EDIT;
		}
		
		// 检查是新增还是更新
		boolean succeed = false;
		TaskInfo exists = mTaskInfoService.findRecordByTaskId(info.getTaskId());
		if(exists != null) {
			// 更新
			succeed = mTaskInfoService.updateRecordById(info.getTaskId(), info);
		} else {
			// 新增
			succeed = mTaskInfoService.addRecord(info);
		}
		
		model.addAttribute("msg", MessageFormat.format("新增/更新任务信息【{0}】", succeed ? "成功" : "失败"));
		
		return M_PAGE_TASKS_EDIT;
	}
	
	private Map<String, List<OperatorInfo>> getOperatorsMap(List<OperatorInfo> opRecrods) {
		Map<String, List<OperatorInfo>> res = new HashMap<String, List<OperatorInfo>>();
		
		if(null == opRecrods || opRecrods.isEmpty()) {
			Logger.getAnonymousLogger().log(Level.WARNING, "运营商列表为空，无法转换为map！");
			return res;
		}
		
		for(OperatorInfo record : opRecrods) {
			if(null == record) {
				continue;
			}
			
			List<OperatorInfo> ops = res.get(record.getCountryAbb());
			if(null == ops) {
				ops = new ArrayList<OperatorInfo>();
				res.put(record.getCountryAbb(), ops);
			}
			
			ops.add(record);
		}
		
		return res;
	}
	
}
