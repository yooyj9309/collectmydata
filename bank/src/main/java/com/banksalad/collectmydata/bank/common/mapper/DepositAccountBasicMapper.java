package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountBasicEntity;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountBasic;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface DepositAccountBasicMapper {

  @Mappings(
      value = {
          @Mapping(target = "commitAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "monthlyPaidInAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  DepositAccountBasicEntity dtoToEntity(DepositAccountBasic depositAccountBasic);
}
