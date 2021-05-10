package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountBasicHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface DepositAccountBasicHistoryMapper {

  @Mapping(target = "id", ignore = true)
  DepositAccountBasicHistoryEntity entityToHistoryEntity(DepositAccountBasicEntity depositAccountBasicEntity,
      @MappingTarget DepositAccountBasicHistoryEntity depositAccountBasicHistoryEntity);
}
