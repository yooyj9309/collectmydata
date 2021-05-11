package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.common.db.entity.RevolvingEntity;
import com.banksalad.collectmydata.card.common.db.entity.RevolvingHistoryEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface RevolvingHistoryMapper {

  @Mapping(target = "id", ignore = true)
  RevolvingHistoryEntity toHistoryEntity(RevolvingEntity revolvingEntity, @MappingTarget RevolvingHistoryEntity revolvingHistoryEntity);
}
