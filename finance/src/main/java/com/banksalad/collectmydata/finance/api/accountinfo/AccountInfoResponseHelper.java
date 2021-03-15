package com.banksalad.collectmydata.finance.api.accountinfo;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;

public interface AccountInfoResponseHelper<Summary, Account> {

  Account getAccountFromResponse(AccountResponse accountResponse);

  void saveAccountAndHistory(ExecutionContext executionContext, Summary summaryDto, Account account);

  void saveSearchTimestamp(ExecutionContext executionContext, Summary summaryDto, long searchTimestamp);

  void saveResponseCode(ExecutionContext executionContext, Summary summaryDto, String responseCode);
  
}
