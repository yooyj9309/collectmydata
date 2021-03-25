package com.banksalad.collectmydata.efin.common.mapper;

import com.banksalad.collectmydata.efin.common.db.entity.BalanceEntity;
import com.banksalad.collectmydata.efin.common.db.entity.BalanceHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BalanceHistoryMapper {

  @Mapping(target = "id", ignore = true)
  BalanceHistoryEntity toHistoryEntity(BalanceEntity balanceEntity);
}
