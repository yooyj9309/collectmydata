package com.banksalad.collectmydata.capital.common.service;

import com.banksalad.collectmydata.capital.account.dto.AccountDetailResponse;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransactionResponse;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.time.LocalDate;

@Deprecated
public interface ExternalApiService {
  // 6.7.3
  AccountDetailResponse getAccountDetail(ExecutionContext executionContext, Organization organization,
      AccountSummary accountSummary);

  // 6.7.6
  OperatingLeaseTransactionResponse listOperatingLeaseTransactions(ExecutionContext executionContext,
      Organization organization, AccountSummary accountSummary, LocalDate fromDate, LocalDate toDate);
}
