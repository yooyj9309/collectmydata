package com.banksalad.collectmydata.insu.loan.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.common.dto.LoanSummary;
import com.banksalad.collectmydata.insu.loan.dto.LoanBasic;
import com.banksalad.collectmydata.insu.loan.dto.LoanDetail;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class LoanServiceImpl implements LoanService {

  @Override
  public List<LoanBasic> listLoanBasics(ExecutionContext executionContext, String organizationCode,
      List<LoanSummary> loanSummaries) {
    return null;
  }

  @Override
  public List<LoanDetail> listLoanDetails(ExecutionContext executionContext, String organizationCode,
      List<LoanSummary> loanSummaries) {
    return null;
  }
}
