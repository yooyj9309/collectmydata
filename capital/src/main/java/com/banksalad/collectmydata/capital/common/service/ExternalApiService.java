package com.banksalad.collectmydata.capital.common.service;

import com.banksalad.collectmydata.capital.account.dto.AccountBasicResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountDetailResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountTransactionResponse;
import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.common.dto.AccountSummaryResponse;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseBasicResponse;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransactionResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.time.LocalDate;

public interface ExternalApiService {

  // 6.7.1
  AccountSummaryResponse getAccounts(ExecutionContext executionContext, String orgCode, long searchTimeStamp);

  // 6.7.2
  AccountBasicResponse getAccountBasic(ExecutionContext executionContext, Organization organization,
      AccountSummary accountSummary);

  // 6.7.3
  AccountDetailResponse getAccountDetail(ExecutionContext executionContext, Organization organization,
      AccountSummary accountSummary);

  // 6.7.4
  AccountTransactionResponse getAccountTransactions(ExecutionContext executionContext,
      String orgCode, String accountNum, String seqno, String fromDate, String toDate);

  // 6.7.5
  OperatingLeaseBasicResponse getOperatingLeaseBasic(ExecutionContext executionContext, Organization organization,
      AccountSummary accountSummary);

  // 6.7.6
  OperatingLeaseTransactionResponse listOperatingLeaseTransactions(ExecutionContext executionContext,
      Organization organization, AccountSummary accountSummary, LocalDate fromDate, LocalDate toDate);
}
