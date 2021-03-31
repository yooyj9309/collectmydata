package com.banksalad.collectmydata.card.loan;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.card.loan.dto.GetLoanSummaryRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseRequestHelper;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoanSummaryRequestHelper implements UserBaseRequestHelper<GetLoanSummaryRequest> {

  @Override
  public GetLoanSummaryRequest make(ExecutionContext executionContext, long searchTimestamp) {
    return GetLoanSummaryRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .searchTimestamp(searchTimestamp)
        .build();
  }
}
