package com.banksalad.collectmydata.efin.common.mapper;

import com.banksalad.collectmydata.efin.common.db.entity.AccountChargeEntity;
import com.banksalad.collectmydata.efin.common.db.entity.AccountChargeHistoryEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AccountChargeHistoryMapper {

  @Mapping(target = "id", ignore = true)
  AccountChargeHistoryEntity toHistoryEntity(AccountChargeEntity accountChargeEntity);
}
