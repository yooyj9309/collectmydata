package com.banksalad.collectmydata.insu.common.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.InsurancePaymentEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsurancePaymentHistoryEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InsurancePaymentHistoryMapper {

  @Mapping(target = "id", ignore = true)
  InsurancePaymentHistoryEntity entityToHistoryEntity(InsurancePaymentEntity insurancePaymentEntity,
      @MappingTarget InsurancePaymentHistoryEntity insurancePaymentHistoryEntity);
}
