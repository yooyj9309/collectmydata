package com.banksalad.collectmydata.insu.car.service;

import org.springframework.stereotype.Service;

import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.common.db.repository.CarInsuranceRepository;
import com.banksalad.collectmydata.insu.common.mapper.CarInsuranceMapper;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarInsuranceServiceImpl implements CarInsuranceService {

  private final CarInsuranceRepository carInsuranceRepository;

  private final CarInsuranceMapper carInsuranceMapper = Mappers.getMapper(CarInsuranceMapper.class);

  @Override
  public List<CarInsurance> listCarInsurances(long banksaladUserId, String organizationId, String insuNum) {
    return carInsuranceRepository.findByBanksaladUserIdAndOrganizationIdAndInsuNum(banksaladUserId, organizationId, insuNum)
        .stream()
        .map(carInsuranceMapper::entityToDto)
        .collect(Collectors.toList());
  }

  @Override
  public void updateTransactionSyncedAt(long banksaladUserId, String organizationId, CarInsurance carInsurance,
      LocalDateTime syncedAt) {
    // TODO : wooody92 - DB error logging, when response is more than one
    carInsuranceRepository.findByBanksaladUserIdAndOrganizationIdAndInsuNumAndCarNumber(
        banksaladUserId, organizationId, carInsurance.getInsuNum(), carInsurance.getCarNumber())
    .ifPresent(carInsuranceEntity -> {
      carInsuranceEntity.setTransactionSyncedAt(syncedAt);
      carInsuranceRepository.save(carInsuranceEntity);
    });
  }

  @Override
  public void updateTransactionResponseCode(long banksaladUserId, String organizationId, CarInsurance carInsurance,
      String responseCode) {
    // TODO : wooody92 - DB error logging, when response is more than one
    carInsuranceRepository.findByBanksaladUserIdAndOrganizationIdAndInsuNumAndCarNumber(
        banksaladUserId, organizationId, carInsurance.getInsuNum(), carInsurance.getCarNumber())
        .ifPresent(carInsuranceEntity -> {
          carInsuranceEntity.setTransactionResponseCode(responseCode);
          carInsuranceRepository.save(carInsuranceEntity);
        });
  }
}
