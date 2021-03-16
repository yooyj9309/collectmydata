package com.banksalad.collectmydata.bank.invest;

import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountTransaction;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface InvestAccountTransactionService {

  List<InvestAccountTransaction> listInvestAccountTransactions(ExecutionContext executionContext,
      List<AccountSummary> accountSummaries);
}
