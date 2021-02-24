package com.banksalad.collectmydata.capital.common.service;

import com.banksalad.collectmydata.capital.common.dto.Account;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface AccountService {

  List<Account> listAccounts(ExecutionContext executionContext, Organization organization);

}
