package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.common.db.entity.LoanShortTermEntity;
import com.banksalad.collectmydata.card.loan.dto.LoanShortTerm;
import com.banksalad.collectmydata.card.publishment.userbase.dto.LoanShortTermPublishment;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface LoanShortTermMapper {

  @Mappings(
      value = {
          @Mapping(target = "loanAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "intRate", qualifiedByName = "BigDecimalScale3"),
      }
  )
  LoanShortTermEntity dtoToEntity(LoanShortTerm loanShortTerm);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mappings(
      value = {
          @Mapping(target = "loanAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "intRate", qualifiedByName = "BigDecimalScale3"),
      }
  )
  LoanShortTerm entityToDto(LoanShortTermEntity loanShortTermEntity);

  LoanShortTermPublishment entityToPublishmentDto(LoanShortTermEntity loanShortTermEntity);
}
