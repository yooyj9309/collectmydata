package com.banksalad.collectmydata.insu.car.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class CarInsuranceServiceImpl implements CarInsuranceService {

  @Override
  public List<CarInsurance> listCarInsurances(ExecutionContext executionContext, String organizationCode,
      List<InsuranceSummary> insuranceSummaries) {
    return null;
  }
}
