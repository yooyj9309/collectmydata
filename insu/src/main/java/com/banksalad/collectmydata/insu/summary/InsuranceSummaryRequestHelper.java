package com.banksalad.collectmydata.insu.summary;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.insu.summary.dto.ListInsuranceSummariesRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InsuranceSummaryRequestHelper implements SummaryRequestHelper<ListInsuranceSummariesRequest> {

  @Override
  public ListInsuranceSummariesRequest make(ExecutionContext executionContext, long searchTimestamp, String nextPage) {
    return ListInsuranceSummariesRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .searchTimestamp(searchTimestamp)
        .build();
  }
}
