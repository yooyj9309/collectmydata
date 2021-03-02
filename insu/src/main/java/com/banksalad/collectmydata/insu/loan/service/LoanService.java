package com.banksalad.collectmydata.insu.loan.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.common.dto.LoanSummary;
import com.banksalad.collectmydata.insu.loan.dto.LoanBasic;
import com.banksalad.collectmydata.insu.loan.dto.LoanDetail;

import java.util.List;

public interface LoanService {

  List<LoanBasic> listLoanBasics(ExecutionContext executionContext, String organizationCode,
      List<LoanSummary> loanSummaries);

  List<LoanDetail> listLoanDetails(ExecutionContext executionContext, String organizationCode,
      List<LoanSummary> loanSummaries);
}
