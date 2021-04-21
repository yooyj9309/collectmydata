package com.banksalad.collectmydata.telecom.common.mapper;

import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.telecom.common.db.entity.TelecomPaidTransactionEntity;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomPaidTransaction;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface TelecomPaidTransactionMapper {

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mappings(
      value = {
          @Mapping(target = "transAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  TelecomPaidTransactionEntity dtoToEntity(TelecomPaidTransaction telecomPaidTransaction);
}
