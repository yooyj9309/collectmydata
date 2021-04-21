package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.InvestAccountDetailEntity;
import com.banksalad.collectmydata.bank.invest.dto.InvestAccountDetail;
import com.banksalad.collectmydata.bank.publishment.invest.dto.InvestAccountDetailResponse;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import static com.banksalad.collectmydata.finance.common.constant.FinanceConstant.CURRENCY_KRW;

@Mapper(uses = BigDecimalMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvestAccountDetailMapper {

  @Mappings(
      value = {
          @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "evalAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "invPrincipal", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "fundNum", qualifiedByName = "BigDecimalScale3"),
      }
  )
  InvestAccountDetailEntity dtoToEntity(InvestAccountDetail investAccountDetail);

  @Mapping(target = "currencyCode", defaultValue = CURRENCY_KRW)
  InvestAccountDetailResponse entityToResponseDto(InvestAccountDetailEntity investAccountDetailEntity);
}
