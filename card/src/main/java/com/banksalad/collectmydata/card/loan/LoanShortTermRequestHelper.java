package com.banksalad.collectmydata.card.loan;

import com.banksalad.collectmydata.card.loan.dto.ListLoanShortTermsRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseRequestHelper;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoanShortTermRequestHelper implements UserBaseRequestHelper<ListLoanShortTermsRequest> {

  private static final long INITIAL_SEARCH_TIMESTAMP = 0L;

  @Override
  public ListLoanShortTermsRequest make(ExecutionContext executionContext, long searchTimestamp) {
    return ListLoanShortTermsRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .searchTimestamp(INITIAL_SEARCH_TIMESTAMP)
        .build();
  }
}
