package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.common.db.entity.CardEntity;
import com.banksalad.collectmydata.card.common.db.entity.CardHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface CardHistoryMapper {

  @Mapping(target = "id", ignore = true)
  CardHistoryEntity toHistoryEntity(CardEntity cardEntity, @MappingTarget CardHistoryEntity cardHistoryEntity);
}
