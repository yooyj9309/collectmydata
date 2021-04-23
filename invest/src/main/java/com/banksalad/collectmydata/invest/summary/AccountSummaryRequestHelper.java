package com.banksalad.collectmydata.invest.summary;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.invest.summary.dto.ListAccountSummariesRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountSummaryRequestHelper implements SummaryRequestHelper<ListAccountSummariesRequest> {

  @Override
  public ListAccountSummariesRequest make(ExecutionContext executionContext, long searchTimestamp, String nextPage) {
    return ListAccountSummariesRequest.builder()
        .searchTimestamp(searchTimestamp)
        .orgCode(executionContext.getOrganizationCode())
        .build();
  }
}
