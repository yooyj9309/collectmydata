package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountBasicEntity;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountBasic;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountBasicResponse;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.CURRENCY_KRW;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface DepositAccountBasicMapper {

  @Mappings(
      value = {
          @Mapping(target = "commitAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "monthlyPaidInAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  DepositAccountBasicEntity dtoToEntity(DepositAccountBasic depositAccountBasic);

  @Mapping(target = "currencyCode", defaultValue = CURRENCY_KRW)
  DepositAccountBasicResponse entityToResponseDto(DepositAccountBasicEntity depositAccountBasicEntity);
}
