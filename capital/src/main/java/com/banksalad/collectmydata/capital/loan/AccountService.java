package com.banksalad.collectmydata.capital.loan;

import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.loan.dto.AccountBasic;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccount;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountTransaction;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface AccountService {

  List<LoanAccount> listLoanAccounts(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries);

  List<AccountBasic> listLoanAccountBasics(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries);

  List<LoanAccount> listLoanAccountDetails(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries);

  List<LoanAccountTransaction> listAccountTransactions(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries);
}
