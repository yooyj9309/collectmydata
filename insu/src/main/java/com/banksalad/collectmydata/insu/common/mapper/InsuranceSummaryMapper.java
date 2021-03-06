package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.insu.publishment.summary.dto.InsuranceSummaryPublishmentResponse;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InsuranceSummaryMapper {

  @Mappings(
      value = {
          @Mapping(target = "basicSearchTimestamp", ignore = true),
          @Mapping(target = "basicResponseCode", ignore = true),
          @Mapping(target = "carSearchTimestamp", ignore = true),
          @Mapping(target = "paymentSearchTimestamp", ignore = true),
          @Mapping(target = "transactionSyncedAt", ignore = true),
      }
  )
  void mergeDtoToEntity(InsuranceSummary insuranceSummary, @MappingTarget InsuranceSummaryEntity entity);

  InsuranceSummary entityToDto(InsuranceSummaryEntity entity);

  InsuranceSummaryPublishmentResponse entityToPublishmentDto(InsuranceSummaryEntity insuranceSummaryEntity);
}
