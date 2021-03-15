package com.banksalad.collectmydata.referencebank.common.mapper;


import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.referencebank.common.db.entity.DepositAccountBasicEntity;
import com.banksalad.collectmydata.referencebank.deposit.dto.DepositAccountBasic;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(uses = BigDecimalMapper.class)
public interface DepositAccountBasicMapper {
  
  @Mappings(
      value = {
          @Mapping(target = "id", ignore = true),
          @Mapping(target = "banksaladUserId", ignore = true),
          @Mapping(target = "organizationId", ignore = true),
          @Mapping(target = "accountNum", ignore = true),
          @Mapping(target = "seqno", ignore = true),
          @Mapping(target = "syncedAt", ignore = true),
          @Mapping(target = "issueDate", source = "issueDate", dateFormat = "yyyyMMdd"),
          @Mapping(target = "expDate", source = "expDate", dateFormat = "yyyyMMdd"),
          @Mapping(target = "commitAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "monthlyPaidInAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  DepositAccountBasicEntity dtoToEntity(DepositAccountBasic depositAccountBasic);
}
