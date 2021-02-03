package com.banksalad.collectmydata.connect.common.service;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.collect.execution.ExecutionRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionResponse;

public interface ExecutionService{
    public <T, R> R execute(ExecutionContext executionContext, Execution execution, ExecutionRequest<T> executionRequest);
}
