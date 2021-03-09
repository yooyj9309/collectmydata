package com.banksalad.collectmydata.insu.common.db.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.InsuranceBasicEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceBasicHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InsuranceBasicHistoryMapper {


  @Mapping(target = "id", ignore = true)
  InsuranceBasicHistoryEntity toHistoryEntity(InsuranceBasicEntity entity);
}
