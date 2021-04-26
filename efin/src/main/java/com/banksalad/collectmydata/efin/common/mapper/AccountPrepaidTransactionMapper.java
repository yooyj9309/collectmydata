package com.banksalad.collectmydata.efin.common.mapper;

import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.efin.account.dto.AccountPrepaidTransaction;
import com.banksalad.collectmydata.efin.common.db.entity.AccountPrepaidTransactionEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface AccountPrepaidTransactionMapper {

  @Mappings(
      value = {
          @Mapping(target = "transAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  AccountPrepaidTransactionEntity dtoToEntity(AccountPrepaidTransaction accountPrepaidTransaction);
}
