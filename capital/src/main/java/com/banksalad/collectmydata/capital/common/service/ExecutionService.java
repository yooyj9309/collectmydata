package com.banksalad.collectmydata.capital.common.service;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;

public interface ExecutionService {

  public <T, R> R execute(ExecutionContext executionContext, Execution execution, ExecutionRequest<T> executionRequest);
}
