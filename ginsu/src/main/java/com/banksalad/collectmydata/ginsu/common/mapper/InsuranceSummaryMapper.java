package com.banksalad.collectmydata.ginsu.common.mapper;

import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceSummaryEntity;
import com.banksalad.collectmydata.ginsu.summary.dto.InsuranceSummary;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InsuranceSummaryMapper {

  void mergeDtoToEntity(InsuranceSummary insuranceSummary, @MappingTarget InsuranceSummaryEntity entity);

  InsuranceSummary entityToDto(InsuranceSummaryEntity entity);
}
