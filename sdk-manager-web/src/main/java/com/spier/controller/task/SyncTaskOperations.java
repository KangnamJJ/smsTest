package com.spier.controller.task;

import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.common.bean.db.task.SyncTaskInfo;
import com.spier.common.config.GlobalConfig;
import com.spier.service.ISyncTaskInfoService;
import com.spier.service.task.ISyncTaskStatisticsService;

/**
 * 同步任务操作类
 * @author GHB
 * @version 1.0
 * @date 2019.2.19
 */
@Controller
public class SyncTaskOperations {

	@Reference
	private ISyncTaskStatisticsService mSyncTaskStatisticsService;
	
	@Reference
	private ISyncTaskInfoService mSyncTaskService;
	
	private static final String M_SYNC_TASK_HOME_JSP_NAME = "synctaskhome";
	
	@RequestMapping(value = "synctask", method = {RequestMethod.GET, RequestMethod.POST})
	public String onHomeLoaded(HttpServletRequest request, Model model) {
		String action = request.getParameter("action");
		if(StringUtils.equals(action, "switcher")) {
			// 切换任务状态
			changeTaskState(request.getParameter("offerid"));
			// 去掉参数，避免带着参数刷新时误入其他流程
			return "redirect:synctask";
		}
		
		// 加载
		List<SyncTaskInfo> allTasks = mSyncTaskService.getAll();
		
		model.addAttribute("tasks", allTasks);
		
		return M_SYNC_TASK_HOME_JSP_NAME;
	}
	
	private void changeTaskState(String offerId) {
		if(StringUtils.isEmpty(offerId)) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "offerId为空，无法切换任务状态！");
			return;
		}
		
		SyncTaskInfo task = mSyncTaskService.getTaskByOfferId(offerId);
		if(null == task) {
			Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
					"未发现id为【{0}】的任务，无法切换任务状态！", offerId));
			return;
		}
		
		int newState = GlobalConfig.M_TASK_STATE_PAUSE;
		switch(task.getSwitch()) {
			case GlobalConfig.M_TASK_STATE_PAUSE:
				newState = GlobalConfig.M_TASK_STATE_RUNNING;
			break;
			
			case GlobalConfig.M_TASK_STATE_RUNNING:
				newState = GlobalConfig.M_TASK_STATE_PAUSE;
			break;
			
			default:
				Logger.getAnonymousLogger().log(Level.SEVERE, MessageFormat.format(
						"任务【{0}】的状态【{1}】异常，设置为暂停！", offerId, task.getSwitch()));
			break;
		}
		
		task.setSwitch(newState);
		mSyncTaskService.updateOrInsert(task);
	}
}
