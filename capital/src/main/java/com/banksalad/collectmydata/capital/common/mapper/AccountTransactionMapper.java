package com.banksalad.collectmydata.capital.common.mapper;

import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionEntity;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

// IGNORE policy suppresses complaining the source fields do not exist in the target.
@Mapper(uses = {BigDecimalMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountTransactionMapper {
  
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mappings(value = {
      @Mapping(target = "transAmt", qualifiedByName = "BigDecimalScale3"),
      @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3"),
      @Mapping(target = "principalAmt", qualifiedByName = "BigDecimalScale3"),
      @Mapping(target = "intAmt", qualifiedByName = "BigDecimalScale3")
  })
  void merge(AccountTransactionEntity sourceAccountTransactionEntity,
      @MappingTarget AccountTransactionEntity targetAccountTransactionEntity);
}
