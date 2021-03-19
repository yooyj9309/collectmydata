package com.banksalad.collectmydata.capital.common.db.mapper;

import com.banksalad.collectmydata.capital.account.dto.AccountBasic;
import com.banksalad.collectmydata.capital.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = {BigDecimalMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountBasicMapper {

  @Mappings(value = {
      @Mapping(target = "lastOfferedRate", qualifiedByName = "BigDecimalScale3")
  })
  AccountBasicEntity dtoToEntity(AccountBasic accountBasic);
}
