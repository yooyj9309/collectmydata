package com.banksalad.collectmydata.ri.bank.account;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.exception.CollectException;
import com.banksalad.collectmydata.ri.bank.account.dto.Account;

import java.util.List;

public interface AccountService {

  List<Account> getAccounts(ExecutionContext executionContext) throws CollectException;
}
