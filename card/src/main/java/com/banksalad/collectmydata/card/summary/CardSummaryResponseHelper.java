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
import java.util.Optional;

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

  /**
   * FIXME : 재발급 카드가 존재할 경우, card_id가 같은 카드 2개가 올 수 있음. -> 추후 저장로직 변경 필요
   * 6.3.1 저장로직 : upsert
   * @author hyunjun
   */
  @Override
  public void saveSummary(ExecutionContext executionContext, CardSummary cardSummary) {

    final long banksaladUserId = executionContext.getBanksaladUserId();
    final String organizationId = executionContext.getOrganizationId();
    final String cardId = cardSummary.getCardId();
    final LocalDateTime syncedAt = executionContext.getSyncStartedAt();

    Optional<CardSummaryEntity> existingEntity = cardSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndCardId(banksaladUserId, organizationId, cardId);

    CardSummaryEntity newCardSummaryEntity = CardSummaryEntity.builder().build();
    cardSummaryMapper.mergeDtoToEntity(cardSummary, newCardSummaryEntity);

    newCardSummaryEntity.setBanksaladUserId(banksaladUserId);
    newCardSummaryEntity.setOrganizationId(organizationId);
    newCardSummaryEntity.setSyncedAt(syncedAt);
    newCardSummaryEntity.setCreatedBy(executionContext.getRequestedBy());
    newCardSummaryEntity.setUpdatedBy(executionContext.getRequestedBy());
    newCardSummaryEntity.setConsentId(executionContext.getConsentId());
    newCardSummaryEntity.setSyncRequestId(executionContext.getSyncRequestId());

    /* update */
    if (existingEntity.isPresent()) {
      CardSummaryEntity cardSummaryEntity = existingEntity.get();
      newCardSummaryEntity.setId(cardSummaryEntity.getId());
      newCardSummaryEntity.setCreatedBy(cardSummaryEntity.getCreatedBy());
      newCardSummaryEntity.setCreatedAt(cardSummaryEntity.getCreatedAt());
    }

    cardSummaryRepository.save(newCardSummaryEntity);
  }
}
