package com.banksalad.collectmydata.insu.publishment.car;

import com.banksalad.collectmydata.insu.publishment.car.dto.CarInsurancePublishmentResponse;
import com.banksalad.collectmydata.insu.publishment.car.dto.CarInsuranceTransactionPublishmentResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface CarInsurancePublishmentService {

  List<CarInsurancePublishmentResponse> getCarInsuranceResponses(long banksaladUserId, String organizationId);

  List<CarInsuranceTransactionPublishmentResponse> getCarInsuranceTransactionResponses(long banksaladUserId,
      String organizationId, String insuNum, LocalDateTime createdAt, int limit);
}
