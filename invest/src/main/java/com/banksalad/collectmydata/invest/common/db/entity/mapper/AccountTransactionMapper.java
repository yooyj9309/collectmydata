package com.banksalad.collectmydata.invest.common.db.entity.mapper;

import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.invest.account.dto.AccountTransaction;
import com.banksalad.collectmydata.invest.common.db.entity.AccountTransactionEntity;
import com.banksalad.collectmydata.invest.publishment.account.dto.AccountTransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.CURRENCY_KRW;

@Mapper(uses = BigDecimalMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountTransactionMapper {

  @Mappings(
      value = {
          @Mapping(target = "baseAmt", qualifiedByName = "BigDecimalScale4"),
          @Mapping(target = "transAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "settleAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  AccountTransactionEntity dtoToEntity(AccountTransaction accountTransaction);

  @Mappings(
      value = {
          @Mapping(target = "baseAmt", qualifiedByName = "BigDecimalScale4"),
          @Mapping(target = "transAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "settleAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "currencyCode", defaultValue = CURRENCY_KRW)
      }
  )
  AccountTransactionResponse entityToResponseDto(AccountTransactionEntity accountTransactionEntity);
}
