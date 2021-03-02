package com.banksalad.collectmydata.insu.common.service;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;

public interface ExecutionService {

  <T, R> R execute(ExecutionContext executionContext, Execution execution, ExecutionRequest<T> executionRequest);
}
