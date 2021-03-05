package com.banksalad.collectmydata.bank.common.service;

import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface AccountSummaryService {

  List<AccountSummary> listAccountSummaries(ExecutionContext executionContext);

  void updateBasicSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      long basicSearchTimestamp);

  void updateDetailSearchTimestamp(long banksaladUserId, String organizationId, AccountSummary accountSummary,
      long detailSearchTimestamp);
}
