package com.banksalad.collectmydata.capital.account;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.capital.account.dto.GetAccountBasicRequest;
import com.banksalad.collectmydata.capital.common.service.AccountSummaryService;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountBasicRequestHelper implements AccountInfoRequestHelper<GetAccountBasicRequest, AccountSummary> {

  private final AccountSummaryService accountSummaryService;

  @Override
  public List<AccountSummary> listSummaries(ExecutionContext executionContext) {
    return accountSummaryService
        .listSummariesConsented(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), false);
  }

  @Override
  public GetAccountBasicRequest make(ExecutionContext executionContext, AccountSummary accountSummary) {
    return GetAccountBasicRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .searchTimestamp(accountSummary.getBasicSearchTimestamp())
        .build();
  }
}
