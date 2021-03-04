package com.banksalad.collectmydata.bank.common.service;

import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface AccountSummaryService {

  List<AccountSummary> listAccountSummaries(ExecutionContext executionContext);

  void updateBasicTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      long basicSearchTimestamp);
}
