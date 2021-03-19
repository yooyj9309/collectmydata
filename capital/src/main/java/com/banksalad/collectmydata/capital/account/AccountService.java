package com.banksalad.collectmydata.capital.account;

import com.banksalad.collectmydata.capital.account.dto.AccountDetail;
import com.banksalad.collectmydata.capital.common.dto.Organization;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;

import java.util.List;

@Deprecated
public interface AccountService {

  List<AccountDetail> listAccountDetails(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries);
}
