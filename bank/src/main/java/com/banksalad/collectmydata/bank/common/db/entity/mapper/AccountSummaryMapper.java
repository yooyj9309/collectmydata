package com.banksalad.collectmydata.bank.common.db.entity.mapper;

import com.banksalad.collectmydata.bank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.bank.common.dto.AccountSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper
public interface AccountSummaryMapper {

  @Mappings(
      value = {
          @Mapping(target = "basicSearchTimestamp", ignore = true),
          @Mapping(target = "detailSearchTimestamp", ignore = true),
          @Mapping(target = "transactionFromDate", ignore = true),
          @Mapping(target = "createdAt", ignore = true),
          @Mapping(target = "createdBy", ignore = true),
          @Mapping(target = "updatedAt", ignore = true),
          @Mapping(target = "updatedBy", ignore = true),
          @Mapping(target = "id", ignore = true),
          @Mapping(target = "syncedAt", ignore = true),
          @Mapping(target = "isForeignDeposit", source = "accountSummary.foreignDeposit")
      }
  )
  void merge(AccountSummary accountSummary, @MappingTarget AccountSummaryEntity entity);

  @Mappings(
      value = {@Mapping(target = "foreignDeposit", source = "isForeignDeposit")}
  )
  AccountSummary entityToDto(AccountSummaryEntity entity);
}
