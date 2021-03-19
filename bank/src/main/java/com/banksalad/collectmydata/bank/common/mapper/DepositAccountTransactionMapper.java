package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountTransactionEntity;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountTransaction;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface DepositAccountTransactionMapper {

  @Mappings(
      value = {
          @Mapping(target = "transAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  DepositAccountTransactionEntity dtoToEntity(DepositAccountTransaction depositAccountTransaction);
}
