package com.banksalad.collectmydata.invest.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.organization.Organization;
import com.banksalad.collectmydata.invest.common.dto.AccountSummary;

import java.util.List;

public interface AccountSummaryService {
  List<AccountSummary> listAccountSummaries(ExecutionContext executionContext, Organization organization);
}
