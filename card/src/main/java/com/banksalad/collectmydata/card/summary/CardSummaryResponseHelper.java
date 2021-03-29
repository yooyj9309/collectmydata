package com.banksalad.collectmydata.card.summary;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.card.common.db.entity.CardSummaryEntity;
import com.banksalad.collectmydata.card.common.db.repository.CardSummaryRepository;
import com.banksalad.collectmydata.card.common.mapper.CardSummaryMapper;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import com.banksalad.collectmydata.card.summary.dto.ListCardSummariesResponse;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.finance.api.summary.SummaryResponseHelper;
import com.banksalad.collectmydata.finance.api.summary.dto.SummaryResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class CardSummaryResponseHelper implements SummaryResponseHelper<CardSummary> {

  private final CardSummaryRepository cardSummaryRepository;

  private final CardSummaryMapper cardSummaryMapper = Mappers.getMapper(CardSummaryMapper.class);

  @Override
  public Iterator<CardSummary> iterator(SummaryResponse response) {
    return ((ListCardSummariesResponse) response).getCardSummaries().iterator();
  }

  @Override
  public void saveOrganizationUser(ExecutionContext executionContext, SummaryResponse response) {
    // do nothing
  }

  @Override
  public void saveSummary(ExecutionContext executionContext, CardSummary cardSummary) {

    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final String cardId = cardSummary.getCardId();
    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();

    CardSummaryEntity cardSummaryEntity = cardSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndCardId(banksaladUserId, organizationId, cardId)
        .orElse(CardSummaryEntity.builder().build());

    cardSummaryMapper.mergeDtoToEntity(cardSummary, cardSummaryEntity);
    cardSummaryEntity.setBanksaladUserId(banksaladUserId);
    cardSummaryEntity.setOrganizationId(organizationId);
    cardSummaryEntity.setSyncedAt(syncedAt);
    cardSummaryRepository.save(cardSummaryEntity);
  }
}
