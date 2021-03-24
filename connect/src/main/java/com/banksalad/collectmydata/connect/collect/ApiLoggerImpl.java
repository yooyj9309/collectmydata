package com.banksalad.collectmydata.connect.collect;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.apilog.ApiLogger;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.executor.ApiLog;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApiLoggerImpl implements ApiLogger {

  @Override
  public void onRequest(ExecutionContext context, ApiLog apiLog) {
    //TODO 여기서도 metric찍어야할지 확인
    log.info("onRequest: apiLog: {}", apiLog.getRequest().getBody());
  }

  @Override
  public void onResponse(ExecutionContext context, ApiLog apiLog) {
    log.info("onResponse: apiLog: {}", apiLog.getResponse().getBody());
  }
}

