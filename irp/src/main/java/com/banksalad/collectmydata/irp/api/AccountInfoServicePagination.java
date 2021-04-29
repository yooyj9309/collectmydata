package com.banksalad.collectmydata.irp.api;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoPublishmentHelper;

public interface AccountInfoServicePagination<Summary, AccountRequest, Account> {

  void listAccountInfos(
      ExecutionContext executionContext,
      Execution execution,
      AccountInfoRequestPaginationHelper<AccountRequest, Summary> requestHelper,
      AccountInfoResponsePaginationHelper<AccountRequest, Summary, Account> responseHelper
  );

  void listAccountInfos(
      ExecutionContext executionContext,
      Execution execution,
      AccountInfoRequestPaginationHelper<AccountRequest, Summary> irpAccountDetailInfoRequestHelper,
      AccountInfoResponsePaginationHelper<AccountRequest, Summary, Account> irpAccountDetailInfoResponseHelper,
      AccountInfoPublishmentHelper publishmentHelper);
}
