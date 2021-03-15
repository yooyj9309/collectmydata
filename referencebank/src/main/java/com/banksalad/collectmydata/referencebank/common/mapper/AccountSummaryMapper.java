package com.banksalad.collectmydata.referencebank.common.mapper;

import com.banksalad.collectmydata.referencebank.common.db.entity.AccountSummaryEntity;
import com.banksalad.collectmydata.referencebank.summaries.dto.AccountSummary;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper
public interface AccountSummaryMapper {

  @Mappings(
      value = {
          @Mapping(target = "id", ignore = true),
          @Mapping(target = "banksaladUserId", ignore = true),
          @Mapping(target = "organizationId", ignore = true),
          @Mapping(target = "syncedAt", ignore = true),
          @Mapping(target = "basicSearchTimestamp", ignore = true),
          @Mapping(target = "detailSearchTimestamp", ignore = true),
          @Mapping(target = "transactionSyncedAt", ignore = true),
          @Mapping(target = "createdAt", ignore = true),
          @Mapping(target = "createdBy", ignore = true),
          @Mapping(target = "updatedAt", ignore = true),
          @Mapping(target = "updatedBy", ignore = true),
          @Mapping(target = "isConsent", source = "consent"),
          @Mapping(target = "isForeignDeposit", source = "foreignDeposit"),
      }
  )
  void mergeDtoToEntity(AccountSummary accountSummary, @MappingTarget AccountSummaryEntity entity);

  @Mappings(
      value = {
          @Mapping(target = "consent", source = "isConsent"),
          @Mapping(target = "foreignDeposit", source = "isForeignDeposit"),
      }
  )
  AccountSummary entityToDto(AccountSummaryEntity entity);
}
