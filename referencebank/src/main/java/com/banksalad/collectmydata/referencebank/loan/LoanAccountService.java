package com.banksalad.collectmydata.referencebank.loan;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.referencebank.account.dto.Account;
import com.banksalad.collectmydata.referencebank.loan.dto.LoanAccount;
import com.banksalad.collectmydata.referencebank.loan.dto.LoanAccountTransaction;

import java.util.List;

public interface LoanAccountService {

  List<LoanAccount> listLoanAccounts(ExecutionContext executionContext, List<Account> accounts);

  List<LoanAccountTransaction> listLoanAccountTransactions(ExecutionContext executionContext, List<Account> accounts);
}
