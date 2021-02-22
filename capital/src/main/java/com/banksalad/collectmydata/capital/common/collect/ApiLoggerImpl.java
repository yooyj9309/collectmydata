package com.banksalad.collectmydata.capital.common.collect;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.capital.common.service.ApiLogService;
import com.banksalad.collectmydata.common.collect.apilog.ApiLogger;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.executor.ApiLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiLoggerImpl implements ApiLogger {

  private final ApiLogService apiLogService;

  @Override
  public void onRequest(ExecutionContext context, ApiLog apiLog) {
    log.debug("onRequest: apiLog: {}", apiLog.getRequest().getBody());

    apiLogService.logRequest(context.getSyncRequestId(), context.getExecutionRequestId(),
        context.getBanksaladUserId(), context.getOrganizationId(), apiLog);
  }

  @Override
  public void onResponse(ExecutionContext context, ApiLog apiLog) {
    log.debug("onResponse: apiLog: {}", apiLog.getResponse().getBody());

    apiLogService.logResponse(context.getSyncRequestId(), context.getExecutionRequestId(),
        context.getBanksaladUserId(), context.getOrganizationId(), apiLog);
  }
}

