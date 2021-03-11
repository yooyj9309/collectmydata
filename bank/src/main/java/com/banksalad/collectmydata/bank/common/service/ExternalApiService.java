package com.banksalad.collectmydata.bank.common.service;

import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import com.banksalad.collectmydata.bank.common.dto.ListAccountSummariesResponse;
import com.banksalad.collectmydata.bank.deposit.dto.ListDepositAccountTransactionsResponse;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountBasicResponse;
import com.banksalad.collectmydata.bank.deposit.dto.GetDepositAccountDetailResponse;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountBasicResponse;
import com.banksalad.collectmydata.bank.invest.dto.GetInvestAccountDetailResponse;
import com.banksalad.collectmydata.bank.invest.dto.ListInvestAccountTransactionsResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.common.util.DateRange;

import java.time.LocalDate;

public interface ExternalApiService {

  ListAccountSummariesResponse listAccountSummaries(ExecutionContext executionContext, String orgCode,
      long searchTimestamp);

  GetDepositAccountBasicResponse getDepositAccountBasic(ExecutionContext executionContext, String orgCode,
      String accountNum, String seqno, long searchTimestamp);

  GetDepositAccountDetailResponse getDepositAccountDetail(ExecutionContext executionContext, String orgCode,
      String accountNum, String seqno, long searchTimestamp);

  ListDepositAccountTransactionsResponse listDepositAccountTransactions(ExecutionContext executionContext,
      String orgCode, String accountNum, String seqno, LocalDate fromDate, LocalDate toDate);

  GetInvestAccountBasicResponse getInvestAccountBasic(ExecutionContext executionContext,
      AccountSummary accountSummary, Organization organization, long searchTimestamp);

  GetInvestAccountDetailResponse getInvestAccountDetail(ExecutionContext executionContext,
      AccountSummary accountSummary, Organization organization, long searchTimestamp);

  ListInvestAccountTransactionsResponse listInvestAccountTransactions(ExecutionContext executionContext,
      AccountSummary accountSummary, Organization organization, DateRange dateRange);
}
