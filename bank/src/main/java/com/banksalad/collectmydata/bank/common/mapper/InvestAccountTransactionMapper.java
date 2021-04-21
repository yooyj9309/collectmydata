package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountTransactionEntity;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountTransaction;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountTransactionResponse;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.CURRENCY_KRW;

@Mapper(uses = BigDecimalMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvestAccountTransactionMapper {

  @Mappings(
      value = {
          @Mapping(target = "baseAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "transFundNum", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "transAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  InvestAccountTransactionEntity dtoToEntity(InvestAccountTransaction investAccountTransaction);

  @Mapping(target = "currencyCode", defaultValue = CURRENCY_KRW)
  InvestAccountTransactionResponse entityToResponseDto(InvestAccountTransactionEntity investAccountTransactionEntity);
}
