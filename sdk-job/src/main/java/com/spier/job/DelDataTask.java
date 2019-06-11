package com.spier.job;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.common.utils.DateTimeUtil;
import com.spier.entity.SpotInfo;
import com.spier.service.spot.ISpotInfoService;

/**
 * 定时删除数据，防止数据量太大，减少数据库的压力。
 */
@Component
public class DelDataTask {
    // 测试cron表达式
    //private static final String M_TRIGGER_PATTERN_TEST = "0 34 11 * * ?";
    private static final String M_TRIGGER_PATTERN = "0 0 0 */1 * ?";

    private static final Logger LOGGER = Logger.getAnonymousLogger();

    @Reference	
    private ISpotInfoService mSpotInfoService;

    // 删除埋点信息间隔时间，默认3天前的数据
    private static final int DEL_SPOT_INFO_INTERVAL_TIME = 3 * 24 * 60 * 60 * 1000;

    // 删除tomcat 日志间隔时间，默认1天前的日志
    private static final int DEL_TOMCAT_LOGS_INTERVAL_TIME = 24 * 60 * 60 * 1000;

    /**
     * 每天执行更新任务信息
     */
    @Scheduled(cron = M_TRIGGER_PATTERN)
    //@Scheduled(cron = M_TRIGGER_PATTERN_TEST)
    public void doTaskDelData() {
        Date date = new Date();
        LOGGER.log(Level.INFO, String.format("开始执行删除埋点数据任务，时间===%s",
                DateTimeUtil.formatDateToString(date)));
        Map<String, Object> args = new HashMap<>();
        args.put("endTime", DateTimeUtil.getDateBAInterval(date, DEL_SPOT_INFO_INTERVAL_TIME, true));
        List<SpotInfo> spotInfos = mSpotInfoService.getSpotsByCondition(args);

        if (!CollectionUtils.isEmpty(spotInfos)) {
            // 更新任务信息
            spotInfos.forEach(spotInfo -> {
                mSpotInfoService.deleteByInd(spotInfo.getIndex());
            });
            LOGGER.log(Level.INFO, String.format("删除埋点数据任务成功，时间===%s",
                    DateTimeUtil.formatDateToString(new Date())));
        }

        // 定期删除tomcat日志文件
        LOGGER.log(Level.INFO, String.format("开始执行删除tomcat日志任务，时间===%s",
                DateTimeUtil.formatDateToString(new Date())));
        String path = this.getClass().getClassLoader().getResource("").getPath();
        int index = path.indexOf("webapps");
        path = path.substring(0, index);
        File file = new File(path + "/logs/");
        File[] files = file.listFiles();
        if (files == null || files.length < 1) {
            LOGGER.log(Level.WARNING, String.format("暂无日志，时间===%s",
                    DateTimeUtil.formatDateToString(new Date())));
            return;
        }
        date = DateTimeUtil.getDateBAInterval(date, DEL_TOMCAT_LOGS_INTERVAL_TIME, true);
        String dateStr = DateTimeUtil.formatDateToString(date);
        index = dateStr.indexOf(" ");
        dateStr = dateStr.substring(0, index);
        for(File fileLog : files) {
            if (fileLog.getName().contains(dateStr)) {
                fileLog.delete();
            }
        }
        LOGGER.log(Level.INFO, String.format("删除tomcat日志任务成功，时间===%s",
                DateTimeUtil.formatDateToString(new Date())));
    }

}
