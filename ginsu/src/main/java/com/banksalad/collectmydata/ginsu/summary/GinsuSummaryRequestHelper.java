package com.banksalad.collectmydata.ginsu.summary;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.ginsu.summary.dto.ListGinsuSummariesRequest;

@Component
public class GinsuSummaryRequestHelper implements SummaryRequestHelper<ListGinsuSummariesRequest> {

  @Override
  public ListGinsuSummariesRequest make(ExecutionContext executionContext, long searchTimestamp, String nextPage) {
    return ListGinsuSummariesRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .searchTimestamp(searchTimestamp)
        .build();
  }
}
