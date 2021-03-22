package com.banksalad.collectmydata.capital.common.mapper;

import com.banksalad.collectmydata.capital.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountBasicHistoryEntity;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = {BigDecimalMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountBasicHistoryMapper {

  @Mappings(value = {
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "lastOfferedRate", qualifiedByName = "BigDecimalScale3")
  })
  AccountBasicHistoryEntity toHistoryEntity(AccountBasicEntity accountBasicEntity);
}
