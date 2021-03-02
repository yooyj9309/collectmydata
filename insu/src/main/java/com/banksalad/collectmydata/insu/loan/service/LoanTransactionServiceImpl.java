package com.banksalad.collectmydata.insu.loan.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.common.dto.LoanSummary;
import com.banksalad.collectmydata.insu.loan.dto.LoanTransaction;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class LoanTransactionServiceImpl implements LoanTransactionService {

  @Override
  public List<LoanTransaction> listLoanTransactions(ExecutionContext executionContext, String organizationCode,
      List<LoanSummary> loanSummaries) {
    return null;
  }
}
