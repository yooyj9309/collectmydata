package com.banksalad.collectmydata.card.card;

import com.banksalad.collectmydata.card.card.dto.ListRevolvingsRequest;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.userbase.UserBaseRequestHelper;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RevolvingRequestHelper implements UserBaseRequestHelper<ListRevolvingsRequest> {

  private static final long INITIAL_SEARCH_TIMESTAMP = 0L;

  @Override
  public ListRevolvingsRequest make(ExecutionContext executionContext, long searchTimestamp) {

    return ListRevolvingsRequest.builder()
        .orgCode(executionContext.getOrganizationCode())
        .searchTimestamp(INITIAL_SEARCH_TIMESTAMP)
        .build();
  }
}
