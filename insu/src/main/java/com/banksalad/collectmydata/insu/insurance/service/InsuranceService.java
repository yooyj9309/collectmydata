package com.banksalad.collectmydata.insu.insurance.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceBasic;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceContract;

import java.util.List;

public interface InsuranceService {

  List<InsuranceBasic> listInsuranceBasics(ExecutionContext executionContext, String organizationCode,
      List<InsuranceSummary> insuranceSummaries);

  List<InsuranceContract> listInsuranceContracts(ExecutionContext executionContext, String organizationCode,
      List<InsuranceBasic> insuranceBasics);
}
