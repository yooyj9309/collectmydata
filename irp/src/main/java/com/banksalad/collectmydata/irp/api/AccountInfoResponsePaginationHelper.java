package com.banksalad.collectmydata.irp.api;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;

public interface AccountInfoResponsePaginationHelper<AccountRequest, Summary, Account> {

  Account getAccountFromResponse(AccountResponse accountResponse);

  void saveAccountAndHistory(ExecutionContext executionContext, Summary summary, Account account,
      AccountRequest accountRequest);

  long getSearchTimestamp(AccountRequest accountRequest);

  void saveSearchTimestamp(ExecutionContext executionContext, Summary summary, long searchTimestamp);

  void saveResponseCode(ExecutionContext executionContext, Summary summary, String responseCode);
}
