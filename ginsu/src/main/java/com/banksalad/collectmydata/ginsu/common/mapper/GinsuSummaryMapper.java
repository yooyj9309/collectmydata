package com.banksalad.collectmydata.ginsu.common.mapper;

import com.banksalad.collectmydata.ginsu.common.db.entity.GinsuSummaryEntity;
import com.banksalad.collectmydata.ginsu.summary.dto.GinsuSummary;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GinsuSummaryMapper {

  void mergeDtoToEntity(GinsuSummary ginsuSummary, @MappingTarget GinsuSummaryEntity entity);

  GinsuSummary entityToDto(GinsuSummaryEntity entity);
}
