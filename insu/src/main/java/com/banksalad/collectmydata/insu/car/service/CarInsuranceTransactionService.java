package com.banksalad.collectmydata.insu.car.service;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.car.dto.CarInsuranceTransaction;

import java.util.List;

public interface CarInsuranceTransactionService {

  List<CarInsuranceTransaction> listCarInsuranceTransactions(ExecutionContext executionContext, String organizationCode,
      List<CarInsurance> carInsurances);
}
