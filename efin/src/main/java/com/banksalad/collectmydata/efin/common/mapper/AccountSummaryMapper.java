package com.banksalad.collectmydata.efin.common.mapper;

import com.banksalad.collectmydata.efin.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.efin.summary.dto.AccountSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountSummaryMapper {

  @Mappings(
      value = {
          @Mapping(target = "balanceSearchTimestamp", ignore = true),
          @Mapping(target = "chargeSearchTimestamp", ignore = true),
          @Mapping(target = "transactionSyncedAt", ignore = true),
          @Mapping(target = "prepaidTransactionSyncedAt", ignore = true)
      }
  )
  void mergeDtoToEntity(AccountSummary accountSummary, @MappingTarget AccountSummaryEntity entity);

  AccountSummary entityToDto(AccountSummaryEntity accountSummaryEntity);
}
