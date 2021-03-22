package com.banksalad.collectmydata.capital.common.mapper;

import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseTransactionEntity;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransaction;
import com.banksalad.collectmydata.common.mapper.BigDecimalMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BigDecimalMapper.class)
public interface OperatingLeaseTransactionMapper {

  @Mappings(
      value = {
          @Mapping(target = "transAmt", qualifiedByName = "BigDecimalScale3")
      }
  )
  OperatingLeaseTransactionEntity dtoToEntity(OperatingLeaseTransaction operatingLeaseTransaction,
      @MappingTarget OperatingLeaseTransactionEntity operatingLeaseTransactionEntity);
}
