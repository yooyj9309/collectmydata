package com.banksalad.collectmydata.referencebank.common.mapper;


import com.banksalad.collectmydata.referencebank.common.db.entity.DepositAccountBasicEntity;
import com.banksalad.collectmydata.referencebank.common.db.entity.DepositAccountBasicHistoryEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DepositAccountBasicHistoryMapper {

  @Mapping(target = "id", ignore = true)
  DepositAccountBasicHistoryEntity toHistoryEntity(DepositAccountBasicEntity DepositAccountBasicEntity);
}
