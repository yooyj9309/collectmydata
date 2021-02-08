package com.banksalad.collectmydata.capital.account;

import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

public interface AccountService {

  public void syncAccounts(ExecutionContext executionContext, Organization organization);
}
