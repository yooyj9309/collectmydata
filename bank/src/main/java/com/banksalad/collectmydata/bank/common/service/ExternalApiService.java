package com.banksalad.collectmydata.bank.common.service;

import com.banksalad.collectmydata.bank.common.dto.ListAccountSummariesResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

public interface ExternalApiService {

  ListAccountSummariesResponse exchangeListAccountSummaries(ExecutionContext executionContext, String orgCode,
      long searchTimeStamp);
}
