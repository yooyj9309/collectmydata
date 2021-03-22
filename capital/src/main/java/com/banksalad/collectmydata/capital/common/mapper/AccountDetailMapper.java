package com.banksalad.collectmydata.capital.common.mapper;

import com.banksalad.collectmydata.capital.account.dto.AccountDetail;
import com.banksalad.collectmydata.capital.account.dto.GetAccountDetailResponse;
import com.banksalad.collectmydata.capital.common.db.entity.AccountDetailEntity;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = {BigDecimalMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountDetailMapper {

  @Mappings(value = {
      @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3"),
      @Mapping(target = "loanPrincipal", qualifiedByName = "BigDecimalScale3")
  })
  AccountDetailEntity dtoToEntity(AccountDetail accountDetail);

  @Mappings(value = {
      @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3"),
      @Mapping(target = "loanPrincipal", qualifiedByName = "BigDecimalScale3")
  })
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  AccountDetail entityToDto(AccountDetailEntity accountDetailEntity);
}
