package com.banksalad.collectmydata.invest.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Executor;

import static com.banksalad.collectmydata.common.logging.LoggingMdcUtil.createTaskDecorator;

@Configuration
public class ThreadPoolConfig {

  @Bean
  public Executor threadPoolTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setThreadNamePrefix("Async-Thread-Collect-");
    executor.setTaskDecorator(createTaskDecorator());
    executor.setCorePoolSize(50);
    executor.setMaxPoolSize(100);
    executor.setQueueCapacity(1000);
    executor.setKeepAliveSeconds(20);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(20);
    executor.initialize();
    return executor;
  }
}
