package com.banksalad.collectmydata.finance.api.transaction;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

public interface TransactionApiService<Summary, TransactionRequest, Transaction> {

  void listTransactions(
      ExecutionContext executionContext,
      Execution execution,
      TransactionRequestHelper<Summary, TransactionRequest> requestHelper,
      TransactionResponseHelper<Summary, Transaction> responseHelper
  );

  void listTransactions(
      ExecutionContext executionContext,
      Execution execution,
      TransactionRequestHelper<Summary, TransactionRequest> requestHelper,
      TransactionResponseHelper<Summary, Transaction> responseHelper,
      TransactionPublishmentHelper<Summary> publishmentHelper
  );
}
