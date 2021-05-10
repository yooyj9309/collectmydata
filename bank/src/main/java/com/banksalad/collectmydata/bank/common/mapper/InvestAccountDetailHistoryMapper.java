package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountDetailEntity;
import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountDetailHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface InvestAccountDetailHistoryMapper {

  @Mapping(target = "id", ignore = true)
  InvestAccountDetailHistoryEntity entityToHistoryEntity(InvestAccountDetailEntity investAccountDetailEntity,
      @MappingTarget InvestAccountDetailHistoryEntity investAccountDetailHistoryEntity);
}
