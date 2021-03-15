package com.banksalad.collectmydata.telecom.collect;

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
    log.info("onRequest: organizationId: {}, userId: {}", context.getOrganizationId(), context.getBanksaladUserId());
    log.info("onRequest: apiLog: {}", apiLog.getRequest().getBody());
  }

  @Override
  public void onResponse(ExecutionContext context, ApiLog apiLog) {
    log.info("onResponse: organizationId: {}, userId: {}", context.getOrganizationId(), context.getBanksaladUserId());
    log.info("onResponse: apiLog: {}", apiLog.getResponse().getBody());
  }
}
