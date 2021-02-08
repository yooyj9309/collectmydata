package com.banksalad.collectmydata.capital.common.service;

import com.banksalad.collectmydata.capital.account.dto.Account;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface ExternalApiService {

  // 6.7.1
  public List<Account> getAccounts(ExecutionContext executionContext, Organization organization);

  // 6.7.2

  // 6.7.3

  // 6.7.4

  // 6.7.5

  // 6.7.6
}
