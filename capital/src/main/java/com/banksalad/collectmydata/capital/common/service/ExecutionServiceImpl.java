package com.banksalad.collectmydata.capital.common.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;
import com.banksalad.collectmydata.common.collect.executor.CollectExecutor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExecutionServiceImpl implements ExecutionService {

  private final CollectExecutor collectExecutor;

  @Override
  public <T, R> R execute(ExecutionContext executionContext, Execution execution,
      ExecutionRequest<T> executionRequest) {

    ExecutionResponse<R> executionResponse = collectExecutor.execute(executionContext, execution, executionRequest);

    if (executionResponse.getHttpStatusCode() != HttpStatus.OK.value()) {
      throw new RuntimeException("execution Statue is not OK");
      //TODO Throw 추후 적용
      // logging
      // execution monitoring
      // throw
    }

    if (executionResponse.getResponse() == null) {
      throw new RuntimeException("execution Statue is not OK");
    }
    return executionResponse.getResponse();
  }
}
