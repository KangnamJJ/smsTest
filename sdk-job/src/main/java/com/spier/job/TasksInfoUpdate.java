package com.spier.job;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.common.bean.db.task.TaskInfo;
import com.spier.common.bean.db.task.TasksHistoryInfo;
import com.spier.common.utils.DateTimeUtil;
import com.spier.service.job.ITasksHistoryInfoService;
import com.spier.service.task.ITaskInfoService;

/**
 * 任务信息更新，每天晚点12点重置任务信息，并记录历史任务信息
 */
@Component
public class TasksInfoUpdate {
    // 测试cron表达式
    //private static final String M_TRIGGER_PATTERN_TEST = "0 0 14 1/1 * ?";
    private static final String M_TRIGGER_PATTERN = "0 0 0 1/1 * ?";

    private static final Logger LOGGER = Logger.getAnonymousLogger();

    @Reference
    private ITasksHistoryInfoService iTasksHistoryInfoService;

    @Reference
    private ITaskInfoService iTaskInfoService;

    /**
     * 每天执行更新任务信息
     */
    @Scheduled(cron = M_TRIGGER_PATTERN)
    public void onTasksInfoUpdate() {
        LOGGER.log(Level.INFO, String.format("开始执行更新任务信息，时间===%s",
                DateTimeUtil.formatDateToString(new Date())));
        List<TaskInfo> taskInfoList = iTaskInfoService.getAllTasks();

        if (CollectionUtils.isEmpty(taskInfoList)) {
            LOGGER.log(Level.WARNING, String.format("暂无数据操作，时间===%s",
                    DateTimeUtil.formatDateToString(new Date())));
            return;
        }

        // 更新任务信息
        taskInfoList.forEach(taskInfo -> {
            TasksHistoryInfo tasksHistoryInfo = getTaskHistoryObj();
            tasksHistoryInfo.setTaskId(taskInfo.getTaskId());
            tasksHistoryInfo.setTaskFinishedCount(taskInfo.getTaskFinishedCount());
            tasksHistoryInfo.setTaskTotalCount(taskInfo.getTaskTotalCount());
            if (doUpdateTaskInfo(tasksHistoryInfo, taskInfo)) {
                LOGGER.log(Level.INFO, String.format("更新任务失败，重试一次，时间===%s，任务ID=%s",
                        DateTimeUtil.formatDateToString(new Date()), taskInfo.getTaskId()));

                // 重试一次
                if (doUpdateTaskInfo(tasksHistoryInfo, taskInfo)) {
                    LOGGER.log(Level.INFO, String.format("更新任务失败，重试时间===%s，任务ID=%s",
                            DateTimeUtil.formatDateToString(new Date()), taskInfo.getTaskId()));
                }
            }
        });

        LOGGER.log(Level.INFO, String.format("更新任务完成，时间===%s",
                DateTimeUtil.formatDateToString(new Date())));
    }

    private boolean doUpdateTaskInfo(TasksHistoryInfo tasksHistoryInfo, TaskInfo taskInfo) {
        return iTasksHistoryInfoService.insertTasksHistoryInfo(tasksHistoryInfo, taskInfo) < 1 ? true : false;
    }

    private TasksHistoryInfo getTaskHistoryObj() {
        TasksHistoryInfo tasksHistoryInfo = new TasksHistoryInfo();
        tasksHistoryInfo.setCreateTime(new Date());
        return tasksHistoryInfo;
    }
}
