package com.banksalad.collectmydata.card.publishment.summary;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.card.common.db.repository.CardSummaryRepository;
import com.banksalad.collectmydata.card.common.mapper.CardSummaryMapper;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardSummaryPublishServiceImpl implements CardSummaryPublishService {

  private final CardSummaryRepository cardSummaryRepository;

  private final CardSummaryMapper cardSummaryMapper = Mappers.getMapper(CardSummaryMapper.class);

  @Override
  public List<CardSummary> getCardSummaryResponses(long banksaladUserId, String organizationId) {

    return cardSummaryRepository.findByBanksaladUserIdAndOrganizationIdAndConsentIsTrue(
        banksaladUserId, organizationId).stream()
        .map(cardSummaryMapper::entityToDto)
        .collect(Collectors.toList());
  }
}
