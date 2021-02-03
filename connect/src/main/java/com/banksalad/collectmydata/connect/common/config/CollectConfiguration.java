package com.banksalad.collectmydata.connect.common.config;

import com.banksalad.collectmydata.common.collect.apilog.ApiLogger;
import com.banksalad.collectmydata.common.collect.apilog.IdGenerator;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutorImpl;
import com.banksalad.collectmydata.common.collect.executor.TransferClient;
import com.banksalad.collectmydata.common.collect.executor.TransferClientImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
public class CollectConfiguration {
  private final TransferClient transferClient;
  private final IdGenerator idGenerator;
  private final ApiLogger apiLogger;

  public CollectConfiguration(IdGenerator idGenerator, ApiLogger apiLogger) {
    this.transferClient = new TransferClientImpl();
    this.idGenerator = idGenerator;
    this.apiLogger = apiLogger;
  }

  @Bean
  public CollectExecutor collectExecutor() {
    return new CollectExecutorImpl(transferClient, idGenerator, apiLogger);
  }
}
