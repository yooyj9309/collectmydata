package com.banksalad.collectmydata.insu.car.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.car.dto.Car;
import com.banksalad.collectmydata.insu.car.dto.CarTransaction;

import java.util.List;

public interface CarTransactionService {

  List<CarTransaction> listCarTransactions(ExecutionContext executionContext, String organizationCode,
      List<Car> cars);
}
