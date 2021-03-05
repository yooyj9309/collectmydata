package com.banksalad.collectmydata.bank.common.db.entity.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountBasicHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DepositAccountBasicHistoryMapper {

  @Mapping(target = "id", ignore = true)
  DepositAccountBasicHistoryEntity toHistoryEntity(DepositAccountBasicEntity DepositAccountBasicEntity);
}
