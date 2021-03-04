package com.banksalad.collectmydata.capital.common.db.entity.mapper;

import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionEntity;
import com.banksalad.collectmydata.capital.common.db.entity.AccountTransactionInterestEntity;
import com.banksalad.collectmydata.capital.loan.dto.LoanAccountTransactionInterest;

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
      // Data types of `transDtime` between two are quite different so advice an explicit formatting.
      @Mapping(source = "loanAccountTransactionInterest.intStartDate", target = "intStartDate", dateFormat = "yyyyMMdd"),
      @Mapping(source = "loanAccountTransactionInterest.intEndDate", target = "intEndDate", dateFormat = "yyyyMMdd"),
      // Must ignore id from `accountTransactionEntity`.
      @Mapping(target = "id", ignore = true)
  })
  void updateEntityFromDto(AccountTransactionEntity accountTransactionEntity,
      int intNo, LoanAccountTransactionInterest loanAccountTransactionInterest,
      @MappingTarget AccountTransactionInterestEntity accountTransactionInterestEntity);
}
