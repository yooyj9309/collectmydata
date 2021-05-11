package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.common.db.entity.BillDetailEntity;
import com.banksalad.collectmydata.card.common.db.entity.BillDetailHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface BillDetailHistoryMapper {

  @Mapping(target = "id", ignore = true)
  BillDetailHistoryEntity toHistoryEntity(BillDetailEntity billDetailEntity,
      @MappingTarget BillDetailHistoryEntity billDetailHistoryEntity);
}
