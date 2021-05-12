package com.banksalad.collectmydata.insu.publishment.insurance;

import com.banksalad.collectmydata.insu.publishment.insurance.dto.InsuranceBasicPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.insurance.dto.InsuranceContractPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.insurance.dto.InsurancePaymentPublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.insurance.dto.InsuranceTransactionPublishmentResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface InsurancePublishmentService {

  List<InsuranceBasicPublishmentResponse> getInsuranceBasicResponses(long banksaladUserId, String organizationId);

  List<InsuranceContractPublishmentResponse> getInsuranceContractResponses(long banksaladUserId, String organizationId);

  List<InsurancePaymentPublishmentResponse> getInsurancePaymentResponses(long banksaladUserId, String organizationId);

  List<InsuranceTransactionPublishmentResponse> getInsuranceTransactionResponses(long banksaladUserId,
      String organizationId, String insuNum, LocalDateTime createdAt, int limit);
}
