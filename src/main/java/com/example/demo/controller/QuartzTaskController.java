package com.example.demo.controller;

import com.example.demo.model.QuartzTask;
import com.example.demo.model.Result;
import com.example.demo.service.QuartzTaskService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/task")
public class QuartzTaskController {

    @Autowired
    @Qualifier("Scheduler")
    private Scheduler scheduler;

    @Autowired
    private QuartzTaskService jobService;

    //添加task
    @RequestMapping(value = "/add/{jobName}")
    public Result addTask(@PathVariable(name = "jobName") String jobName) throws Exception {
        QuartzTask task = new QuartzTask(
                jobName,
                jobName + "_group",
                "",
                "com.example.demo.jobs." + jobName,
                "0/10 * * * * ?",
                jobName + "_trigger");
        //获取.class
        Class jobClass = Class.forName(task.getJobClassName());
        jobClass.newInstance();
        //创建jobdetail
        JobDetail job = JobBuilder.newJob(jobClass).withIdentity(task.getJobName(),
                task.getJobGroup())
                .withDescription(task.getDescription())
                .build();
        // 使用cron表达式
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(task.getCronExpression());
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(task.getTriggerName(), task.getJobGroup())
                .startNow()
                .withSchedule(cronScheduleBuilder)
                .build();
        //交由Scheduler安排触发
        scheduler.scheduleJob(job, trigger);
        return new Result("200", "", null);
    }

}