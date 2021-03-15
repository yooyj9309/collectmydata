package com.banksalad.collectmydata.finance.common.service;

import com.banksalad.collectmydata.common.collect.executor.ApiLog;

public interface ApiLogService {

  void logRequest(String syncRequestId, String executionRequestId, long banksaladUserId, String organizationId,
      ApiLog apiLog);

  void logResponse(String syncRequestId, String executionRequestId, long banksaladUserId, String organizationId,
      ApiLog apiLog);
}
