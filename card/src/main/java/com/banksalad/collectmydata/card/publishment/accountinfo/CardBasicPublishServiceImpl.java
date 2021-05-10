package com.banksalad.collectmydata.card.publishment.accountinfo;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.card.card.dto.CardBasic;
import com.banksalad.collectmydata.card.common.db.entity.CardEntity;
import com.banksalad.collectmydata.card.common.db.entity.CardSummaryEntity;
import com.banksalad.collectmydata.card.common.db.repository.CardRepository;
import com.banksalad.collectmydata.card.common.db.repository.CardSummaryRepository;
import com.banksalad.collectmydata.card.common.mapper.CardMapper;
import com.banksalad.collectmydata.card.publishment.accountinfo.dto.CardBasicPublishment;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.INVALID_RESPONSE_CODE;

@Service
@RequiredArgsConstructor
public class CardBasicPublishServiceImpl implements CardBasicPublishService {

  private final CardSummaryRepository cardSummaryRepository;
  private final CardRepository cardRepository;
  private final CardMapper cardMapper = Mappers.getMapper(CardMapper.class);

  @Override
  public List<CardBasicPublishment> getCardBasicResponses(long banksaladUserId, String organzationId) {
    List<CardSummaryEntity> cardSummaryEntities = cardSummaryRepository
        .findAllByBanksaladUserIdAndOrganizationIdAndResponseCodeInAndConsentIsTrue(banksaladUserId, organzationId,
            INVALID_RESPONSE_CODE);

    List<CardEntity> cardEntities = new ArrayList<>();

    /** cardId에 매핑되는 card가 존재할 때만 publish하기 위해 ifPresent 사용
     * @author hyunjun
     */
    for (CardSummaryEntity cardSummaryEntity : cardSummaryEntities) {
      cardRepository
          .findByBanksaladUserIdAndOrganizationIdAndCardId(banksaladUserId, organzationId,
              cardSummaryEntity.getCardId()).ifPresent(cardEntities::add);
    }

    return cardEntities.stream()
        .map(cardMapper::entityToPublishmentDto)
        .collect(Collectors.toList());
  }
}
