package com.banksalad.collectmydata.finance.api.transaction;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionResponseHelper<Summary, Transaction> {

  List<Transaction> getTransactionsFromResponse(TransactionResponse transactionResponse);

  void saveTransactions(ExecutionContext executionContext, Summary summary, List<Transaction> transactions);

  void saveTransactionSyncedAt(ExecutionContext executionContext, Summary summary, LocalDateTime syncStartedAt);

  void saveResponseCode(ExecutionContext executionContext, Summary summary, String responseCode);
}
