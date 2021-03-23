package com.banksalad.collectmydata.insu.car;

import org.springframework.stereotype.Component;

import com.banksalad.collectmydata.common.collect.execution.ExecutionContext;
import com.banksalad.collectmydata.common.util.ObjectComparator;
import com.banksalad.collectmydata.finance.api.accountinfo.AccountInfoResponseHelper;
import com.banksalad.collectmydata.finance.api.accountinfo.dto.AccountResponse;
import com.banksalad.collectmydata.finance.common.constant.FinanceConstant;
import com.banksalad.collectmydata.insu.car.dto.CarInsurance;
import com.banksalad.collectmydata.insu.car.dto.GetCarInsuranceResponse;
import com.banksalad.collectmydata.insu.common.db.entity.CarInsuranceEntity;
import com.banksalad.collectmydata.insu.common.mapper.CarInsuranceHistoryMapper;
import com.banksalad.collectmydata.insu.common.mapper.CarInsuranceMapper;
import com.banksalad.collectmydata.insu.common.db.repository.CarInsuranceHistoryRepository;
import com.banksalad.collectmydata.insu.common.db.repository.CarInsuranceRepository;
import com.banksalad.collectmydata.insu.common.service.InsuranceSummaryService;
import com.banksalad.collectmydata.insu.summary.dto.InsuranceSummary;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CarInsuranceResponseHelper implements AccountInfoResponseHelper<InsuranceSummary, List<CarInsurance>> {

  private final InsuranceSummaryService insuranceSummaryService;
  private final CarInsuranceRepository carInsuranceRepository;
  private final CarInsuranceHistoryRepository carInsuranceHistoryRepository;

  private final CarInsuranceMapper carInsuranceMapper = Mappers.getMapper(CarInsuranceMapper.class);
  private final CarInsuranceHistoryMapper carInsuranceHistoryMapper = Mappers
      .getMapper(CarInsuranceHistoryMapper.class);

  @Override
  public List<CarInsurance> getAccountFromResponse(AccountResponse accountResponse) {
    return ((GetCarInsuranceResponse) accountResponse).getCarInsurances();
  }

  @Override
  public void saveAccountAndHistory(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      List<CarInsurance> carInsurances) {

    for (CarInsurance carInsurance : carInsurances) {
      /* mapping car insurance dto to entity */
      CarInsuranceEntity carInsuranceEntity = carInsuranceMapper.dtoToEntity(carInsurance);

      carInsuranceEntity.setBanksaladUserId(executionContext.getBanksaladUserId());
      carInsuranceEntity.setOrganizationId(executionContext.getOrganizationId());
      carInsuranceEntity.setSyncedAt(executionContext.getSyncStartedAt());
      carInsuranceEntity.setInsuNum(insuranceSummary.getInsuNum());

      /* load existing car insurance entity */
      CarInsuranceEntity existingCarInsuranceEntity = carInsuranceRepository
          .findByBanksaladUserIdAndOrganizationIdAndInsuNumAndCarNumber(
              executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
              insuranceSummary.getInsuNum(), carInsurance.getCarNumber())
          .orElse(null);

      /* copy PK for update */
      if (existingCarInsuranceEntity != null) {
        carInsuranceEntity.setId(existingCarInsuranceEntity.getId());
      }

      /* upsert car insurance and history entity */
      if (!ObjectComparator
          .isSame(carInsuranceEntity, existingCarInsuranceEntity, FinanceConstant.ENTITY_EXCLUDE_FIELD)) {
        carInsuranceRepository.save(carInsuranceEntity);
        carInsuranceHistoryRepository.save(carInsuranceHistoryMapper.toHistoryEntity(carInsuranceEntity));
      }
    }
  }

  @Override
  public void saveSearchTimestamp(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      long searchTimestamp) {
    insuranceSummaryService
        .updateCarSearchTimestamp(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            insuranceSummary, searchTimestamp);
  }

  @Override
  public void saveResponseCode(ExecutionContext executionContext, InsuranceSummary insuranceSummary,
      String responseCode) {
    insuranceSummaryService
        .updateCarResponseCode(executionContext.getBanksaladUserId(), executionContext.getOrganizationId(),
            insuranceSummary, responseCode);
  }
}
