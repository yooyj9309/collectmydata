package com.banksalad.collectmydata.insu.car.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.car.dto.Car;
import com.banksalad.collectmydata.insu.car.dto.CarTransaction;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class CarTransactionServiceImpl implements CarTransactionService {

  @Override
  public List<CarTransaction> listCarTransactions(ExecutionContext executionContext, String organizationCode,
      List<Car> cars) {
    return null;
  }
}
