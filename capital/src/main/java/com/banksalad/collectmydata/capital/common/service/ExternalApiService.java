package com.banksalad.collectmydata.capital.common.service;

import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.common.dto.AccountSummaryResponse;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountBasicResponse;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountDetailResponse;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountTransactionResponse;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseBasicResponse;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransactionResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

public interface ExternalApiService {

  // 6.7.1
  AccountSummaryResponse getAccounts(ExecutionContext executionContext, String orgCode, long searchTimeStamp);

  // 6.7.2
  LoanAccountBasicResponse getAccountBasic(ExecutionContext executionContext, Organization organization,
      AccountSummary accountSummary);

  // 6.7.3
  LoanAccountDetailResponse getAccountDetail(ExecutionContext executionContext, Organization organization,
      AccountSummary accountSummary);

  // 6.7.4
  LoanAccountTransactionResponse getAccountTransactions(ExecutionContext executionContext, Organization organization,
      AccountSummary accountSummary);

  // 6.7.5
  OperatingLeaseBasicResponse getOperatingLeaseBasic(ExecutionContext executionContext, Organization organization,
      AccountSummary accountSummary);

  // 6.7.6
  OperatingLeaseTransactionResponse getOperatingLeaseTransactions(ExecutionContext executionContext,
      Organization organization, AccountSummary accountSummary);
}
