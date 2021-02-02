package com.banksalad.collectmydata.common.collect.executor;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;

public interface CollectExecutor {

  <T, R> ExecutionResponse<R> execute(ExecutionContext context, Execution execution,
      ExecutionRequest<T> executionRequest);

}
