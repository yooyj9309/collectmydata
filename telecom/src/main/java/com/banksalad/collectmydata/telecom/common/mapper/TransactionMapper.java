package com.banksalad.collectmydata.telecom.common.mapper;

import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;
import com.banksalad.collectmydata.telecom.common.db.entity.TransactionEntity;
import com.banksalad.collectmydata.telecom.telecom.dto.TelecomTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface TransactionMapper {

  @Mappings(
      value = {
          @Mapping(target = "paidAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  TransactionEntity dtoToEntity(TelecomTransaction telecomTransaction);
}
