package com.banksalad.collectmydata.insu.car.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.car.dto.Car;
import com.banksalad.collectmydata.insu.common.dto.InsuranceSummary;

import java.util.List;

public interface CarService {

  List<Car> listCars(ExecutionContext executionContext, String organizationCode,
      List<InsuranceSummary> insuranceSummaries);
}
