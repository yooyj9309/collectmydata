package com.banksalad.collectmydata.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.banksalad.collectmydata.common.collect.apilog.ApiLogger;
import com.banksalad.collectmydata.common.collect.apilog.IdGenerator;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutorImpl;
import com.banksalad.collectmydata.common.collect.executor.TransferClient;
import com.banksalad.collectmydata.common.collect.executor.TransferClientImpl;

@Configuration
public class CollectConfig {

  private final TransferClient transferClient;
  private final IdGenerator idGenerator;
  private final ApiLogger apiLogger;

  public CollectConfig(IdGenerator idGenerator, ApiLogger apiLogger) {
    this.transferClient = new TransferClientImpl();
    this.idGenerator = idGenerator;
    this.apiLogger = apiLogger;
  }

  @Bean
  public CollectExecutor collectExecutor() {
    return new CollectExecutorImpl(transferClient, idGenerator, apiLogger);
  }
}
