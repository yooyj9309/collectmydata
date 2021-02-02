package com.banksalad.collectmydata.common.collect.apilog;


import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.executor.ApiLog;

public interface ApiLogger {

  void onRequest(ExecutionContext context, ApiLog apiLog);

  void onResponse(ExecutionContext context, ApiLog apiLog);
}
