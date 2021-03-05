package com.banksalad.collectmydata.capital.oplease;

import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLease;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransaction;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface OperatingLeaseService {

  List<OperatingLease> listOperatingLeases(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries);

  List<OperatingLeaseTransaction> listOperatingLeaseTransactions(ExecutionContext executionContext,
      Organization organization, List<AccountSummary> accountSummaries);
}
