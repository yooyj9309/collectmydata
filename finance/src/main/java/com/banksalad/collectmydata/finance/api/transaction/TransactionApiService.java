package com.banksalad.collectmydata.finance.api.transaction;

import com.banksalad.collectmydata.common.collect.execution.Execution;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface TransactionApiService<Summary, TransactionRequest, Transaction> {

  List<Transaction> listTransactions(
      ExecutionContext executionContext,
      Execution execution,
      TransactionRequestHelper<Summary, TransactionRequest> requestHelper,
      TransactionResponseHelper<Summary, Transaction> responseHelper
  );
}
