package com.banksalad.collectmydata.common.collect.executor;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;

import reactor.core.publisher.Flux;

public interface CollectReactiveExecutor {

  <T, R> Flux<ExecutionResponse<R>> executeWithPagination(ExecutionContext context, Execution execution,
      ExecutionRequest<T> executionRequest);

}
