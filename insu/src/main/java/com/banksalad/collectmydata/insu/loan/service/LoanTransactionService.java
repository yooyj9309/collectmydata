package com.banksalad.collectmydata.insu.loan.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.common.dto.LoanSummary;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransaction;

import java.util.List;

public interface LoanTransactionService {

  List<LoanTransaction> listLoanTransactions(ExecutionContext executionContext, String organizationCode,
      List<LoanSummary> loanSummaries);
}
