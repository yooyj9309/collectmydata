package com.banksalad.collectmydata.bank.deposit;

import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountTransaction;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface DepositAccountTransactionService {

  List<DepositAccountTransaction> listDepositAccountTransactions(ExecutionContext executionContext,
      List<AccountSummary> accountSummaries);
}
