package com.banksalad.collectmydata.bank.common.service;

import com.banksalad.collectmydata.bank.common.dto.ListAccountsResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

public interface ExternalApiService {

  ListAccountsResponse exchangeListAccounts(ExecutionContext executionContext, String orgCode, long searchTimeStamp);
}
