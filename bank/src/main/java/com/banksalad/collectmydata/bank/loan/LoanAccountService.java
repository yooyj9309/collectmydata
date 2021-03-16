package com.banksalad.collectmydata.bank.loan;

import com.banksalad.collectmydata.bank.summary.dto.AccountSummary;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountBasic;
import com.banksalad.collectmydata.bank.loan.dto.LoanAccountDetail;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface LoanAccountService {

  List<LoanAccountBasic> listLoanAccountBasics(ExecutionContext executionContext,
      List<AccountSummary> accountSummaries);

  List<LoanAccountDetail> listLoanAccountDetails(ExecutionContext executionContext,
      List<AccountSummary> accountSummaries);
}
