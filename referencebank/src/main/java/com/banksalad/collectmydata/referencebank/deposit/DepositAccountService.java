package com.banksalad.collectmydata.referencebank.deposit;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.referencebank.account.dto.Account;
import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccount;
import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccountTransaction;

import java.util.List;

public interface DepositAccountService {

  List<DepositAccount> listDepositAccounts(ExecutionContext executionContext, List<Account> accounts);

  List<DepositAccountTransaction> listDepositAccountTransactions(ExecutionContext executionContext, List<Account> accounts);
}
