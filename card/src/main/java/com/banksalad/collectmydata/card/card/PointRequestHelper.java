package com.banksalad.collectmydata.card.card;

import com.banksalad.collectmydata.card.card.dto.ListPointsRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseRequestHelper;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointRequestHelper implements UserBaseRequestHelper<ListPointsRequest> {

  @Override
  public ListPointsRequest make(ExecutionContext executionContext, long searchTimestamp) {
    return ListPointsRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .searchTimestamp(searchTimestamp)
        .build();
  }
}
