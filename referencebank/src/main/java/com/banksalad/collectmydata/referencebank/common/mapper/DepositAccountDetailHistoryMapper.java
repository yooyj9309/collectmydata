package com.banksalad.collectmydata.referencebank.common.mapper;


import com.banksalad.collectmydata.referencebank.common.db.entity.DepositAccountDetailEntity;
import com.banksalad.collectmydata.referencebank.common.db.entity.DepositAccountDetailHistoryEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DepositAccountDetailHistoryMapper {

  @Mapping(target = "id", ignore = true)
  DepositAccountDetailHistoryEntity toHistoryEntity(DepositAccountDetailEntity depositAccountDetailEntity);
}
