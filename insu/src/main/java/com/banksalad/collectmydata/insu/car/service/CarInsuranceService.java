package com.banksalad.collectmydata.insu.car.service;

import com.banksalad.collectmydata.insu.car.dto.CarInsurance;

import java.time.LocalDateTime;
import java.util.List;

public interface CarInsuranceService {

  List<CarInsurance> listCarInsurances(long banksaladUserId, String organizationId, String insuNum);

  void updateTransactionSyncedAt(long banksaladUserId, String organizationId, CarInsurance carInsurance,
      LocalDateTime syncedAt);

  void updateTransactionResponseCode(long banksaladUserId, String organizationId, CarInsurance carInsurance,
      String responseCode);
}
