package com.banksalad.collectmydata.insu.common.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.common.dto.LoanSummary;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanSummaryService {

  List<LoanSummary> listLoanSummaries(ExecutionContext executionContext, String organizationCode);

  void updateBasicSearchTimestampAndResponseCode(long banksaladUserId, String organizationId, String accountNum,
      long searchTimestamp, String rspCode);

  void updateDetailSearchTimestampAndResponseCode(long banksaladUserId, String organizationId, String accountNum,
      long searchTimestamp, String rspCode);

  void updateTransactionSyncedAt(long banksaladUserId, String organizationId, String accountNum,
      LocalDateTime transactionSyncedAt);

}
