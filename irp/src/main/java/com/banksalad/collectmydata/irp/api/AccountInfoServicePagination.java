package com.banksalad.collectmydata.irp.api;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface AccountInfoServicePagination<Summary, AccountRequest, Account> {

  List<Account> listAccountInfos(
      ExecutionContext executionContext,
      Execution execution,
      AccountInfoRequestPaginationHelper<AccountRequest, Summary> requestHelper,
      AccountInfoResponsePaginationHelper<AccountRequest, Summary, Account> responseHelper
  );
}
