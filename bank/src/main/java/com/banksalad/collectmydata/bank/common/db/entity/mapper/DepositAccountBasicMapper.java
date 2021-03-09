package com.banksalad.collectmydata.bank.common.db.entity.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountBasicEntity;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountBasic;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(uses = BigDecimalMapper.class)
public interface DepositAccountBasicMapper {

  @Mappings(value = {
      @Mapping(target = "rspCode", ignore = true),
      @Mapping(target = "rspMsg", ignore = true),
      @Mapping(target = "searchTimestamp", ignore = true),
      @Mapping(target = "commitAmt", qualifiedByName = "BigDecimalScale3"),
      @Mapping(target = "monthlyPaidInAmt", qualifiedByName = "BigDecimalScale3")
  })
  DepositAccountBasic entityToDto(DepositAccountBasicEntity entity);

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
