package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.common.db.entity.BillEntity;
import com.banksalad.collectmydata.card.common.db.entity.BillHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BillHistoryMapper {

  @Mapping(target = "id", ignore = true)
  BillHistoryEntity toHistoryEntity(BillEntity billEntity);
}
