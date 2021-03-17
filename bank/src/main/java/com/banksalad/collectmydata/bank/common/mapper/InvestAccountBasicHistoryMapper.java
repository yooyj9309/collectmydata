package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountBasicEntity;
import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountBasicHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface InvestAccountBasicHistoryMapper {

  @Mapping(target = "id", ignore = true)
  InvestAccountBasicHistoryEntity toInvestAccountBasicHistoryEntity(
      InvestAccountBasicEntity investAccountBasicEntity);
}
