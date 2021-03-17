package com.banksalad.collectmydata.bank.common.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.DepositAccountDetailEntity;
import com.banksalad.collectmydata.bank.deposit.dto.DepositAccountDetail;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(uses = BigDecimalMapper.class)
public interface DepositAccountDetailMapper {

  @Mappings(
      value = {
          @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "withdrawableAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "offeredRate", qualifiedByName = "BigDecimalScale5"),
      }
  )
  DepositAccountDetail entityToDto(DepositAccountDetailEntity depositAccountDetailEntity);

  @Mappings(
      value = {
          @Mapping(target = "id", ignore = true),
          @Mapping(target = "banksaladUserId", ignore = true),
          @Mapping(target = "organizationId", ignore = true),
          @Mapping(target = "accountNum", ignore = true),
          @Mapping(target = "seqno", ignore = true),
          @Mapping(target = "syncedAt", ignore = true),
          @Mapping(target = "balanceAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "withdrawableAmt", qualifiedByName = "BigDecimalScale3"),
          @Mapping(target = "offeredRate", qualifiedByName = "BigDecimalScale5"),
      }
  )
  DepositAccountDetailEntity dtoToEntity(DepositAccountDetail depositAccountDetail);
}
