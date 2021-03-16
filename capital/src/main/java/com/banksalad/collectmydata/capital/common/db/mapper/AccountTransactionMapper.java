package com.banksalad.collectmydata.capital.common.db.mapper;

import com.banksalad.collectmydata.capital.account.dto.AccountTransaction;
import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

// IGNORE policy suppresses complaining the source fields do not exist in the target.
@Mapper(uses = {BigDecimalMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountTransactionMapper {

  // Update only non-null fields partially with the below @BeanMapping.
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void merge(AccountTransaction accountTransaction, @MappingTarget AccountTransactionEntity accountTransactionEntity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void merge(AccountTransactionEntity accountTransactionEntity, @MappingTarget AccountTransaction accountTransaction);
}
