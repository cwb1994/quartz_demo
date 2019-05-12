package com.example.demo.service;

import com.example.demo.mapper.QuartzTaskMapper;
import com.example.demo.model.QuartzTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuartzTaskService {

    @Autowired
    private QuartzTaskMapper quartzTaskMapper;

    public List<QuartzTask> getTaskList() {
        return quartzTaskMapper.getTaskList();
    }

    public QuartzTask getTask(String jobName) {
        return quartzTaskMapper.getTask(jobName);
    }

}