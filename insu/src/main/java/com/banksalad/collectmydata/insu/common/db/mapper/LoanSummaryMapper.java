package com.banksalad.collectmydata.insu.common.db.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.LoanSummaryEntity;
import com.banksalad.collectmydata.insu.common.dto.LoanSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper
public interface LoanSummaryMapper {

  @Mappings(
      value = {
          @Mapping(target = "basicSearchTimestamp", ignore = true),
          @Mapping(target = "detailSearchTimestamp", ignore = true),
          @Mapping(target = "transactionSyncedAt", ignore = true)

      }
  )
  void merge(LoanSummary loanSummary, @MappingTarget LoanSummaryEntity loanSummaryEntity);

  LoanSummary entityToDto(LoanSummaryEntity entity);
}
