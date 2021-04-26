package com.banksalad.collectmydata.efin.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.efin.account.dto.GetAccountChargeRequest;
import com.banksalad.collectmydata.efin.common.service.AccountSummaryService;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountChargeRequestHelper implements AccountInfoRequestHelper<GetAccountChargeRequest, AccountSummary> {

  private final AccountSummaryService accountSummaryService;

  @Override
  public List<AccountSummary> listSummaries(ExecutionContext executionContext) {
    return accountSummaryService
        .listSummariesConsented(executionContext.getBanksaladUserId(), executionContext.getOrganizationId());
  }

  @Override
  public GetAccountChargeRequest make(ExecutionContext executionContext, AccountSummary accountSummary) {
    return GetAccountChargeRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .subKey(accountSummary.getSubKey())
        .searchTimestamp(accountSummary.getChargeSearchTimestamp())
        .build();
  }
}
