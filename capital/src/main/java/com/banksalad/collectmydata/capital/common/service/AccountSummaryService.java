package com.banksalad.collectmydata.capital.common.service;

import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface AccountSummaryService {

  List<AccountSummary> listAccountSummaries(ExecutionContext executionContext, Organization organization);

  void updateSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary);

  void updateTransactionSyncedAt(ExecutionContext executionContext, AccountSummary accountSummary);

  void updateOperatingLeaseTransactionSyncedAt(ExecutionContext executionContext, AccountSummary accountSummary);

}
