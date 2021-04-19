package com.banksalad.collectmydata.finance.api.accountinfo;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

public interface AccountInfoService<Summary, AccountRequest, Account> {

  
  void listAccountInfos(
      ExecutionContext executionContext,
      Execution execution,
      AccountInfoRequestHelper<AccountRequest, Summary> requestHelper,
      AccountInfoResponseHelper<Summary, Account> responseHelper
  );

  void listAccountInfos(
      ExecutionContext executionContext,
      Execution execution,
      AccountInfoRequestHelper<AccountRequest, Summary> requestHelper,
      AccountInfoResponseHelper<Summary, Account> responseHelper,
      AccountInfoPublishmentHelper publishmentHelper
  );
}
