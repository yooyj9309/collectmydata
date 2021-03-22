package com.banksalad.collectmydata.bank.summary;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.bank.summary.dto.ListAccountSummariesRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryRequestHelper;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.DEFAULT_PAGING_LIMIT;

@Component
public class BankSummaryRequestHelper implements SummaryRequestHelper<ListAccountSummariesRequest> {

  @Override
  public ListAccountSummariesRequest make(ExecutionContext executionContext, long searchTimestamp, String nextPage) {
    return ListAccountSummariesRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .searchTimestamp(searchTimestamp)
        .nextPage(nextPage)
        .limit(DEFAULT_PAGING_LIMIT)
        .build();
  }
}
