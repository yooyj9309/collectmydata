package com.banksalad.collectmydata.finance.api.bill;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface BillRequestHelper<BillRequest> {

  LocalDateTime getTransactionSyncedAt(ExecutionContext executionContext);

  BillRequest make(ExecutionContext executionContext, LocalDate fromDate, LocalDate toDate, String nextPage);
}
