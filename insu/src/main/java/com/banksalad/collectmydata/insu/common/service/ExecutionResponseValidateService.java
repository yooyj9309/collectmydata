package com.banksalad.collectmydata.insu.common.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

public interface ExecutionResponseValidateService {

  Boolean isAllResponseResultSuccess(ExecutionContext executionContex, Boolean isExceptionOccurred);
}
