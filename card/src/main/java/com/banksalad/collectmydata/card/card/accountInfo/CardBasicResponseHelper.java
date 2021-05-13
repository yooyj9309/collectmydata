package com.banksalad.collectmydata.card.card.accountInfo;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.card.card.dto.CardBasic;
import com.banksalad.collectmydata.card.card.dto.GetCardBasicResponse;
import com.banksalad.collectmydata.card.common.db.entity.CardEntity;
import com.banksalad.collectmydata.card.common.db.entity.CardHistoryEntity;
import com.banksalad.collectmydata.card.common.db.repository.CardHistoryRepository;
import com.banksalad.collectmydata.card.common.db.repository.CardRepository;
import com.banksalad.collectmydata.card.common.mapper.CardHistoryMapper;
import com.banksalad.collectmydata.card.common.mapper.CardMapper;
import com.banksalad.collectmydata.card.common.service.CardSummaryService;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.ENTITY_EXCLUDE_FIELD;

@Component
@RequiredArgsConstructor
public class CardBasicResponseHelper implements AccountInfoResponseHelper<CardSummary, CardBasic> {

  private final CardSummaryService cardSummaryService;

  private final CardRepository cardRepository;
  private final CardHistoryRepository cardHistoryRepository;

  private final CardMapper cardMapper = Mappers.getMapper(CardMapper.class);
  private final CardHistoryMapper cardHistoryMapper = Mappers.getMapper(CardHistoryMapper.class);

  @Override
  public CardBasic getAccountFromResponse(AccountResponse accountResponse) {
    return ((GetCardBasicResponse) accountResponse).getCardBasic();
  }

  @Override
  public void saveAccountAndHistory(ExecutionContext executionContext, CardSummary cardSummary,
      CardBasic cardBasic) {

    long banksaladUserId = executionContext.getBanksaladUserId();
    String organizationId = executionContext.getOrganizationId();
    LocalDateTime syncedAt = executionContext.getSyncStartedAt();

    CardEntity existingEntity = cardRepository
        .findByBanksaladUserIdAndOrganizationIdAndCardId(banksaladUserId, organizationId, cardSummary.getCardId())
        .orElse(null);

    CardEntity cardEntity = cardMapper.dtoToEntity(cardBasic);
    cardEntity.setSyncedAt(syncedAt);
    cardEntity.setBanksaladUserId(banksaladUserId);
    cardEntity.setOrganizationId(organizationId);
    cardEntity.setCardId(cardSummary.getCardId());
    cardEntity.setConsentId(executionContext.getConsentId());
    cardEntity.setSyncRequestId(executionContext.getSyncRequestId());
    cardEntity.setCreatedBy(executionContext.getRequestedBy());
    cardEntity.setUpdatedBy(executionContext.getRequestedBy());

    if (existingEntity != null) {
      cardEntity.setId(existingEntity.getId());
      cardEntity.setCreatedAt(existingEntity.getCreatedAt());
      cardEntity.setCreatedBy(existingEntity.getCreatedBy());
    }

    /* update if entity has changed */
    if (!ObjectComparator.isSame(existingEntity, cardEntity, ENTITY_EXCLUDE_FIELD)) {
      cardRepository.save(cardEntity);
      cardHistoryRepository.save(cardHistoryMapper.toHistoryEntity(cardEntity, CardHistoryEntity.builder().build()));
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, CardSummary cardSummary,
      long searchTimestamp) {
    cardSummaryService.updateSearchTimestamp(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(), cardSummary, searchTimestamp);

  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, CardSummary cardSummary, String responseCode) {
    cardSummaryService.updateResponseCode(executionContext.getBanksaladUserId(),
        executionContext.getOrganizationId(), cardSummary, responseCode);
  }
}
