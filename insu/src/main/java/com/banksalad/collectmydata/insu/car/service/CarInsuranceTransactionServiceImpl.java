package com.banksalad.collectmydata.insu.car.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.car.dto.CarInsuranceTransaction;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class CarInsuranceTransactionServiceImpl implements CarInsuranceTransactionService {

  @Override
  public List<CarInsuranceTransaction> listCarInsuranceTransactions(ExecutionContext executionContext,
      String organizationCode,
      List<CarInsurance> carInsurances) {
    return null;
  }
}
