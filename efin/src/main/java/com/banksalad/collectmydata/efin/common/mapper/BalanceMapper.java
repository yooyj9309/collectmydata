package com.banksalad.collectmydata.efin.common.mapper;

import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.efin.account.dto.AccountBalance;
import com.banksalad.collectmydata.efin.common.db.entity.AccountBalanceEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface BalanceMapper {

  @Mappings(
      value = {
          @Mapping(target = "totalBalanceAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "chargeBalanceAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "reserveBalanceAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "reserveDueAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "expDueAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "limitAmt", qualifiedByName = "BigDecimalScale3"),
      }
  )
  AccountBalance entityToDto(AccountBalanceEntity accountBalanceEntity);

  @Mappings(
      value = {
          @Mapping(target = "totalBalanceAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "chargeBalanceAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "reserveBalanceAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "reserveDueAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "expDueAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "limitAmt", qualifiedByName = "BigDecimalScale3"),
      }
  )
  AccountBalanceEntity dtoToEntity(AccountBalance accountBalance);
}
