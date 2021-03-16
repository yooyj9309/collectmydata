package com.banksalad.collectmydata.insu.loan.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.insu.summary.dto.LoanSummary;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransaction;

import java.util.List;

public interface LoanTransactionService {

  List<LoanTransaction> listLoanTransactions(ExecutionContext executionContext, Organization organization,
      List<LoanSummary> loanSummaries);
}
