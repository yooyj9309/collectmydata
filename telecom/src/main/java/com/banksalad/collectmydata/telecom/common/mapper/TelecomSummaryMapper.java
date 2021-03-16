package com.banksalad.collectmydata.telecom.common.mapper;

import com.banksalad.collectmydata.telecom.common.db.entity.TelecomSummaryEntity;
import com.banksalad.collectmydata.telecom.summary.dto.TelecomSummary;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TelecomSummaryMapper {

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void mergeDtoToEntity(TelecomSummary telecomSummary, @MappingTarget TelecomSummaryEntity telecomSummaryEntity);

  TelecomSummary entityToDto(TelecomSummaryEntity telecomSummaryEntity);
}
