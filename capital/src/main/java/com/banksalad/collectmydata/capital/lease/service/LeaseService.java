package com.banksalad.collectmydata.capital.lease.service;

import com.banksalad.collectmydata.capital.account.dto.Account;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.lease.dto.OperatingLease;
import com.banksalad.collectmydata.capital.lease.dto.OperatingLeaseTransaction;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface LeaseService {

  List<OperatingLease> syncLeaseBasic(ExecutionContext executionContext, Organization organization,
      List<Account> accountList);

  List<OperatingLeaseTransaction> syncLeaseTransaction(ExecutionContext executionContext, Organization organization,
      List<Account> accountList);
}
