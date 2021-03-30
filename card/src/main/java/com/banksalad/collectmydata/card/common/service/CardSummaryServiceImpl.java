package com.banksalad.collectmydata.card.common.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.card.common.db.entity.CardSummaryEntity;
import com.banksalad.collectmydata.card.common.db.repository.CardSummaryRepository;
import com.banksalad.collectmydata.card.common.mapper.CardSummaryMapper;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import com.banksalad.collectmydata.common.exception.CollectRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardSummaryServiceImpl implements CardSummaryService {

  private final CardSummaryRepository cardSummaryRepository;

  private final CardSummaryMapper cardSummaryMapper = Mappers.getMapper(CardSummaryMapper.class);

  @Override
  public List<CardSummary> listSummariesConsented(long banksaladUserId, String organizationId) {
    return cardSummaryRepository
        .findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(banksaladUserId, organizationId)
        .stream()
        .map(cardSummaryMapper::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  public void updateSearchTimestamp(long banksaladUserId, String organizationId, CardSummary cardSummary,
      long searchTimestamp) {
    CardSummaryEntity cardSummaryEntity = getCardSummaryEntity(banksaladUserId, organizationId,
        cardSummary.getCardId());
    cardSummaryEntity.setSearchTimestamp(searchTimestamp);
    cardSummaryRepository.save(cardSummaryEntity);
  }

  @Override
  public void updateResponseCode(long banksaladUserId, String organizationId, CardSummary cardSummary,
      String responseCode) {
    CardSummaryEntity cardSummaryEntity = getCardSummaryEntity(banksaladUserId, organizationId,
        cardSummary.getCardId());
    cardSummaryEntity.setResponseCode(responseCode);
    cardSummaryRepository.save(cardSummaryEntity);
  }

  private CardSummaryEntity getCardSummaryEntity(long banksaladUserId, String organizationId, String cardId) {
    return cardSummaryRepository.findByBanksaladUserIdAndOrganizationIdAndCardId(
        banksaladUserId,
        organizationId,
        cardId
    ).orElseThrow(() -> new CollectRuntimeException("No data CardSummaryEntity"));
  }
}
