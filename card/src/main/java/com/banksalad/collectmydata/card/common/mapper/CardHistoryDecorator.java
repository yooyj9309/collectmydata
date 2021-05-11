package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.common.db.entity.CardEntity;
import com.banksalad.collectmydata.card.common.db.entity.CardHistoryEntity;

public abstract class CardHistoryDecorator implements CardHistoryMapper {

  private CardHistoryMapper decorator;

  public CardHistoryDecorator(CardHistoryMapper decorator) {
    this.decorator = decorator;
  }

  @Override
  public CardHistoryEntity toHistoryEntity(CardEntity cardEntity) {
    CardHistoryEntity cardHistoryEntity = decorator.toHistoryEntity(cardEntity);
    cardHistoryEntity.setCreatedAt(cardEntity.getCreatedAt());
    cardHistoryEntity.setUpdatedAt(cardEntity.getUpdatedAt());
    cardHistoryEntity.setCreatedBy(cardEntity.getCreatedBy());
    cardHistoryEntity.setUpdatedBy(cardEntity.getUpdatedBy());
    return cardHistoryEntity;
  }
}
