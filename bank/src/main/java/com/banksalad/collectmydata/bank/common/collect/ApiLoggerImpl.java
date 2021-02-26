package com.banksalad.collectmydata.bank.common.collect;

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
    // TODO jayden-lee ApiLogService에서 요청 API 로그 저장
  }

  @Override
  public void onResponse(ExecutionContext context, ApiLog apiLog) {
    // TODO jayden-lee ApiLogService에서 요청 API 로그 저장
  }
}
