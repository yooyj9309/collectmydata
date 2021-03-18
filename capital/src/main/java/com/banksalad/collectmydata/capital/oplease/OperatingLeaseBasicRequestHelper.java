package com.banksalad.collectmydata.capital.oplease;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.capital.common.service.AccountSummaryService;
import com.banksalad.collectmydata.capital.oplease.dto.GetOperatingLeaseBasicRequest;
import com.banksalad.collectmydata.capital.summary.dto.AccountSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OperatingLeaseBasicRequestHelper implements
    AccountInfoRequestHelper<GetOperatingLeaseBasicRequest, AccountSummary> {

  private final AccountSummaryService accountSummaryService;

  @Override
  public List<AccountSummary> listSummaries(ExecutionContext executionContext) {
    return accountSummaryService
        .listSummariesConsented(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(), true);
  }

  @Override
  public GetOperatingLeaseBasicRequest make(ExecutionContext executionContext, AccountSummary accountSummary) {
    return GetOperatingLeaseBasicRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .accountNum(accountSummary.getAccountNum())
        .seqno(accountSummary.getSeqno())
        .searchTimestamp(accountSummary.getOperatingLeaseBasicSearchTimestamp())
        .build();
  }
}
