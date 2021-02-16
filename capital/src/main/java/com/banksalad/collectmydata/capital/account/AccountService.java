package com.banksalad.collectmydata.capital.account;

import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

public interface AccountService {

  public void syncAllAccounts(ExecutionContext executionContext, Organization organization);
}
