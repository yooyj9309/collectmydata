package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountDetailEntity;
import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountDetailHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface DepositAccountDetailHistoryMapper {

  @Mapping(target = "id", ignore = true)
  DepositAccountDetailHistoryEntity entityToHistoryEntity(DepositAccountDetailEntity depositAccountDetailEntity,
      @MappingTarget DepositAccountDetailHistoryEntity depositAccountDetailHistoryEntity);
}
