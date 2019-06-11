package com.spier.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.common.bean.db.ScriptInfo;
import com.spier.common.bean.db.task.TaskInfo;
import com.spier.common.config.GlobalConfig;
import com.spier.common.utils.BussinessCommonUtil;
import com.spier.service.BussinessCommonService;
import com.spier.service.task.ITaskInfoService;

@Controller
public class HomePage {

	private static final String M_PAGE_HOME = "home";
	
	@Reference	
	private ITaskInfoService mTaskInfoService;
	
	@Reference	
	private BussinessCommonService bussinessCommonService;
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String hello(Model model) {
		List<TaskInfo> tasks = mTaskInfoService.getAllTasks();
		model.addAttribute("tasks", tasks);
		
		Map<Integer, String> taskStates = new HashMap<Integer, String>();
		taskStates.put(GlobalConfig.M_TASK_STATE_PAUSE, "暂停");
		taskStates.put(GlobalConfig.M_TASK_STATE_RUNNING, "运行");
		model.addAttribute("statesValue", taskStates);
		
		Map<Integer, String> netEnvValues = new HashMap<Integer, String>();
		netEnvValues.put(GlobalConfig.M_NET_ENV_ALL, "全部");
		netEnvValues.put(GlobalConfig.M_NET_ENV_MOBILE, "手机流量");
		netEnvValues.put(GlobalConfig.M_NET_ENV_WIFI, "WiFi流量");
		model.addAttribute("netEnvValue", netEnvValues);
		
		Map<String, ScriptInfo> scripts = bussinessCommonService.getScriptsByTasks(tasks);
		model.addAttribute("scripts", scripts);
		model.addAttribute("testtask", BussinessCommonUtil.getInstance().getTaskTestSelections());
		model.addAttribute("payouttypes", BussinessCommonUtil.getInstance().getPayoutTypes());
		
		return M_PAGE_HOME;
	}
	
	
}
