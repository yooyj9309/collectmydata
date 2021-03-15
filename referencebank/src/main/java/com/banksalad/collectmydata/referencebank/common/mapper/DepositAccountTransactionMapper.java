package com.banksalad.collectmydata.referencebank.common.mapper;

import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.referencebank.common.db.entity.DepositAccountTransactionEntity;
import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccountTransaction;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(uses = BigDecimalMapper.class)
public interface DepositAccountTransactionMapper {

  @Mappings(value = {
      @Mapping(target = "transAmt", qualifiedByName = "BigDecimalScale3"),
      @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3")
  })
  DepositAccountTransaction entityToDto(DepositAccountTransactionEntity depositAccountTransactionEntity);

  @Mappings(
      value = {
          @Mapping(target = "id", ignore = true),
          @Mapping(target = "transactionYearMonth", ignore = true),
          @Mapping(target = "syncedAt", ignore = true),
          @Mapping(target = "banksaladUserId", ignore = true),
          @Mapping(target = "organizationId", ignore = true),
          @Mapping(target = "accountNum", ignore = true),
          @Mapping(target = "seqno", ignore = true),
          @Mapping(target = "currencyCode", ignore = true),
          @Mapping(target = "uniqueTransNo", ignore = true),
          @Mapping(target = "transAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  DepositAccountTransactionEntity dtoToEntity(DepositAccountTransaction depositAccountTransaction);
}
