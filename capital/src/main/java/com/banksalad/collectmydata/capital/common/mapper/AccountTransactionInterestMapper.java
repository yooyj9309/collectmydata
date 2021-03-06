package com.banksalad.collectmydata.capital.common.mapper;

import com.banksalad.collectmydata.capital.account.dto.AccountTransactionInterest;
import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionInterestEntity;
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
public interface AccountTransactionInterestMapper {

  // Update only non-null fields partially with the below @BeanMapping.
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mappings(value = {
      // Must ignore id from `accountTransactionEntity`.
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "intRate", qualifiedByName = "BigDecimalScale3")
  })
  AccountTransactionInterestEntity toEntity(AccountTransactionEntity accountTransactionEntity,
      AccountTransactionInterest accountTransactionInterest);
}
