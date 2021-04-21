package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountDetailEntity;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountDetail;
import com.banksalad.collectmydata.bank.publishment.deposit.dto.DepositAccountDetailResponse;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.CURRENCY_KRW;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface DepositAccountDetailMapper {

  @Mappings(
      value = {
          @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "withdrawableAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "offeredRate", qualifiedByName = "BigDecimalScale5"),
      }
  )
  DepositAccountDetailEntity dtoToEntity(DepositAccountDetail depositAccountDetail);

  @Mapping(target = "currencyCode", defaultValue = CURRENCY_KRW)
  DepositAccountDetailResponse entityToResponseDto(DepositAccountDetailEntity depositAccountDetailEntity);
}
