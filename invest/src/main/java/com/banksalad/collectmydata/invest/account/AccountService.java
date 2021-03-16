package com.banksalad.collectmydata.invest.account;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.invest.account.dto.AccountBasic;
import com.banksalad.collectmydata.invest.common.dto.AccountSummary;

import java.util.List;

public interface AccountService {

  List<AccountBasic> listAccountBasics(ExecutionContext executionContext, Organization organization,
      List<AccountSummary> accountSummaries);
}
