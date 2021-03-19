package com.banksalad.collectmydata.insu.summary;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.insu.summary.dto.ListLoanSummariesRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoanSummaryRequestHelper implements SummaryRequestHelper<ListLoanSummariesRequest> {

  @Override
  public ListLoanSummariesRequest make(ExecutionContext executionContext, long searchTimestamp, String nextPage) {
    return ListLoanSummariesRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .searchTimestamp(searchTimestamp)
        .build();
  }
}
