package com.banksalad.collectmydata.invest.common.db.entity.mapper;

import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.invest.account.dto.AccountProduct;
import com.banksalad.collectmydata.invest.common.db.entity.AccountProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = BigDecimalMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountProductMapper {

  @Mappings(
      value = {
          @Mapping(target = "purchaseAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "evalAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "paidInAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "withdrawalAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "rcvAmt", qualifiedByName = "BigDecimalScale3"),
      }
  )
  AccountProduct entityToDto(AccountProductEntity accountProductEntity);

  @Mappings(
      value = {
          @Mapping(target = "purchaseAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "evalAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "paidInAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "withdrawalAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "rcvAmt", qualifiedByName = "BigDecimalScale3"),
      }
  )
  AccountProductEntity dtoToEntity(AccountProduct accountProduct);
}
