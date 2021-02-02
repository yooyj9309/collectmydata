package com.banksalad.collectmydata.bank.account;

import com.banksalad.collectmydata.bank.account.dto.Account;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.exception.CollectException;

import java.util.List;

public interface AccountService {

  List<Account> getAccounts(ExecutionContext executionContext) throws CollectException;
}
