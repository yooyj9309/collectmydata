package com.banksalad.collectmydata.card.summary;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.card.summary.dto.ListCardSummariesRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CardSummaryRequestHelper implements SummaryRequestHelper<ListCardSummariesRequest> {

  @Override
  public ListCardSummariesRequest make(ExecutionContext executionContext, long searchTimestamp, String nextPage) {
    return ListCardSummariesRequest.builder()
        .searchTimestamp(searchTimestamp)
        .orgCode(executionContext.getOrganizationCode())
        .build();
  }
}
