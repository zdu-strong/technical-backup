package com.john.project.common.config;

import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.john.project.properties.SchedulingPoolSizeProperties;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {

    @Autowired
    private SchedulingPoolSizeProperties schedulingPoolSizeProperties;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(new ConcurrentTaskScheduler(Executors.newScheduledThreadPool(
                schedulingPoolSizeProperties.getSchedulingPoolSize().intValue(),
                Thread.ofVirtual().factory())));
    }

}
