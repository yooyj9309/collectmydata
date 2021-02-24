package com.banksalad.collectmydata.capital.loan;

import com.banksalad.collectmydata.capital.common.dto.Account;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccount;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountTransaction;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface LoanAccountService {

  List<LoanAccount> listLoanAccounts(ExecutionContext executionContext, Organization organization,
      List<Account> accounts);

  List<LoanAccount> listLoanAccountBasics(ExecutionContext executionContext, Organization organization,
      List<Account> accounts);

  List<LoanAccount> listLoanAccountDetails(ExecutionContext executionContext, Organization organization,
      List<Account> accounts);

  List<LoanAccountTransaction> listAccountTransactions(ExecutionContext executionContext, Organization organization,
      List<Account> accounts);

  void updateSearchTimestampOnAccount(long banksaladUserId, String organizationId, Account account);
}
