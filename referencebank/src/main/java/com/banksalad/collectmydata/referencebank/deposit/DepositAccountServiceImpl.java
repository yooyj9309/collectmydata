package com.banksalad.collectmydata.referencebank.deposit;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.referencebank.account.dto.Account;
import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccount;
import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccountTransaction;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepositAccountServiceImpl implements DepositAccountService {

  @Override
  public List<DepositAccount> listDepositAccounts(ExecutionContext executionContext, List<Account> accounts) {
    return null;
  }

  @Override
  public List<DepositAccountTransaction> listDepositAccountTransactions(ExecutionContext executionContext,
      List<Account> accounts) {
    return null;
  }
}
