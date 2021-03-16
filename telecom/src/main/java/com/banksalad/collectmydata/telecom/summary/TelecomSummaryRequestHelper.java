package com.banksalad.collectmydata.telecom.summary;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.telecom.summary.dto.ListTelecomSummariesRequest;

import org.springframework.stereotype.Component;

@Component
public class TelecomSummaryRequestHelper implements SummaryRequestHelper<ListTelecomSummariesRequest> {

  @Override
  public ListTelecomSummariesRequest make(ExecutionContext executionContext, long searchTimestamp, String nextPage) {
    return ListTelecomSummariesRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .searchTimestamp(searchTimestamp)
        .build();
  }
}
