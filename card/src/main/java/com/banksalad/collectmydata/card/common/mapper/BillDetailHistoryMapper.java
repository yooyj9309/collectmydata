package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.common.db.entity.BillDetailEntity;
import com.banksalad.collectmydata.card.common.db.entity.BillDetailHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BillDetailHistoryMapper {

  @Mapping(target = "id", ignore = true)
  BillDetailHistoryEntity toHistoryEntity(BillDetailEntity billDetailEntity);
}
