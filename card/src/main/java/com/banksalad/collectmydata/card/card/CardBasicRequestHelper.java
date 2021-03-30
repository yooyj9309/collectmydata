package com.banksalad.collectmydata.card.card;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.card.card.dto.GetCardBasicRequest;
import com.banksalad.collectmydata.card.common.service.CardSummaryService;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoRequestHelper;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CardBasicRequestHelper implements AccountInfoRequestHelper<GetCardBasicRequest, CardSummary> {

  private final CardSummaryService cardSummaryService;

  @Override
  public List<CardSummary> listSummaries(ExecutionContext executionContext) {
    return cardSummaryService
        .listSummariesConsented(executionContext.getBanksaladUserId(), executionContext.getOrganizationId());
  }

  @Override
  public GetCardBasicRequest make(ExecutionContext executionContext, CardSummary cardSummary) {
    return GetCardBasicRequest.builder()
        .cardId(cardSummary.getCardId())
        .orgCode(executionContext.getOrganizationCode())
        .searchTimestamp(cardSummary.getSearchTimestamp())
        .build();
  }
}
