package com.banksalad.collectmydata.efin.common.mapper;

import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.efin.account.dto.AccountCharge;
import com.banksalad.collectmydata.efin.common.db.entity.AccountChargeEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface AccountChargeMapper {

  @Mappings(
      value = {
          @Mapping(target = "chargeBaseAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "chargeAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  AccountChargeEntity dtoToEntity(AccountCharge accountCharge);
}
