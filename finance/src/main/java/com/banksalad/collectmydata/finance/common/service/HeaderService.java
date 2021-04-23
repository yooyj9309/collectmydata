package com.banksalad.collectmydata.finance.common.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.Map;

public interface HeaderService {

  Map<String, String> makeHeader(ExecutionContext executionContext);
}
