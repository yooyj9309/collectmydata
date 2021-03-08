package com.banksalad.collectmydata.bank.common.db.entity.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountDetailEntity;
import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountDetailHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface InvestAccountDetailHistoryMapper {

  @Mapping(target = "id", ignore = true)
  InvestAccountDetailHistoryEntity toInvestAccountDetailHistoryEntity(
      InvestAccountDetailEntity investAccountDetailEntity);
}
