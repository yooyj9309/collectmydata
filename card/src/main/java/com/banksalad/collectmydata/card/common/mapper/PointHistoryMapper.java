package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.common.db.entity.CardEntity;
import com.banksalad.collectmydata.card.common.db.entity.CardHistoryEntity;
import com.banksalad.collectmydata.card.common.db.entity.PointEntity;
import com.banksalad.collectmydata.card.common.db.entity.PointHistoryEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PointHistoryMapper {

  @Mapping(target = "id", ignore = true)
  PointHistoryEntity toHistoryEntity(PointEntity pointEntity);
}
