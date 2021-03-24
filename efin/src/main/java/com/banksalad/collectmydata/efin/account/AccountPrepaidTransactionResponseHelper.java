package com.banksalad.collectmydata.efin.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.efin.account.dto.AccountPrepaidTransaction;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import com.banksalad.collectmydata.finance.api.transaction.TransactionResponseHelper;
import com.banksalad.collectmydata.finance.api.transaction.dto.TransactionResponse;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountPrepaidTransactionResponseHelper implements
    TransactionResponseHelper<AccountSummary, AccountPrepaidTransaction> {

  @Override
  public List<AccountPrepaidTransaction> getTransactionsFromResponse(TransactionResponse transactionResponse) {
    //TODO 구현 필요
    return null;
  }

  @Override
  public void saveTransactions(ExecutionContext executionContext, AccountSummary accountSummary,
      List<AccountPrepaidTransaction> accountPrepaidTransactions) {
    //TODO 구현 필요

  }

  @Override
  public void saveTransactionSyncedAt(ExecutionContext executionContext, AccountSummary accountSummary,
      LocalDateTime syncStartedAt) {
    //TODO 구현 필요

  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, AccountSummary accountSummary, String responseCode) {
    //TODO 구현 필요
  }
}
