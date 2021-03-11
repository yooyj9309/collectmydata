package com.banksalad.collectmydata.capital.common.db.entity.mapper;

import com.banksalad.collectmydata.capital.common.db.entity.OperatingLeaseTransactionEntity;
import com.banksalad.collectmydata.capital.common.dto.AccountSummary;
import com.banksalad.collectmydata.capital.oplease.dto.OperatingLeaseTransaction;
import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = {BigDecimalMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OperatingLeaseTransactionMapper {

  @Mappings(
      value = {
          @Mapping(target = "id", ignore = true),
          @Mapping(target = "transactionYearMonth", ignore = true),
          @Mapping(source = "context.syncStartedAt", target = "syncedAt")
      }
  )
  void merge(ExecutionContext context, AccountSummary accountSummary, OperatingLeaseTransaction response,
      @MappingTarget OperatingLeaseTransactionEntity entity);
}
