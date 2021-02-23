package com.banksalad.collectmydata.referencebank.common.config;

import com.banksalad.collectmydata.common.logging.LoggingMdcUtil;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

public class ThreadPoolConfig {

  @Bean
  public Executor threadPoolTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setThreadNamePrefix("Async-Thread-Collect-");
    executor.setTaskDecorator(LoggingMdcUtil.createTaskDecorator());
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
