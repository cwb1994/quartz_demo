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

import java.util.List;

@RestController
@RequestMapping("/task")
public class QuartzTaskController {

    @Autowired
    @Qualifier("Scheduler")
    private Scheduler scheduler;

    @Autowired
    private QuartzTaskService taskService;

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

    //立即运行task
    @RequestMapping(value = "/run/{jobName}")
    public Result run(@PathVariable(name = "jobName") String jobName) throws Exception {
        QuartzTask task = taskService.getTask(jobName);
        if (task == null) {
            return new Result("401", jobName + "不存在", null);
        }
        JobKey jobKey = new JobKey(task.getJobName(), task.getJobGroup());
        scheduler.triggerJob(jobKey);
        return new Result("200", "", null);
    }

    //停止task
    @RequestMapping(value = "/pause/{jobName}")
    public Result pause(@PathVariable(name = "jobName") String jobName) throws Exception {
        QuartzTask task = taskService.getTask(jobName);
        if (task == null) {
            return new Result("401", jobName + "不存在", null);
        }
        JobKey jobKey = new JobKey(task.getJobName(), task.getJobGroup());
        scheduler.pauseJob(jobKey);
        return new Result("200", "", null);
    }

    //恢复task
    @RequestMapping(value = "/resume/{jobName}")
    public Result resume(@PathVariable(name = "jobName") String jobName) throws Exception{
        QuartzTask task = taskService.getTask(jobName);
        if (task == null) {
            return new Result("401", jobName + "不存在", null);
        }
        JobKey jobKey = new JobKey(task.getJobName(), task.getJobGroup());
        scheduler.resumeJob(jobKey);
        return new Result("200", "", null);
    }

    //删除task
    @RequestMapping(value = "/delete/{jobName}")
    public Result delete(@PathVariable(name = "jobName") String jobName) throws Exception{
        QuartzTask task = taskService.getTask(jobName);
        if (task == null) {
            return new Result("401", jobName + "不存在", null);
        }
        TriggerKey triggerKey = TriggerKey.triggerKey(task.getJobName(), task.getJobGroup());
        // 停止触发器
        scheduler.pauseTrigger(triggerKey);
        // 移除触发器
        scheduler.unscheduleJob(triggerKey);
        // 删除task
        scheduler.deleteJob(JobKey.jobKey(task.getJobName(), task.getJobGroup()));
        return new Result("200", "", null);
    }

    //获取task列表
    @RequestMapping(value = "/getlist")
    public Result getList() throws Exception {
        List<QuartzTask> taskList=taskService.getTaskList();
        return new Result("200","",taskList);
    }
}