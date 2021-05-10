package com.banksalad.collectmydata.card.common.mapper;

import com.banksalad.collectmydata.card.common.db.entity.CardSummaryEntity;
import com.banksalad.collectmydata.card.publishment.summary.dto.CardSummaryPublishment;
import com.banksalad.collectmydata.card.summary.dto.CardSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardSummaryMapper {

  @Mappings(
      value = {
          @Mapping(target = "searchTimestamp", ignore = true),
          @Mapping(target = "approvalDomesticTransactionSyncedAt", ignore = true),
          @Mapping(target = "approvalOverseasTransactionSyncedAt", ignore = true)
      }
  )
  void mergeDtoToEntity(CardSummary cardSummary, @MappingTarget CardSummaryEntity cardSummaryEntity);

  CardSummary entityToDto(CardSummaryEntity cardSummaryEntity);

  CardSummaryPublishment entityToPublishmentDto(CardSummaryEntity cardSummaryEntity);

}
