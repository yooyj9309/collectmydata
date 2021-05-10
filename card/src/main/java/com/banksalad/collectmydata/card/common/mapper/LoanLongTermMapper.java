package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.common.db.entity.LoanLongTermEntity;
import com.banksalad.collectmydata.card.loan.dto.LoanLongTerm;
import com.banksalad.collectmydata.card.publishment.userbase.dto.LoanLongTermPublishment;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface LoanLongTermMapper {

  @Mappings(
      value = {
          @Mapping(target = "loanAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "intRate", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "intAmt", qualifiedByName = "BigDecimalScale3"),
      }
  )
  LoanLongTermEntity dtoToEntity(LoanLongTerm loanLongTerm);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mappings(
      value = {
          @Mapping(target = "loanAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "intRate", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "intAmt", qualifiedByName = "BigDecimalScale3"),
      }
  )
  LoanLongTerm entityToDto(LoanLongTermEntity loanLongTermEntity);

  LoanLongTermPublishment entityToPublishmentDto(LoanLongTermEntity loanLongTermEntity);
}
