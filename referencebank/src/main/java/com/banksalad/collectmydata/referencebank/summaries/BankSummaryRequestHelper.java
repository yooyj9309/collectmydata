package com.banksalad.collectmydata.referencebank.summaries;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.referencebank.summaries.dto.ListAccountSummariesRequest;

import org.springframework.stereotype.Component;

@Component
public class BankSummaryRequestHelper implements SummaryRequestHelper<ListAccountSummariesRequest> {

  @Override
  public ListAccountSummariesRequest make(ExecutionContext executionContext, long searchTimestamp, String nextPage) {
    return ListAccountSummariesRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .searchTimestamp(searchTimestamp)
        .nextPage(nextPage)
        .limit(500)
        .build();
  }
}
