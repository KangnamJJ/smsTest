package com.spier.job;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTaskTest  {
    private  static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    @Scheduled(fixedRate = 3000)
    public void reportCurrentTime(){
        System.out.println("当前时间：" + dateFormat.format(new Date()));
    }
}
