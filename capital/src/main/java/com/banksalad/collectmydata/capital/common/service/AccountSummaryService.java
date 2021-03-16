package com.banksalad.collectmydata.capital.common.service;

import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

public interface AccountSummaryService {
  
  void updateSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary);

  void updateTransactionSyncedAt(ExecutionContext executionContext, AccountSummary accountSummary);

  void updateOperatingLeaseTransactionSyncedAt(ExecutionContext executionContext, AccountSummary accountSummary);

}
