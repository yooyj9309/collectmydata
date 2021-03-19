package com.banksalad.collectmydata.capital.account;

import com.banksalad.collectmydata.capital.account.dto.GetAccountDetailRequest;
import com.banksalad.collectmydata.capital.common.service.AccountSummaryService;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountDetailRequestHelper implements AccountInfoRequestHelper<GetAccountDetailRequest, AccountSummary> {

  private final AccountSummaryService accountSummaryService;

  @Override
  public List<AccountSummary> listSummaries(ExecutionContext executionContext) {
    return accountSummaryService
        .listSummariesConsented(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), false);
  }

  @Override
  public GetAccountDetailRequest make(ExecutionContext executionContext, AccountSummary accountSummary) {
    return GetAccountDetailRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .searchTimestamp(accountSummary.getDetailSearchTimestamp())
        .build();
  }
}
