package com.banksalad.collectmydata.insu.common.db.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.InsurancePaymentEntity;
import com.banksalad.collectmydata.insu.common.db.entity.InsurancePaymentHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface InsurancePaymentHistoryMapper {

  @Mapping(target = "id", ignore = true)
  InsurancePaymentHistoryEntity toInsurancePaymentHistoryEntityFrom(InsurancePaymentEntity insurancePaymentEntity);
}
