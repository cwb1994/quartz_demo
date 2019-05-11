package com.example.demo.mapper;

import com.example.demo.model.QuartzTask;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuartzTaskMapper {
   public List<QuartzTask> getTaskList();
}