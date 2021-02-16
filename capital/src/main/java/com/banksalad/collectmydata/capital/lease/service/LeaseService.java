package com.banksalad.collectmydata.capital.lease.service;

import com.banksalad.collectmydata.capital.account.dto.Account;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface LeaseService {

  void syncAllLeaseInfo(ExecutionContext executionContext, Organization organization, List<Account> accountList);

  void syncLeaseBasic(ExecutionContext executionContext, Organization organization, List<Account> accountList);

  void syncLeaseTransaction(ExecutionContext executionContext, Organization organization, List<Account> accountList);
}
