package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountBasicHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface InvestAccountBasicHistoryMapper {

  @Mapping(target = "id", ignore = true)
  InvestAccountBasicHistoryEntity entityToHistoryEntity(InvestAccountBasicEntity investAccountBasicEntity,
      @MappingTarget InvestAccountBasicHistoryEntity investAccountBasicHistoryEntity);
}
