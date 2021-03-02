package com.banksalad.collectmydata.insu.car.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.car.dto.Car;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class CarServiceImpl implements CarService {

  @Override
  public List<Car> listCars(ExecutionContext executionContext, String organizationCode,
      List<InsuranceSummary> insuranceSummaries) {
    return null;
  }
}
