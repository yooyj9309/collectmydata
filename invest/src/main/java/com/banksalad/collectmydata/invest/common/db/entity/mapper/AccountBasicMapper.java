package com.banksalad.collectmydata.invest.common.db.entity.mapper;

import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.invest.account.dto.AccountBasic;
import com.banksalad.collectmydata.invest.common.db.entity.AccountBasicEntity;
import com.banksalad.collectmydata.invest.publishment.account.dto.AccountBasicResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.CURRENCY_KRW;

@Mapper(uses = BigDecimalMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountBasicMapper {

  @Mappings(
      value = {
          @Mapping(target = "withholdingsAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "creditLoanAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "mortgageAmt", qualifiedByName = "BigDecimalScale3"),
      }
  )
  AccountBasicEntity dtoToEntity(AccountBasic accountBasic);

  @Mappings(
      value = {
          @Mapping(target = "withholdingsAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "creditLoanAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "mortgageAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "currencyCode", defaultValue = CURRENCY_KRW)
      }
  )
  AccountBasicResponse entityToResponseDto(AccountBasicEntity accountBasicEntity);
}
