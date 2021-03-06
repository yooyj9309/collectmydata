package com.banksalad.collectmydata.ginsu.common.mapper;

import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceBasicEntity;
import com.banksalad.collectmydata.ginsu.common.db.entity.InsuranceBasicHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InsuranceBasicHistoryMapper {

  @Mapping(target = "id", ignore = true)
  InsuranceBasicHistoryEntity entityToHistoryEntity(InsuranceBasicEntity insuranceBasicEntity,
      @MappingTarget InsuranceBasicHistoryEntity insuranceBasicHistoryEntity);

}
