package com.banksalad.collectmydata.irp.summary;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import com.banksalad.collectmydata.irp.common.dto.ListIrpAccountSummariesRequest;

@Component
public class IrpSummaryRequestHelper implements SummaryRequestHelper<ListIrpAccountSummariesRequest> {

  @Override
  public ListIrpAccountSummariesRequest make(ExecutionContext executionContext, long searchTimestamp, String nextPage) {

    return ListIrpAccountSummariesRequest.builder()
        .searchTimestamp(searchTimestamp)
        .orgCode(executionContext.getOrganizationCode())
        .build();
  }
}
