package com.banksalad.collectmydata.bank.common.db.entity.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountDetailEntity;
import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountDetailHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DepositAccountDetailHistoryMapper {

  @Mapping(target = "id", ignore = true)
  DepositAccountDetailHistoryEntity toHistoryEntity(DepositAccountDetailEntity depositAccountDetailEntity);
}
