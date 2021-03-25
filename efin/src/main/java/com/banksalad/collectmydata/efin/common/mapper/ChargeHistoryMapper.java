package com.banksalad.collectmydata.efin.common.mapper;

import com.banksalad.collectmydata.efin.common.db.entity.ChargeEntity;
import com.banksalad.collectmydata.efin.common.db.entity.ChargeHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ChargeHistoryMapper {

  @Mapping(target = "id", ignore = true)
  ChargeHistoryEntity toHistoryEntity(ChargeEntity chargeEntity);
}
