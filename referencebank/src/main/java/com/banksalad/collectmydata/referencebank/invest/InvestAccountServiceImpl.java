package com.banksalad.collectmydata.referencebank.invest;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.referencebank.account.dto.Account;
import com.banksalad.collectmydata.referencebank.invest.dto.InvestAccount;
import com.banksalad.collectmydata.referencebank.invest.dto.InvestAccountTransaction;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvestAccountServiceImpl implements InvestAccountService {


  @Override
  public List<InvestAccount> listInvestAccounts(ExecutionContext executionContext, List<Account> accounts) {
    return null;
  }

  @Override
  public List<InvestAccountTransaction> listInvestAccountTransactions(ExecutionContext executionContext, List<Account> accounts) {
    return null;
  }
}
