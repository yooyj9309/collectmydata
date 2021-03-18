package com.banksalad.collectmydata.capital.oplease;

import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransaction;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

@Deprecated
public interface OperatingLeaseService {

  List<OperatingLeaseTransaction> listOperatingLeaseTransactions(ExecutionContext executionContext,
      Organization organization, List<AccountSummary> accountSummaries);
}
