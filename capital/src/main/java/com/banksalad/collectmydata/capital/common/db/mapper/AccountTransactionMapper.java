package com.banksalad.collectmydata.capital.common.db.mapper;

import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

// IGNORE policy suppresses complaining the source fields do not exist in the target.
@Mapper(uses = {BigDecimalMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountTransactionMapper {
  
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void merge(AccountTransactionEntity sourceAccountTransactionEntity,
      @MappingTarget AccountTransactionEntity targetAccountTransactionEntity);

}
