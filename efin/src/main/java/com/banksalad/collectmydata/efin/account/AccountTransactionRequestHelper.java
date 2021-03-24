package com.banksalad.collectmydata.efin.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.efin.account.dto.ListAccountTransactionsRequest;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import com.banksalad.collectmydata.finance.api.transaction.TransactionRequestHelper;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountTransactionRequestHelper implements
    TransactionRequestHelper<AccountSummary, ListAccountTransactionsRequest> {

  @Override
  public List<AccountSummary> listSummaries(ExecutionContext executionContext) {
    //TODO 구현 필요
    return null;
  }

  @Override
  public LocalDateTime getTransactionSyncedAt(ExecutionContext executionContext, AccountSummary accountSummary) {
    //TODO 구현 필요
    return null;
  }

  @Override
  public ListAccountTransactionsRequest make(ExecutionContext executionContext, AccountSummary accountSummary,
      LocalDate fromDate, LocalDate toDate, String nextPage) {
    //TODO 구현 필요
    return null;
  }
}
