package com.banksalad.collectmydata.insu.car.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;

import java.util.List;

public interface CarInsuranceService {

  List<CarInsurance> listCarInsurances(ExecutionContext executionContext, String organizationCode,
      List<InsuranceSummary> insuranceSummaries);
}
