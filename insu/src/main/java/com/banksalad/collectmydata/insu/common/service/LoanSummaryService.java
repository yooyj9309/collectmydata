package com.banksalad.collectmydata.insu.common.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.common.dto.LoanSummary;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanSummaryService {

  List<LoanSummary> listLoanSummaries(ExecutionContext executionContext, String organizationCode);

  void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, String accountNum, long searchTimestamp);

  void updateDetailSearchTimestamp(long banksaladUserId, String organizationId, String accountNum,
      long searchTimestamp);

  void updateTransactionSyncedAt(long banksaladUserId, String organizationId, String accountNum,
      LocalDateTime transactionSyncedAt);

}
