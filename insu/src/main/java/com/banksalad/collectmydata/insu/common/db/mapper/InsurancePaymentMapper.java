package com.banksalad.collectmydata.insu.common.db.mapper;

import com.banksalad.collectmydata.insu.common.db.entity.InsurancePaymentEntity;
import com.banksalad.collectmydata.insu.insurance.dto.GetInsurancePaymentResponse;
import com.banksalad.collectmydata.insu.insurance.dto.InsurancePayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InsurancePaymentMapper {

  InsurancePaymentEntity toInsurancePaymentEntityFrom(GetInsurancePaymentResponse insurancePaymentResponse);

  InsurancePayment toInsurancePaymentFrom(InsurancePaymentEntity insurancePaymentEntity);
}
