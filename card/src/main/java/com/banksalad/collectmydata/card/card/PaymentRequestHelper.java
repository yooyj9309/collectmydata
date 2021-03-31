package com.banksalad.collectmydata.card.card;

import com.banksalad.collectmydata.card.card.dto.ListPaymentsRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseRequestHelper;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentRequestHelper implements UserBaseRequestHelper<ListPaymentsRequest> {

  @Override
  public ListPaymentsRequest make(ExecutionContext executionContext, long searchTimestamp) {
    return ListPaymentsRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .searchTimestamp(searchTimestamp)
        .build();
  }
}
