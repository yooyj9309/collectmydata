package com.banksalad.collectmydata.finance.api.transaction;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRequestHelper<Summary, TransactionRequest> {

  List<Summary> listSummaries(ExecutionContext executionContext);

  LocalDateTime getTransactionSyncedAt(ExecutionContext executionContext, Summary summary);

  TransactionRequest make(ExecutionContext executionContext, Summary summary, LocalDate fromDate, LocalDate toDate,
      String nextPage);
}
