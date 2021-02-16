package com.banksalad.collectmydata.capital.common.service;

import com.banksalad.collectmydata.capital.account.dto.Account;
import com.banksalad.collectmydata.capital.account.dto.AccountDetailResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountBasicResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountResponse;
import com.banksalad.collectmydata.capital.account.dto.AccountTransactionResponse;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.lease.dto.OperatingLeaseResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

public interface ExternalApiService {

  // 6.7.1
  AccountResponse getAccounts(ExecutionContext executionContext, Organization organization);

  // 6.7.2
  AccountBasicResponse getAccountBasic(ExecutionContext executionContext, Organization organization, Account account);

  // 6.7.3
  AccountDetailResponse getAccountDetail(ExecutionContext executionContext, Organization organization, Account account);

  // 6.7.4
  AccountTransactionResponse getAccountTransactions(ExecutionContext executionContext, Organization organization, Account account);

  // 6.7.5
  OperatingLeaseResponse getLeaseBasic(ExecutionContext executionContext, Organization organization, Account account);

  // 6.7.6
}
