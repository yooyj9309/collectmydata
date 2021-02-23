package com.banksalad.collectmydata.referencebank.loan;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.referencebank.account.dto.Account;
import com.banksalad.collectmydata.referencebank.loan.dto.LoanAccount;
import com.banksalad.collectmydata.referencebank.loan.dto.LoanAccountTransaction;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanAccountServiceImpl implements LoanAccountService {


  @Override
  public List<LoanAccount> listLoanAccounts(ExecutionContext executionContext, List<Account> accounts) {
    return null;
  }

  @Override
  public List<LoanAccountTransaction> listLoanAccountTransactions(ExecutionContext executionContext, List<Account> accounts) {
    return null;
  }
}
