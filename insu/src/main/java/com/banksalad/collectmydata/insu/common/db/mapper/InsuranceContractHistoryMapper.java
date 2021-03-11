package com.banksalad.collectmydata.insu.common.db.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.InsuranceContractEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsuranceContractHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InsuranceContractHistoryMapper {

  @Mapping(target = "id", ignore = true)
  InsuranceContractHistoryEntity toHistoryEntity(InsuranceContractEntity entity);
}
