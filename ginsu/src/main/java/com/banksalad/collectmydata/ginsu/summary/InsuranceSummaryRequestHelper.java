package com.banksalad.collectmydata.ginsu.summary;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.ginsu.summary.dto.ListInsuranceSummariesRequest;

@Component
public class InsuranceSummaryRequestHelper implements SummaryRequestHelper<ListInsuranceSummariesRequest> {

  @Override
  public ListInsuranceSummariesRequest make(ExecutionContext executionContext, long searchTimestamp, String nextPage) {
    return ListInsuranceSummariesRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .searchTimestamp(searchTimestamp)
        .build();
  }
}
