package com.banksalad.collectmydata.referencebank.account;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.exception.CollectException;
import com.banksalad.collectmydata.referencebank.account.dto.Account;

import java.util.List;

public interface AccountService {

  List<Account> listAccounts(ExecutionContext executionContext) throws CollectException;
}
