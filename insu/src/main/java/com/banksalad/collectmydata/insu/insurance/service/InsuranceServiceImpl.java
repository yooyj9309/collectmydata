package com.banksalad.collectmydata.insu.insurance.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceBasic;
import com.banksalad.collectmydata.insu.insurance.dto.InsuranceContract;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class InsuranceServiceImpl implements InsuranceService {

  @Override
  public List<InsuranceBasic> listInsuranceBasics(ExecutionContext executionContext, String organizationCode,
      List<InsuranceSummary> insuranceSummaries) {
    return null;
  }

  @Override
  public List<InsuranceContract> listInsuranceContracts(ExecutionContext executionContext, String organizationCode,
      List<InsuranceBasic> insuranceBasics) {
    return null;
  }
}
