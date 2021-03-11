package com.banksalad.collectmydata.bank.common.db.entity.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountTransactionEntity;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountTransaction;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = BigDecimalMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvestAccountTransactionMapper {

  @Mappings(value = {
      @Mapping(target = "baseAmt", qualifiedByName = "BigDecimalScale3"),
      @Mapping(target = "transFundNum", qualifiedByName = "BigDecimalScale3"),
      @Mapping(target = "transAmt", qualifiedByName = "BigDecimalScale3"),
      @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3")
  })
  InvestAccountTransaction entityToDto(InvestAccountTransactionEntity investAccountTransactionEntity);

  @Mappings(
      value = {
          @Mapping(target = "baseAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "transFundNum", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "transAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  InvestAccountTransactionEntity dtoToEntity(InvestAccountTransaction investAccountTransaction);
}
