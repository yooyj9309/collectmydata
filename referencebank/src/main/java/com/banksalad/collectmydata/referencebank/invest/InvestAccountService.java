package com.banksalad.collectmydata.referencebank.invest;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.referencebank.account.dto.Account;
import com.banksalad.collectmydata.referencebank.invest.dto.InvestAccount;
import com.banksalad.collectmydata.referencebank.invest.dto.InvestAccountTransaction;

import java.util.List;

public interface InvestAccountService {

  List<InvestAccount> listInvestAccounts(ExecutionContext executionContext, List<Account> accounts);

  List<InvestAccountTransaction> listInvestAccountTransactions(ExecutionContext executionContext, List<Account> accounts);
}
