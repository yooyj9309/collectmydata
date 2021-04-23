package com.banksalad.collectmydata.efin.common.mapper;

import com.banksalad.collectmydata.efin.common.db.entity.AccountBalanceEntity;
import com.banksalad.collectmydata.efin.common.db.entity.AccountBalanceHistoryEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BalanceHistoryMapper {

  @Mapping(target = "id", ignore = true)
  AccountBalanceHistoryEntity toHistoryEntity(AccountBalanceEntity accountBalanceEntity);
}
