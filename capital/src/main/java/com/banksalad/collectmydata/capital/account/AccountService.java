package com.banksalad.collectmydata.capital.account;

import com.banksalad.collectmydata.capital.account.dto.Account;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

public interface AccountService {

  public void syncAllAccounts(ExecutionContext executionContext, Organization organization);

  public void updateSearchTimestampForAccount(long banksaladUserId, String organizationId, Account account);
}
