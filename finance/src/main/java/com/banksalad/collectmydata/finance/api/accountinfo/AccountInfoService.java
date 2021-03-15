package com.banksalad.collectmydata.finance.api.accountinfo;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface AccountInfoService<Summary, AccountRequest, Account> {

  List<Account> listAccountInfos(
      ExecutionContext executionContext,
      Execution execution,
      AccountInfoRequestHelper<AccountRequest, Summary> requestHelper,
      AccountInfoResponseHelper<Summary, Account> responseHelper
  );
}
