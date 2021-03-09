package com.banksalad.collectmydata.capital.account;

import com.banksalad.collectmydata.capital.account.dto.AccountBasic;
import com.banksalad.collectmydata.capital.account.dto.AccountTransaction;
import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.account.dto.AccountDetail;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

public interface AccountService {

  List<AccountBasic> listAccountBasics(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries);

  List<AccountDetail> listAccountDetails(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries);

  List<AccountTransaction> listAccountTransactions(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries);
}
