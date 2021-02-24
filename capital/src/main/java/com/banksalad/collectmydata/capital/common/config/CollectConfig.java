package com.banksalad.collectmydata.capital.common.config;

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
    //TODO 명시적으로 ThreadPool 적용해야하는지 확인 후 로직 추가.
    this.transferClient = new TransferClientImpl();
    this.idGenerator = idGenerator;
    this.apiLogger = apiLogger;
  }

  @Bean
  public CollectExecutor collectExecutor() {
    return new CollectExecutorImpl(transferClient, idGenerator, apiLogger);
  }
}
