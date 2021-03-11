package com.banksalad.collectmydata.invest.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

public interface ExecutionResponseValidateService {

  Boolean isAllResponseResultSuccess(ExecutionContext executionContext, Boolean isExceptionOccurred);
}
